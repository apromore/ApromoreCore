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

package cs.ut.ui

import cs.ut.configuration.ConfigNode
import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.item.ModelParameter
import cs.ut.engine.item.Property
import cs.ut.logging.NirdizatiLogger
import cs.ut.ui.components.CheckBoxGroup
import cs.ut.ui.components.ComponentGroup
import cs.ut.ui.context.NirdizatiContextMenu.Companion.COMPONENT_VALUE
import cs.ut.util.COMP_ID
import cs.ut.util.GridColumns
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.PROPERTY
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Column
import org.zkoss.zul.Columns
import org.zkoss.zul.Combobox
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Grid
import org.zkoss.zul.Intbox
import org.zkoss.zul.Menupopup
import org.zkoss.zul.Row
import org.zkoss.zul.Rows
import org.zkoss.zul.impl.NumberInputElement
import java.io.File

/**
 * Data class that stores grid components for easy data collection
 */
data class FieldComponent(val label: Component, val control: Any)

/**
 * Custom ZK grid implementation that allows to generate grid with custom row providers
 */
class NirdizatiGrid<in T>(private val provider: GridValueProvider<T, Row>, private val namespace: String = "") : Grid(), UIComponent {
    private val log = NirdizatiLogger.getLogger(NirdizatiGrid::class, getSessionId())
    private val configNode = if (namespace.isNotBlank()) ConfigurationReader.findNode("grids/$namespace") else ConfigNode()
    var contextMenu: Menupopup? = null

    val fields = mutableListOf<FieldComponent>()

    init {
        appendChild(Rows())
    }

    /**
     * Generate grid rows using the data provided
     *
     * @param data to generate rows with
     * @param clear whether or not existing data should be cleared before appending new data
     */
    fun generate(data: Collection<T>, clear: Boolean = true, reversedInsert: Boolean = false) {
        log.debug("Row generation start with ${data.size} properties")
        val start = System.currentTimeMillis()

        if (clear) {
            rows.getChildren<Component>().clear()
            fields.clear()
        }

        generateRows(data.toMutableList(), rows, reversedInsert)

        val end = System.currentTimeMillis()
        log.debug("Row generation finished in ${end - start} ms")
    }

    /**
     * Column argument holder class
     * @param name column name that will be translated
     * @param flex for the column
     * @param comp comparators to use for column sorting
     */
    data class ColumnArgument(val name: String = "", val flex: String = "", val comp: GridComparator = Empty())

    /**
     * Set grid columnArguments.
     * @param columnArguments list of column arguments
     */
    fun setColumns(columnArguments: List<ColumnArgument>) {
        val cols = Columns()
        appendChild(cols)
        columnArguments.forEach {
            val column = if (it.name.isNotBlank()) Column(NirdizatiTranslator.localizeText(it.name)) else Column()
            column.id = it.name
            if (it.flex.isNotEmpty()) {
                column.hflex = it.flex
            }

            when (it.comp) {
                is ComparatorPair<*> -> {
                    column.sortAscending = it.comp.asc
                    column.sortDescending = it.comp.desc
                }

                is Empty -> Unit
            }

            cols.appendChild(column)
        }

        if (namespace.isNotBlank()) {
            log.debug("Namespace for grid -> $namespace")
            setSortingRules()
            cols.menupopup = "auto"
        }
    }

    private fun setSortingRules() {
        val sortable = configNode.childNodes.first { it.identifier == GridColumns.SORTABLE.value }
        val values = sortable.itemListValues()

        if (sortable.isEnabled()) {
            columns.getChildren<Column>().forEach {
                if (it.id in values) {
                    it.setSort("auto")
                }
            }
        }
    }

    private tailrec fun generateRows(data: MutableList<T>, rows: Rows, reversedInsert: Boolean) {
        if (data.isNotEmpty()) {
            val (field, row) = provider.provide(data.first())
            fields.add(field)
            if (contextMenu != null) {
                row.context = contextMenu!!.id
                row.setAttribute(COMPONENT_VALUE, data.first())
            }

            if (reversedInsert) {
                rows.insertBefore(row, rows.firstChild)
            } else {
                rows.appendChild(row)
            }

            generateRows(data.tail(), rows, reversedInsert)
        }
    }

    /**
     * Validate that data in the grid is correct according to component definitions
     */
    fun validate(): Boolean {
        val invalid = mutableListOf<Any>()
        validateFields(fields, invalid)
        return invalid.isEmpty()
    }

    private tailrec fun validateFields(fields: MutableList<FieldComponent>, invalid: MutableList<Any>) {
        if (fields.isNotEmpty()) {
            val comp = fields.first().control

            when (comp) {
                is Intbox -> if (comp.value == null || !isInLimits(comp)) {
                    if (!comp.hasAttribute(PROPERTY)) {
                        comp.errorMessage = NirdizatiTranslator.localizeText("training.validation.greater_than_zero")
                    } else {
                        setErrorMsg(comp)
                    }
                    invalid.add(comp)
                }
                is Doublebox -> if (comp.value == null || !isInLimits(comp)) {
                    if (!comp.hasAttribute(PROPERTY)) {
                        comp.errorMessage = NirdizatiTranslator.localizeText("training.validation.greater_than_zero")
                    } else {
                        setErrorMsg(comp)
                    }
                    invalid.add(comp)
                }

                is ComponentGroup<*> -> if (!comp.valid) invalid.add(comp)
            }
            validateFields(fields.tail(), invalid)
        }
    }

    private fun setErrorMsg(comp: NumberInputElement) {
        val prop = comp.getAttribute(PROPERTY) as Property

        if (prop.minValue != -1.0 && prop.maxValue != -1.0) {
            comp.errorMessage = NirdizatiTranslator.localizeText("training.validation.in_range", prop.minValue, prop.maxValue)
        } else if (prop.minValue != -1.0) {
            comp.errorMessage = NirdizatiTranslator.localizeText("training.validation.min_val", prop.minValue)
        } else {
            comp.errorMessage = NirdizatiTranslator.localizeText("training.validation.max_val", prop.maxValue)
        }
    }

    private fun isInLimits(comp: Component): Boolean {
        if (!comp.hasAttribute(PROPERTY)) return true

        val prop = comp.getAttribute(PROPERTY) as Property

        if (prop.maxValue == -1.0 && prop.minValue == -1.0) return true

        return when (comp) {
            is Intbox -> isInRange(comp.value, prop.minValue, prop.maxValue)
            is Doublebox -> isInRange(comp.value, prop.minValue, prop.maxValue)
            else -> throw UnsupportedOperationException("Operation not defined for class $comp")
        }
    }

    private fun <T> MutableList<T>.tail(): MutableList<T> = drop(1).toMutableList()

    /**
     * Gather values from the grid
     *
     * @return map with collected elements from the grid
     */
    fun gatherValues(): MutableMap<String, Any> {
        val valueMap = mutableMapOf<String, Any>()
        gatherValueFromFields(valueMap, fields)
        return valueMap
    }

    @Suppress("UNCHECKED_CAST")
    private tailrec fun gatherValueFromFields(valueMap: MutableMap<String, Any>, fields: MutableList<FieldComponent>) {
        if (fields.isNotEmpty()) {
            val field = fields.first().control
            val id = fields.first().label.getAttribute(COMP_ID) as String

            when (field) {
                is CheckBoxGroup -> {
                    field.gatherer = this::gatherFromCheckbox
                    field.gather(valueMap, id)
                }

                is Intbox -> valueMap[id] = field.value
                is Doublebox -> valueMap[id] = field.value
                is Combobox -> valueMap[id] = field.selectedItem.getValue()
                is Checkbox -> gatherFromCheckbox(field, valueMap, id)

            }
            gatherValueFromFields(valueMap, fields.tail())
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun gatherFromCheckbox(field: Checkbox, valueMap: MutableMap<String, Any>, id: String) {
        if (field.isChecked) {
            if (valueMap.containsKey(id)) {
                (valueMap[id] as MutableList<ModelParameter>).add(field.getValue())
            } else {
                valueMap[id] = mutableListOf<ModelParameter>()
                val params = valueMap[id]
                when (params) {
                    is MutableList<*> -> (params as MutableList<ModelParameter>).add(field.getValue())
                }
            }
        }
    }
}

/**
 * Whether given value is in specific range with default arguments
 */
fun isInRange(num: Number, min: Double = -1.0, max: Double = -1.0): Boolean {
    return if (min != -1.0 && max != -1.0) num.toDouble() in min..max
    else if (max != -1.0) num.toDouble() <= max
    else min <= num.toDouble()
}