/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package cs.ut.ui.controllers

import com.google.common.html.HtmlEscapers
import cs.ut.configuration.ConfigFetcher
import cs.ut.engine.JobManager
import cs.ut.engine.LogManager
import cs.ut.engine.item.ModelParameter
import cs.ut.exceptions.Left
import cs.ut.exceptions.Right
import cs.ut.jobs.Job
import cs.ut.jobs.SimulationJob
import cs.ut.json.JSONService
import cs.ut.json.TrainingConfiguration
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.ModelParamProvider
import cs.ut.ui.UIComponent
import cs.ut.ui.controllers.modal.ParameterModalController.Companion.FILE
import cs.ut.ui.controllers.modal.ParameterModalController.Companion.IS_RECREATION
import cs.ut.ui.controllers.training.AdvancedModeController
import cs.ut.ui.controllers.training.BasicModeController
import cs.ut.ui.controllers.training.ModeController
import cs.ut.util.Algorithm
import cs.ut.util.Cookies
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.UPLOADED_FILE
import org.zkoss.util.resource.Labels
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.SelectEvent
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.A
import org.zkoss.zul.Button
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Combobox
import org.zkoss.zul.Comboitem
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Radio
import org.zkoss.zul.Radiogroup
import org.zkoss.zul.Vlayout
import org.zkoss.zul.Window
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import org.apromore.model.LogSummaryType
import org.apromore.plugin.portal.predictortraining.PortalPlugin
import org.deckfour.xes.model.XAttribute
import org.deckfour.xes.model.XAttributeBoolean
import org.deckfour.xes.model.XAttributeContinuous
import org.deckfour.xes.model.XAttributeDiscrete
import org.deckfour.xes.model.XAttributeLiteral
import org.deckfour.xes.model.XAttributeTimestamp
import org.deckfour.xes.model.XEvent
import org.deckfour.xes.model.XLog
import org.deckfour.xes.model.XTrace
import org.json.JSONArray
import org.json.JSONObject

class TrainingController : SelectorComposer<Component>(), Redirectable, UIComponent {

    private val log = NirdizatiLogger.getLogger(TrainingController::class, getSessionId())

    companion object {
        const val LEARNER = "learner"
        const val ENCODING = "encoding"
        const val BUCKETING = "bucketing"
        const val PREDICTION = "predictiontype"

        private val log = NirdizatiLogger.getLogger(TrainingController::class, "static")

        private val configNode by ConfigFetcher("defaultValues")
        val DEFAULT: Double = configNode.values.first { it.identifier == "minimum" }.value()
        val AVERAGE = configNode.values.first { it.identifier == "average" }.value

        const val START_TRAINING = "startTraining"
        const val GENERATE_DATASET = "genDataSetParam"

        fun convertXLogToDatasetParams(xlog: XLog): JSONObject {

            var params = JSONObject()

            val isCategoreal = { attribute: XAttribute -> attribute is XAttributeBoolean || attribute is XAttributeLiteral && attribute.getKey() != "concept:name" }
            val isNumerical = { attribute: XAttribute -> attribute is XAttributeContinuous || attribute is XAttributeDiscrete }
            val isSpurious = { attribute: XAttribute -> hashSetOf("time", "variant", "variant-index").contains(attribute.getKey()) }

            val staticAttributes = findStaticAttributes(xlog).filter { !isSpurious(it) }
            val dynamicAttributes = findDynamicAttributes(xlog).filter { !isSpurious(it) }

            params.put("dynamic_cat_cols", dynamicAttributes.filter { isCategoreal(it) }.map { it.getKey() }.toSet())
            params.put("dynamic_num_cols", dynamicAttributes.filter { isNumerical(it) }.map { it.getKey() }.toSet())
            params.put("static_cat_cols", staticAttributes.filter { isCategoreal(it) }.map { it.getKey() }.toSet())
            params.put("static_num_cols", staticAttributes.filter { isNumerical(it) }.map { it.getKey() }.toSet())

            params.put("case_id_col", "case_id")
            params.put("activity_col", "concept:name")
            params.put("timestamp_col", "time:timestamp")
            //params.put("ignore", "?")
            //params.put("future_values", "?")

            return params
        }

        fun convertXLogToCSV(xlog: XLog, file: OutputStream) {
                log.info("Exporting log")
                val writer = PrintWriter(file)

                // Write header
                val headers = ArrayList<String>()
                headers.add("case_id")
                val dynamicAttributes = findDynamicAttributes(xlog)

                for (attribute: String in dynamicAttributes.map { it.getKey() }.toSet()) headers.add(attribute)

                writeCSV(headers, writer)

                // Write content
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

                for (trace: XTrace in xlog) {
                    if (trace.size < 1) {
                        continue
                    }

                    for (event: XEvent in trace) {
                        val items = ArrayList<String>()
                        items.add(trace.getAttributes().get("concept:name").toString());
                        for (attributeName: String in headers.subList(1, headers.lastIndex)) {
                            val attribute: XAttribute? = event.getAttributes().get(attributeName)
                            if (attribute == null) {
                                items.add("")
                        
                            } else if (attribute is XAttributeBoolean) {
                                items.add(java.lang.Boolean.toString(attribute.getValue()))

                            } else if (attribute is XAttributeContinuous) {
                                items.add(java.lang.Double.toString(attribute.getValue()))

                            } else if (attribute is XAttributeDiscrete) {
                                items.add(java.lang.Long.toString(attribute.getValue()))

                            } else if (attribute is XAttributeLiteral) {
                                items.add(attribute.getValue())

                            } else if (attribute is XAttributeTimestamp) {
                                items.add(dateFormat.format(attribute.getValue()))

                            } else {
                                throw UnsupportedOperationException("Attribute with unsupported type: ${attribute.getKey()}")
                            }
                        }

                        writeCSV(items, writer)
                    }
                }
                log.info("Exported log")

                writer.close()
        }

        /**
         * Format a series of strings into a comma separated line.
         *
         * This attempts to conform to the de-facto standard behavior of Excel w.r.t escaping commas and quotation marks.
         *
         * @throws IllegalArgumentException if any of the <var>values</var> contains a comma
         * @see https://stackoverflow.com/a/21749399
         */
        fun writeCSV(values: Iterable<String>, writer: PrintWriter) {
            val i = values.iterator()
            if (i.hasNext()) {
                do {
                    val value = i.next()
                    val needsQuotation = value.indexOf(",") != -1 || value.indexOf("\n") != -1 || value.indexOf("\"") != -1
                    if (needsQuotation) {
                        writer.print("\"")
                        writer.print((value as java.lang.String).replaceAll("\"", "\"\""))
                        writer.print("\"")

                    } else {
                        writer.print(value)
                    }
                    if (i.hasNext()) {
                        writer.print(",")
                    }
                } while (i.hasNext())
            }
            writer.println()
        }

        /**
         * @param xlog  any XES log
         * @return the global trace attributes of xlog if they exist, or if those are missing, the attributes of the first trace
         */
        private fun findStaticAttributes(xlog: XLog): List<XAttribute> =
                ArrayList(xlog.get(0).getAttributes().values)
/*            if (!xlog.getGlobalTraceAttributes().isEmpty()) {
                xlog.getGlobalTraceAttributes()
            } else {
                ArrayList(xlog.get(0).getAttributes().values)
            }*/

        /**
         * @param xlog  any XES log
         * @return the global event attributes of xlog if they exist, or if those are missing, the attributes of the first event
         */
        private fun findDynamicAttributes(xlog: XLog): List<XAttribute> =
                xlog[0].flatMap { event -> event.attributes.values }

    }


    @Wire
    private lateinit var clientLogs: Combobox

    @Wire
    private lateinit var predictionType: Combobox

    @Wire
    private lateinit var advancedMode: Checkbox

    @Wire
    private lateinit var gridContainer: Vlayout

    @Wire
    private lateinit var genDataSetParam: A

    @Wire
    private lateinit var radioGroup: Radiogroup

    @Wire
    private lateinit var avgRadio: Radio

    @Wire
    private lateinit var customRadio: Radio

    @Wire
    private lateinit var customBox: Doublebox

    private lateinit var gridController: ModeController

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)

        initClientLogs()
        if (initPredictions()) {
            genDataSetParam.isDisabled = false
            gridController = BasicModeController(gridContainer, getLogFileName())
        }
    }

    /**
     * Get log file name from combo box
     *
     * @return log file name from the client log combo
     */
    private fun getLogFileName(): String = "${(clientLogs.selectedItem.getValue() as LogSummaryType).getId()}"

    /**
     * @param logSummary  a log summary of an .XES file obtained from Apromore's event log service
     * @return  the corresponding locally-cached .CSV file
     */
    private fun fileForLogSummary(logSummary: LogSummaryType): File {
        val logDirectory = File(DirectoryConfiguration.dirPath(Dir.USER_LOGS))
        log.info("Creating temp file from logSummary ID ${logSummary.getId()} in ${logDirectory}")
        return File(logDirectory, "${logSummary.getId()}.csv")
    }

    /**
     * Init predictions combo box
     *
     * @return whether initialization was successful
     */
    private fun initPredictions(): Boolean {
        customBox.isDisabled = true
        avgRadio.isDisabled = true
        customRadio.isDisabled = true

        predictionType.items.clear()
        log.debug("Cleared prediction type items")

        val params: List<ModelParameter> = ModelParamProvider().getPredictionTypes()
        log.debug("Received ${params.size} prediciton types")

        if (clientLogs.itemCount == 0) {
            predictionType.isDisabled = true
            return false
        }

        val logFile = fileForLogSummary(clientLogs.selectedItem.getValue() as LogSummaryType)

        val res = JSONService
                .getTrainingData(logFile.nameWithoutExtension)

        val dataSetColumns: List<String> =
                when (res) {
                    is Right -> res.result.getAllColumns()
                    is Left -> {
                        log.debug("Error occurred when fetching dataset columns", res.error)
                        listOf()
                    }
                }

        params.forEach {
            val item: Comboitem = predictionType.appendItem(NirdizatiTranslator.localizeText("${it.type}.${it.id}"))
            val modelParam = it.copy()
            item.setValue(modelParam)

            if (modelParam.id == Algorithm.OUTCOME.value) {
                modelParam.setUpRadioButtons()
            }
        }

        dataSetColumns.forEach {
            val modelParameter = ModelParameter(it, it, PREDICTION, true, mutableListOf())
            modelParameter.translate = false
            val item: Comboitem = predictionType.appendItem(modelParameter.id)
            item.setValue(modelParameter)
        }

        predictionType.addEventListener(Events.ON_SELECT, { e ->
            e as SelectEvent<*, *>
            val param = (e.selectedItems.first() as Comboitem).getValue() as ModelParameter
            log.debug("Prediction type model changed to $param")
            if (param.id == Algorithm.OUTCOME.value) {
                customBox.isDisabled = false
                avgRadio.isDisabled = false
                customRadio.isDisabled = false
                log.debug("Prediciton type is ${param.id} generating radio buttons")
            } else {
                log.debug("Clearing thresholdContainer")
                customBox.isDisabled = true
                avgRadio.isDisabled = true
                customRadio.isDisabled = true
            }
        })

        predictionType.selectedItem = predictionType.items[0]
        predictionType.isReadonly = true
        return true
    }

    /**
     * Set up radio buttons for threshold selection
     */
    private fun ModelParameter.setUpRadioButtons() {
        avgRadio.setValue(this.parameter.toDouble())
        customRadio.setValue(DEFAULT)
        customBox.setValue(DEFAULT)

        customBox.addEventListener(Events.ON_CHANGE, { _ ->
            log.debug("New value for custom threshold ${customBox.value}")
            val res: Double? = customBox.value

            if (res == null || res <= 0) {
                customRadio.setValue(DEFAULT)
                customBox.setValue(DEFAULT)
                customBox.errorMessage = NirdizatiTranslator.localizeText("threshold.custom_error", 0)
            } else {
                customBox.clearErrorMessage()
                customBox.setValue(res)
                customRadio.setValue(res)
            }
        })
    }

    /**
     * Load client logs into the combo
     */
    private fun initClientLogs() {
        val files = LogManager.getAllAvailableLogs()
        log.debug("Found ${files.size} log files")

        val escaper = HtmlEscapers.htmlEscaper()
        files.forEach {
            val item = clientLogs.appendItem(escaper.escape(it.getName()))
            item.setValue(it)
        }

        if (clientLogs.itemCount > 0) {
            clientLogs.selectedItem = clientLogs.items[0]
        } else {
            clientLogs.isDisabled = true
        }

        Executions.getCurrent().desktop.getAttribute(UPLOADED_FILE)?.apply {
            this as LogSummaryType
            clientLogs.items.forEach {
                if ((it.getValue() as LogSummaryType) == this) {
                    clientLogs.selectedItem = it
                }
            }
        }

        clientLogs.isReadonly = true

        clientLogs.addEventListener(Events.ON_SELECT, { _ ->
            switchMode()
            initPredictions()
        })


        // Disable start button if logs are not found so simulation can not be started
        val startButton = Executions.getCurrent().desktop.components.firstOrNull { it.id == START_TRAINING }
        startButton?.let {
            startButton as Button
            if (files.isEmpty()) {
                startButton.isDisabled = true
                advancedMode.isDisabled = true
            }
        }
    }

    @Listen("onCheck = #advancedMode")
    fun switchMode() {
        gridController = when (advancedMode.isChecked) {
            true -> {
                gridController.preDestroy()
                AdvancedModeController(gridContainer)
            }
            false -> {
                gridController.preDestroy()
                BasicModeController(gridContainer, getLogFileName())
            }
        }
    }

    @Listen("onClick = #startTraining")
    fun startTraining() {
        if (!gridController.isValid()) return

        val jobParameters = mutableMapOf<String, List<ModelParameter>>()
        jobParameters[PREDICTION] = listOf(predictionType.selectedItem.getValue())
        jobParameters.putAll(gridController.gatherValues())

        if (!jobParameters.validateParameters()) return

        val prediction: ModelParameter = jobParameters[PREDICTION]!!.first()

        if (prediction.id == Algorithm.OUTCOME.value) {
            val value = (radioGroup.selectedItem.getValue() as Double)
            prediction.parameter = if (value == -1.0) AVERAGE else value.toString()
        }

        val logSummary = clientLogs.selectedItem.getValue() as LogSummaryType
        val xlog = PortalPlugin.globalEventLogService!!.getXLog(logSummary.getId())

        val logDirectory = File(DirectoryConfiguration.dirPath(Dir.USER_LOGS))
        val logFile = File(logDirectory, "${logSummary.getId()}.csv")
        log.info("Converting log to CSV: ${logFile}")
        if (!logFile.isFile()) {
            convertXLogToCSV(xlog, FileOutputStream(logFile))
            log.info("Converted log to CSV")
        } else {
            log.info("Didn't need to convert log to CSV; already in cache")
        }

        val datasetParamsDirectory = File(DirectoryConfiguration.dirPath(Dir.DATA_DIR))
        val datasetParamsFile = File(datasetParamsDirectory, "${logSummary.getId()}.json")
        log.info("Extracting dataset parameters: ${datasetParamsFile}")
        if (!datasetParamsFile.isFile()) {
            val printWriter = PrintWriter(FileOutputStream(datasetParamsFile))
            printWriter.write(convertXLogToDatasetParams(xlog).toString())
            printWriter.close()
            log.info("Extracted dataset parameters")
        } else {
            log.info("Didn't need to extract dataset parameters; already in cache")
        }

        log.debug("Parameters are valid, calling script to train the model")
        val jobThread = Runnable {
            passJobs(jobParameters, logFile, logSummary.getName())
        }
        jobThread.run()
        log.debug("Job generation thread started")
    }

    /**
     * Pass jobs to job manager for execution
     *
     * @param jobParameters to generate jobs from
     * @param logFile a CSV-formatted log file
     */
    private fun passJobs(jobParameters: MutableMap<String, List<ModelParameter>>, logFile: File, logName: String) {
        log.debug("Generating jobs -> $jobParameters")
        val encodings = jobParameters[ENCODING]!!
        val bucketings = jobParameters[BUCKETING]!!
        val predictionTypes = jobParameters[PREDICTION]!!
        val learners = jobParameters[LEARNER]!!

        val jobs: MutableList<Job> = mutableListOf()
        encodings.forEach { encoding ->
            bucketings.forEach { bucketing ->
                learners.forEach { learner ->
                    predictionTypes.forEach { pred ->
                        val config = TrainingConfiguration(encoding, bucketing, learner, pred)
                        jobs.add(
                                SimulationJob(
                                        config,
                                        logFile,
                                        logName,
                                        Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
                                )
                        )
                    }
                }
            }
        }
        log.debug("Generated ${jobs.size} jobs")
        JobManager.deployJobs(
                Cookies.getCookieKey(Executions.getCurrent().nativeRequest),
                jobs
        )
    }

    /**
     * Validate that all the required parameters are present
     *
     * @return whether or not data is present
     */
    private fun Map<String, List<ModelParameter>>.validateParameters(): Boolean {
        var isValid = true
        var msg = ""

        if (this[ENCODING] == null) {
            msg += ENCODING
            isValid = false
        }

        if (this[BUCKETING] == null) {
            msg += if (msg == "") BUCKETING else ", $BUCKETING"
            isValid = false
        }

        if (this[LEARNER] == null) {
            msg += if (msg == "") LEARNER else ", $LEARNER"
            isValid = false
        }

        if (!isValid) {
            NirdizatiTranslator.showNotificationAsync(
                    Labels.getLabel("training.validation_failed", arrayOf(msg)),
                    Executions.getCurrent().desktop,
                    "error"
            )
        }

        return isValid
    }

    @Listen("onClick = #genDataSetParam")
    fun generateNewDatasetParams() {
        genDataSetParam.isDisabled = true
        log.debug("Started new dataset parameter generation for -> ${clientLogs.value}")
        val args = mapOf<String, Any>(FILE to fileForLogSummary(clientLogs.selectedItem.getValue() as LogSummaryType), IS_RECREATION to true)
        val window: Window = Executions.createComponents(
                "/views/modals/params.zul",
                self,
                args
        ) as Window
        if (self.getChildren<Component>().contains(window)) {
            window.doModal()
        }
    }
}
