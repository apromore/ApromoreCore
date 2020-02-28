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

package cs.ut.ui.components

import org.zkoss.zul.impl.XulElement

abstract class ComponentGroup<T : XulElement> {
    val valid: Boolean
        get() = validator(components)

    val components: MutableList<T> = mutableListOf()

    lateinit var validator: (List<T>) -> Boolean

    fun addComponent(element: T) {
        components.add(element)
    }

    fun applyToAll(addition: (T) -> Unit) {
        components.forEach { component -> addition(component) }
    }
}