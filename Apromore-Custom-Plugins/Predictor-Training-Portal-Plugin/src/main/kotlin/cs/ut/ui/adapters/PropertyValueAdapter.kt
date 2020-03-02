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

import cs.ut.engine.item.Property
import cs.ut.exceptions.NirdizatiRuntimeException
import cs.ut.ui.FieldComponent
import cs.ut.ui.GridValueProvider
import cs.ut.util.COMP_ID
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.PROPERTY
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Doublebox
import org.zkoss.zul.Intbox
import org.zkoss.zul.Label
import org.zkoss.zul.Row
import org.zkoss.zul.impl.InputElement

/**
 * Used to generate hyper parameter grids in training view
 */
object PropertyValueAdapter : GridValueProvider<Property, Row> {

    override fun provide(data: Property): Pair<FieldComponent, Row> {
        val row = Row()

        val label = Label(NirdizatiTranslator.localizeText("property." + data.id))
        label.setAttribute(COMP_ID, data.id)
        val control = generateControl(data)

        row.appendChild(label)
        row.appendChild(control)

        return FieldComponent(label, control) to row
    }

    /**
     * Generate input component for given property
     *
     * @param prop to generate input component for
     *
     * @return input component corresponding to property definition
     */
    private fun generateControl(prop: Property): Component {
        val obj = Class.forName(prop.type).getConstructor().newInstance()

        when (obj) {
            is Intbox -> obj.value = prop.property.toInt()
            is Doublebox -> obj.setValue(prop.property.toDouble())
            else -> NirdizatiRuntimeException("Uknown element $prop")
        }

        obj as InputElement
        obj.setConstraint("no empty")
        obj.width = "60px"
        obj.setAttribute(PROPERTY, prop)

        return obj
    }
}