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

package cs.ut.ui.controllers.training

import cs.ut.engine.item.ModelParameter
import cs.ut.engine.item.Property
import cs.ut.logging.NirdizatiLogger
import cs.ut.ui.NirdizatiGrid
import cs.ut.ui.UIComponent
import cs.ut.ui.adapters.AdvancedModeAdapter
import cs.ut.ui.adapters.GeneratorArgument
import cs.ut.ui.adapters.PropertyValueAdapter
import cs.ut.ui.components.CheckBoxGroup
import cs.ut.ui.controllers.TrainingController
import cs.ut.util.HYPER_PARAM_CONT
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.CheckEvent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Hlayout
import org.zkoss.zul.Vlayout

class AdvancedModeController(gridContainer: Vlayout) : AbstractModeController(gridContainer), ModeController, UIComponent {
    private val log = NirdizatiLogger.getLogger(AdvancedModeController::class, getSessionId())

    private val rowProvider = AdvancedModeAdapter()
    private val grid: NirdizatiGrid<GeneratorArgument> = NirdizatiGrid(rowProvider)
    private val hyperParamsContainer: Hlayout =
            Executions.getCurrent().desktop.components.first { it.id == HYPER_PARAM_CONT } as Hlayout
    private var hyperParameters: MutableMap<ModelParameter, MutableList<Property>> = mutableMapOf()

    init {
        log.debug("Initializing advanced mode controller")
        gridContainer.getChildren<Component>().clear()

        rowProvider.fields = grid.fields
        grid.generate(parameters
                .entries
                .map { GeneratorArgument(it.key, it.value) })

        gridContainer.appendChild(grid)
        grid.fields
                .asSequence()
                .forEach {
                    val cont = it.control as CheckBoxGroup
                    cont.applyToAll(this::generateListener)
                }

        grid.sclass = "max-height max-width"
        grid.hflex = "min"
        grid.vflex = "min"
        log.debug("Finished grid initialization")
    }

    /**
     * Create listener when to show hyper parameter grid when checkbox is checked
     */
    private fun generateListener(checkBox: Checkbox) {
        val parameter = checkBox.getValue<ModelParameter>()
        if (TrainingController.LEARNER == parameter.type) {
            hyperParameters[parameter] = mutableListOf()
        }

        val handleLearner = { p: ModelParameter, e: CheckEvent ->
            if (e.isChecked) {
                hyperParameters[p]?.addAll(p.properties)
            } else {
                hyperParameters[p]?.removeAll(p.properties)
            }
        }

        val handleOther = { p: ModelParameter, e: CheckEvent ->
            hyperParameters.values.forEach {
                if (e.isChecked) {
                    it.addAll(p.properties)
                } else {
                    it.removeAll(p.properties)
                }
            }
        }

        checkBox.addEventListener(Events.ON_CHECK, { e ->
            e as CheckEvent
            log.debug("$this value changed, regenerating grid")
            when (parameter.type) {
                TrainingController.LEARNER -> handleLearner(parameter, e)
                else -> handleOther(parameter, e)
            }
            if (parameter.properties.isNotEmpty()) {
                hyperParamsContainer.getChildren<Component>().clear()
                hyperParameters.entries.forEach { it.generateGrid() }
            }
        })
    }

    /**
     * Generate a single grid based on given entry
     */
    private fun Map.Entry<ModelParameter, List<Property>>.generateGrid() {
        if (value.size < 2) return

        log.debug("Key: $key -> value: $value")

        val propGrid = NirdizatiGrid(PropertyValueAdapter)
        propGrid.setColumns(
                listOf(
                        NirdizatiGrid.ColumnArgument(key.type + "." + key.id, "min"),
                        NirdizatiGrid.ColumnArgument(flex = "min")
                )
        )

        propGrid.generate(value)
        propGrid.vflex = "1"
        propGrid.sclass = "hyper-grid"

        hyperParamsContainer.appendChild(propGrid)
    }

    /**
     * Is given grid valid
     *
     * @return is all the data correct in the grid
     */
    override fun isValid(): Boolean {
        var isValid = grid.validate()

        hyperParamsContainer.getChildren<Component>().forEach {
            isValid = (it as NirdizatiGrid<*>).validate()
        }

        return isValid
    }

    /**
     * Gather values from this controller
     *
     * @return map of collected values
     */
    @Suppress("UNCHECKED_CAST")
    override fun gatherValues(): Map<String, List<ModelParameter>> {
        val gathered = grid.gatherValues()

        val hyperParams = mutableMapOf<String, Map<String, Any>>()
        hyperParamsContainer.getChildren<Component>().forEach {
            it as NirdizatiGrid<*>
            hyperParams[it.columns.firstChild.id] = it.gatherValues()
        }

        hyperParams.forEach { k, v ->
            val keys = k.split(".")
            if (keys.size > 1) {
                val params = gathered[keys[0]] as List<*>
                val copy = mutableListOf<ModelParameter>()
                params.forEach { param ->
                    param as ModelParameter
                    val parameter = param.copy()
                    if (parameter.id == keys[1]) {
                        parameter.properties.clear()
                        v.forEach {
                            parameter.properties.add(Property(it.key, "", it.value.toString(), -1.0, -1.0))
                        }
                    }
                    copy.add(parameter)
                }
                gathered[keys[0]] = copy
            }
        }

        return gathered as Map<String, List<ModelParameter>>
    }

    override fun preDestroy() {
        hyperParamsContainer.getChildren<Component>().clear()
    }

}