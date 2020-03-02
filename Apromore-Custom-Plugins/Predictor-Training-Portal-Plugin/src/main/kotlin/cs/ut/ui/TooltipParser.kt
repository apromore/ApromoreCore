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

import cs.ut.configuration.ConfigFetcher
import org.zkoss.zul.Html
import org.zkoss.zul.Popup

/**
 * Parses tooltips from configuration into objects
 */
class TooltipParser {
    private val configNode by ConfigFetcher("tooltip/tooltips")

    /**
     * Read tooltip from configuration with given id
     *
     * @param id of tooltip to read
     *
     * @return ZK popup object that can be appended to the page
     */
    fun readTooltip(id: String): Popup {
        val popup = Popup()

        configNode.childNodes.firstOrNull { it.identifier == id }?.apply tooltip@ {
            popup.id = id
            popup.appendChild(
                Html(this.valueWithIdentifier("label").value).apply { this.id = id })
        }

        return popup
    }
}