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

package cs.ut.ui.context

import cs.ut.configuration.ConfigurationReader
import cs.ut.logging.NirdizatiLogger
import cs.ut.util.NirdizatiTranslator
import org.zkoss.zul.Menupopup


class MenuGenerator<T>(private val menuMode: MenuMode) {
    enum class MenuMode(val value: String) {
        VALIDATION("validation")
    }

    private val pkg = "cs.ut.ui.context.operations."

    fun generate(): Menupopup {
        val start = System.currentTimeMillis()

        log.debug("Generating context menu in mode $menuMode")
        val config = ConfigurationReader.findNode("context/${menuMode.value}")
        log.debug("Generating ${config.childNodes.size} operations")

        val menu = NirdizatiContextMenu<T>()
        menu.id = menuMode.value

        config.childNodes.forEach {
            val menuItem = NirdizatiContextMenu.NirdizatiContextMenuItem(
                    NirdizatiTranslator.localizeText(it.valueWithIdentifier("label").value),
                    Class.forName(pkg + it.valueWithIdentifier("operation").value))

            menuItem.id = it.identifier
            menuItem.iconSclass = it.valueWithIdentifier("icon").value
            menuItem.sclass = "n-menu-item"

            menu.appendChild(menuItem)
        }

        System.currentTimeMillis().apply {
            log.debug("Finished context menu generation in ${this - start} ms.")
        }

        menu.finalize()
        return menu
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(MenuGenerator::class)
    }
}