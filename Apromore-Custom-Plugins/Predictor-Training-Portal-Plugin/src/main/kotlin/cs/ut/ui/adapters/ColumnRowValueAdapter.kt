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

import cs.ut.exceptions.Left
import cs.ut.exceptions.perform
import cs.ut.ui.FieldComponent
import cs.ut.ui.GridValueProvider
import cs.ut.util.COMP_ID
import cs.ut.util.IdentColumns
import cs.ut.util.NirdizatiTranslator
import org.zkoss.zul.Combobox
import org.zkoss.zul.Label
import org.zkoss.zul.Row

/**
 * Adapter that is used when generating data set parameter modal
 */
class ColumnRowValueAdapter(private val valueList: List<String>, private val identifiedCols: Map<String, String>) :
    GridValueProvider<String, Row> {

    override fun provide(data: String): Pair<FieldComponent, Row> {
        val noResource = "modals.param.no_resource"
        val row = Row()

        val label = Label(NirdizatiTranslator.localizeText("modals.param.$data"))
        label.setAttribute(COMP_ID, data)
        label.sclass = "param-modal-label"

        val comboBox = Combobox()

        val identified = identifiedCols[data]
        comboBox.isReadonly = true
        comboBox.setConstraint("no empty")

        valueList.forEach {
            val comboItem = comboBox.appendItem(it)
            comboItem.setValue(it)

            if (it == identified) comboBox.selectedItem = comboItem
        }

        // Add empty value as well if resource column is not present
        if (data == IdentColumns.RESOURCE.value) {
            comboBox.appendItem(NirdizatiTranslator.localizeText(noResource)).setValue("")
        }

        val res = perform { comboBox.selectedItem }
        when (res) {
            is Left -> comboBox.selectedItem = (comboBox.getItemAtIndex(0))
        }

        row.appendChild(label)
        row.appendChild(comboBox)

        return FieldComponent(label, comboBox) to row
    }
}