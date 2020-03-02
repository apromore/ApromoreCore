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

package cs.ut.ui.controllers.validation

import cs.ut.charts.Chart
import cs.ut.charts.ChartGenerator
import cs.ut.charts.MAE
import cs.ut.engine.JobManager
import cs.ut.jobs.SimulationJob
import cs.ut.logging.NirdizatiLogger
import cs.ut.ui.adapters.ComparisonAdapter
import cs.ut.ui.adapters.JobValueAdapter
import cs.ut.ui.adapters.ValidationViewAdapter
import cs.ut.ui.controllers.Redirectable
import cs.ut.util.NirdizatiDownloader
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.Page
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.SelectEvent
import org.zkoss.zk.ui.event.SerializableEventListener
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Cell
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Combobox
import org.zkoss.zul.Comboitem
import org.zkoss.zul.Label
import org.zkoss.zul.Row
import org.zkoss.zul.Rows
import org.zkoss.zul.Vbox

class SingleJobValidationController : SelectorComposer<Component>(), Redirectable {
    private val log = NirdizatiLogger.getLogger(SingleJobValidationController::class)
    private lateinit var job: SimulationJob
    private lateinit var charts: Map<String, List<Chart>>

    @Wire
    private lateinit var gridContainer: Vbox

    @Wire
    private lateinit var propertyRows: Rows

    @Wire
    private lateinit var selectionRows: Rows

    @Wire
    private lateinit var comboLayout: Vbox

    @Wire
    private lateinit var comparisonContainer: Vbox

    @Wire
    private lateinit var exportFile: Button

    @Wire
    private lateinit var exportAll: Button

    private var currentlySelected: String = ""

    var accuracyMode: String = ""

    private lateinit var currentChart: Chart

    @Wire
    private lateinit var compRows: Rows

    val checkBoxes = mutableListOf<Checkbox>()

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)
        job = Executions.getCurrent().getAttribute(JobValueAdapter.jobArg) as SimulationJob

        log.debug("Received job argument $job, initializing in read only mode")
        charts = ChartGenerator(job).getCharts().groupBy { it.javaClass.name }

        val provider = ComparisonAdapter(gridContainer, this)
        (listOf(job) +
                JobManager.findSimilarJobs(job))
                .map { provider.provide(it) }
                .forEach { compRows.appendChild(it.second) }

        generateReadOnlyMode()
    }

    /**
     * Listener - Set content to training mode when clicked
     */
    @Listen("onClick=#backToTraining")
    fun backToTraining() {
        setContent(Page.TRAINING.value, page)
    }

    /**
     * Listener - Set content to validation view when clicked
     */
    @Listen("onClick=#backToValidation")
    fun backToValidation() {
        setContent(Page.MODEL_OVERVIEW.value, page)
    }

    /**
     * Generate read only mode for this view
     */
    private fun generateReadOnlyMode() {
        propertyRows.appendChild(ValidationViewAdapter(null, gridContainer).provide(job, false).second)
        generateChartOptions()
    }

    /**
     * Generate menu to select charts
     */
    private fun generateChartOptions() {
        val row = Row()
        row.align = "center"
        charts.forEach { row.generateCell(it) }
        selectionRows.appendChild(row)
        Events.postEvent("onClick", row.getChildren<Component>().first { it.id == ACCURACY_COMPARISON }, null)
    }

    /**
     * Generate cell for each chart
     */
    private fun Row.generateCell(entry: Map.Entry<String, List<Chart>>) {
        val cell = Cell()
        val label = Label(NirdizatiTranslator.localizeText(entry.key))
        cell.id = entry.key
        cell.align = "center"
        cell.valign = "center"

        cell.addEventListener(Events.ON_CLICK, { _ ->
            selectionRows.getChildren<Row>().first().getChildren<Cell>().forEach { it.sclass = "val-cell" }
            cell.sclass = "val-cell selected-option"
            currentlySelected = entry.key
        })

        cell.addEventListener(
                Events.ON_CLICK,
                if (entry.value.size == 1) entry.value.first().generateListenerForOne() else entry.value.generateListenerForMany()
        )

        cell.appendChild(label)
        this.appendChild(cell)
    }

    /**
     * Generate listener for a single chart
     */
    private fun Chart.generateListenerForOne(): SerializableEventListener<Event> {
        return SerializableEventListener { _ ->
            removeChildren()
            comboLayout.parent.parent.isVisible = false
            this.render()
            currentChart = this
            setVisibility()
        }
    }

    /**
     * Set visibility of collapsible panels
     */
    private fun setVisibility() {
        comparisonContainer.parent.parent.isVisible = currentlySelected == ACCURACY_COMPARISON
        if (currentlySelected == ACCURACY_COMPARISON) {
            addDataSets()
        }
    }

    /**
     * Add data sets to the client view
     */
    private fun addDataSets() {
        checkBoxes
                .asSequence()
                .filter { it.isChecked && (it.getValue() as? SimulationJob)?.id != job.id }
                .forEach {
                    val value = it.getValue<SimulationJob>() as SimulationJob
                    ComparisonAdapter.addDataSet(value.id, ComparisonAdapter.getPayload(value, accuracyMode))
                }
    }

    /**
     * Remove children from combo layout
     */
    private fun removeChildren() {
        comboLayout.getChildren<Component>().clear()
    }

    /**
     * Create listener to handle many charts (such as prefix length based bucketing)
     */
    private fun List<Chart>.generateListenerForMany(): SerializableEventListener<Event> {
        return SerializableEventListener { _ ->
            removeChildren()

            comboLayout.parent.parent.isVisible = true

            var itemSet = false
            val comboBox = Combobox()
            comboBox.hflex = "max"
            this.forEach {
                val comboItem = comboBox.appendItem(NirdizatiTranslator.localizeText(it.getCaption()))
                comboItem.setValue(it)

                if (MAE == it.name) {
                    comboBox.selectedItem = comboItem
                    itemSet = true
                    accuracyMode = it.name
                }
            }

            if (!itemSet) comboBox.selectedItem = comboBox.items.first().apply {
                accuracyMode = (this.getValue() as Chart).name
            }

            comboBox.isReadonly = true
            comboBox.setConstraint("no empty")
            (comboBox.selectedItem.getValue() as Chart)
                    .apply {
                        this.render()
                        currentChart = this
                    }

            comboBox.addEventListener(
                    Events.ON_SELECT,
                    { e ->
                        (((e as SelectEvent<*, *>).selectedItems.first() as Comboitem).getValue() as Chart).apply {
                            accuracyMode = this.name
                            this.render()
                            currentChart = this
                            addDataSets()
                        }
                    })
            comboLayout.appendChild(comboBox)
            setVisibility()
        }
    }

    @Listen("onClick=#exportFile")
    fun exportFile() {
        NirdizatiDownloader.executeOnSingleFile(currentChart.file)
    }

    @Listen("onClick=#exportAll")
    fun exportAll() {
        NirdizatiDownloader.downloadFilesAsZip(job)
    }

    companion object {
        private const val ACCURACY_COMPARISON = "cs.ut.charts.LineChart"
    }
}