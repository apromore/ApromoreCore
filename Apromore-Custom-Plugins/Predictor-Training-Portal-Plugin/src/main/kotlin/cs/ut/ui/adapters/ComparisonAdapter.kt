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

import cs.ut.charts.ChartGenerator
import cs.ut.charts.LineChart
import cs.ut.jobs.SimulationJob
import cs.ut.ui.FieldComponent
import cs.ut.ui.GridValueProvider
import cs.ut.ui.controllers.validation.SingleJobValidationController
import cs.ut.util.NirdizatiTranslator
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.CheckEvent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.A
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Hlayout
import org.zkoss.zul.Label
import org.zkoss.zul.Row

/**
 * Adapter which is used when generating comparison grid in validation view
 */
class ComparisonAdapter(container: Component, private val controller: SingleJobValidationController) :
    GridValueProvider<SimulationJob, Row> {
    private val adapter = ValidationViewAdapter(null, container)

    private var first = true

    override fun provide(data: SimulationJob): Pair<FieldComponent, Row> {
        val checkBox = Checkbox().apply {
            isChecked = first
            isDisabled = first
            setValue(data)
            if (first) first = false

            addEventListener(Events.ON_CHECK, { e ->
                e as CheckEvent
                if (e.isChecked) {
                    addDataSet(data.id, getPayload(data, controller.accuracyMode))
                } else {
                    removeDataSet(data.id)
                }
            })
            controller.checkBoxes.add(this)
        }

        val row = with (Row()) {
            appendChild(Hlayout().apply {
                hflex = "1"
                vflex = "1"

                appendChild(checkBox)
                appendChild(Hlayout().apply {
                    sclass = "color-box c${data.id}"
                })
            })

            val config = data.configuration

            appendChild(Label(NirdizatiTranslator.localizeText("${config.bucketing.type}.${config.bucketing.id}")))
            appendChild(Label(NirdizatiTranslator.localizeText("${config.encoding.type}.${config.encoding.id}")))
            appendChild(Label(NirdizatiTranslator.localizeText("${config.learner.type}.${config.learner.id}")))
            appendChild(A().apply {
                adapter.loadTooltip(this, data)
            })
            this
        }

        return FieldComponent(Label(), checkBox) to row
    }

    companion object {
        /**
         * Get chart payload for a given job
         * @param job to get payload for
         * @param accuracyMode that is used in current controller
         *
         * @return payload for a chart
         */
        fun getPayload(job: SimulationJob, accuracyMode: String): String {
            return ChartGenerator(job).getCharts()
                .first { it::class.java == LineChart::class.java && it.name == accuracyMode }
                .payload
        }

        /**
         * Add data set on client side
         * @param label data set to add
         * @param payload data set payload which will be added
         */
        fun addDataSet(label: String, payload: String) {
            Clients.evalJavaScript("addDataSet('$label', '$payload')")
        }

        /**
         * Remove data set on client side
         * @param label data set to remove
         */
        fun removeDataSet(label: String) {
            Clients.evalJavaScript("removeDataSet('$label')")
        }
    }
}