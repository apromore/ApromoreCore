/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

import cs.ut.configuration.Value
import cs.ut.ui.FieldComponent
import cs.ut.ui.GridValueProvider
import cs.ut.util.COMP_ID
import cs.ut.util.NirdizatiTranslator
import org.zkoss.zul.Combobox
import org.zkoss.zul.Label
import org.zkoss.zul.Row

/**
 * Wrapper to hold the data
 * @param caption to use for the combo
 * @param values put inside the combo box
 * @param selected which option is selected
 */
data class ComboArgument(val caption: String, val values: List<Value>, val selected: String)

/**
 * Adapter used when generating data set parameters stage 2
 */
object ComboProvider : GridValueProvider<ComboArgument, Row> {
    override fun provide(data: ComboArgument): Pair<FieldComponent, Row> {
        val label = Label(data.caption)
        label.setAttribute(COMP_ID, data.caption)
        label.sclass = "display-block"

        val comboBox = Combobox()
        comboBox.sclass = "max-width max-height"
        data.values.forEach {
            val item = comboBox.appendItem(NirdizatiTranslator.localizeText(it.value))
            item.setValue(it.identifier)

            if (it.identifier == data.selected) {
                comboBox.selectedItem = item
            }
        }

        comboBox.isReadonly = true

        val row = Row()
        row.appendChild(label)
        row.appendChild(comboBox)

        return FieldComponent(label, comboBox) to row
    }
}