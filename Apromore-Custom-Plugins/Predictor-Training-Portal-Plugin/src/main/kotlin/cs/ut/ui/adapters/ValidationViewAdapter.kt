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

package cs.ut.ui.adapters

import cs.ut.configuration.ConfigFetcher
import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.item.Property
import cs.ut.jobs.Job
import cs.ut.jobs.SimulationJob
import cs.ut.json.TrainingConfiguration
import cs.ut.providers.Dir
import cs.ut.ui.FieldComponent
import cs.ut.ui.GridValueProvider
import cs.ut.ui.Navigator
import cs.ut.ui.adapters.JobValueAdapter.jobArg
import cs.ut.ui.context.operations.ExportToPortalOperation
import cs.ut.ui.controllers.validation.ValidationController
import cs.ut.util.GridColumns
import cs.ut.util.NirdizatiDownloader
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.Page
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.A
import org.zkoss.zul.Html
import org.zkoss.zul.Label
import org.zkoss.zul.Popup
import org.zkoss.zul.Row
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

/**
 * Used to generate metadata info about the job in validation views
 */
class ValidationViewAdapter(private val parentController: ValidationController?, private val container: Component?) :
        GridValueProvider<Job, Row> {

    override fun provide(data: Job): Pair<FieldComponent, Row> {
        return provide(data, true)
    }

    /**
     * Overloaded method that says whether redirection listener should be added to given row
     * @param data to generate row from
     * @param addRedirectListener should redirect listener be added
     *
     * @return row with generated data
     */
    fun provide(data: Job, addRedirectListener: Boolean = true): Pair<FieldComponent, Row> {
        data as SimulationJob
        val config = data.configuration

        val row = Row()
        with(row) {
            sclass = if (addRedirectListener) "pointer" else "no-hover-effect"
            align = "center"
            appendChild(Label(data.logName))
            appendChild(getLabel(config.outcome.toString()))
            appendChild(getLabel(config.bucketing.toString()))
            appendChild(getLabel(config.encoding.toString()))
            appendChild(getLabel(config.learner.toString()))
            appendChild(A().apply { loadTooltip(this, data) })
            appendChild(getLabel(timeFormat.format(Date.from(Instant.parse(data.startTime)))))
            appendChild(config.getEvaluationLabel())
            appendChild(A().apply {
                iconSclass = icons.valueWithIdentifier("download").value
                sclass = "n-download"
                addEventListener(Events.ON_CLICK, { _ -> ExportToPortalOperation(data).perform() })
            })

            if (addRedirectListener) {
                addEventListener(Events.ON_CLICK, { _ ->
                    Executions.getCurrent().setAttribute(jobArg, data)
                    parentController!!.setContent(Page.VALIDATION.value, parentController.page(),
                            Navigator.createParameters(Navigator.RequestParameter.JOB.value to data.id))
                })
            }

            setValue(data)
        }

        return FieldComponent(Label(), Label()) to row
    }

    private fun Double.format(): String = DecimalFormat(decimalFormat).format(this)

    private fun TrainingConfiguration.getEvaluationLabel(): Component =
            if (this.evaluation.metric == "") {
                Label("-")
            } else {
                Label("${this.evaluation.metric.toUpperCase()}: ${this.evaluation.value.format()}")
            }

    /**
     * Load tooltip for given job and attach to given element
     *
     * @param a to attach to
     * @param data to generate tooltip from
     */
    fun loadTooltip(a: A, data: SimulationJob) {
        a.iconSclass = icons.valueWithIdentifier("tooltip").value
        a.sclass = "validation-btn"
        a.vflex = "1"
        a.addEventListener(Events.ON_MOUSE_OVER, { _ ->
            a.desktop.components.firstOrNull { it.id == PROP_POPUP }?.detach()
            Popup().also {
                it.appendChild(Html(data.formTooltip()))
                it.id = PROP_POPUP
                container?.appendChild(it)
            }.open(a, "after_end ")
        })
        a.addEventListener(Events.ON_MOUSE_OUT, { _ ->
            a.desktop.components.asSequence()
                    .filter { it is Popup }
                    .forEach { (it as Popup).close() }
        })
    }

    /**
     * Create tooltip that contains info about hyper parameters for the job
     */
    private fun SimulationJob.formTooltip(): String {
        val parameters = mutableListOf<Property>().also { it.addAll(this.configuration.learner.properties) }

        return parameters.joinToString(
                separator = "<br/>",
                transform = { "<b>" + NirdizatiTranslator.localizeText("property.${it.id}") + "</b>: ${it.property}" }) + "<br/><br/>${this.id}"
    }

    /**
     * Helper to create label with content
     * @param str to be localized and appended to label
     *
     * @return label with localized text
     */
    private fun getLabel(str: String) = Label().apply {
        val labelText = NirdizatiTranslator.localizeText(str)
        this.value = if (labelText.contains(".")) labelText.split(".")[1] else labelText
    }

    companion object {
        const val PROP_POPUP = "propertyPopUpMenu"
        val timeFormat = SimpleDateFormat(ConfigurationReader.findNode("grids").valueWithIdentifier(GridColumns.TIMESTAMP.value).value)
        val icons by ConfigFetcher("iconClass")
        val decimalFormat = ConfigurationReader.findNode("grids").valueWithIdentifier("decimalFormat").value
    }
}
