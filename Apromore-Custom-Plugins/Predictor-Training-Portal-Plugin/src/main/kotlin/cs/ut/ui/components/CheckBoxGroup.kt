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

package cs.ut.ui.components

import cs.ut.logging.NirdizatiLogger
import org.zkoss.zul.Checkbox


class CheckBoxGroup(mode: Mode) : ComponentGroup<Checkbox>() {
    enum class Mode {
        ALL,
        ANY
    }

    lateinit var gatherer: (Checkbox, MutableMap<String, Any>, String) -> Unit

    init {
        validator = when (mode) {
            Mode.ALL -> { items ->
                items.asSequence()
                        .any(Checkbox::isChecked)
            }
            Mode.ANY -> { items ->
                items.asSequence()
                        .all(Checkbox::isChecked)
            }
        }

        log.debug("Validation mode set to $mode")
    }

    fun gather(valueMap: MutableMap<String, Any>, label: String) {
        components.asSequence()
                .forEach { component -> gatherer(component, valueMap, label) }
    }

    companion object {
        val log = NirdizatiLogger.getLogger(CheckBoxGroup::class)
    }
}