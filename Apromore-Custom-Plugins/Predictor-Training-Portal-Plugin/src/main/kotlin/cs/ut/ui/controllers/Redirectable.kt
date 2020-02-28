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

package cs.ut.ui.controllers

import cs.ut.configuration.ConfigNode
import cs.ut.configuration.ConfigurationReader
import cs.ut.util.DEST
import cs.ut.util.NAVBAR
import org.zkoss.util.resource.Labels
import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Page
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.select.Selectors
import org.zkoss.zul.Button
import org.zkoss.zul.Hbox
import org.zkoss.zul.Include
import java.util.Timer
import kotlin.concurrent.timerTask

typealias PageEnum = cs.ut.util.Page

interface Redirectable {
    /**
     * Sets content of the page. Since application content is located in a single component, then we change is
     * asynchronously. This is done using this method.
     *
     * @param dest - id of the page to which the content should be changed (defined in configuration.xml)
     * @param page        - caller page where Include element should be looked for.
     */
    fun setContent(dest: String, page: Page, params: String = "") {
        Executions.getCurrent().desktop.setBookmark(dest + (if (params.isNotBlank()) "?" else "") + params, false)
        page.title = "${Labels.getLabel("header.$dest")}"
        val include = Selectors.iterable(page, "#contentInclude").iterator().next() as Include
        include.src = null
        include.src = pages.first { it.identifier == dest }.valueWithIdentifier("page").value
        activateHeaderButton(if (dest == PageEnum.VALIDATION.value) PageEnum.MODEL_OVERVIEW.value else dest, page)
    }

    /**
     * Update content of the page with a delay
     *
     * @param dest new destination
     * @param page page where to update the destination
     * @param delay delay in MS
     * @param desktop client to update the content for
     */
    fun setContent(dest: String, page: Page, delay: Int, desktop: Desktop) {
        Timer().schedule(timerTask {
            Executions.schedule(
                    desktop,
                    { _ ->
                        setContent(dest, page)
                    },
                    Event("content change")
            )
        }, delay.toLong())
    }

    /**
     * Active selected header button
     *
     * @param dest new selected value
     * @param page where to update the header
     */
    private fun activateHeaderButton(dest: String, page: Page) {
        val navbar = page.desktop.components.first { it.id == NAVBAR } as Hbox //Navbar
        val navItem = page.desktop.components.firstOrNull { it.getAttribute(DEST) == dest } as Button? //Navitem?
        //navItem?.let { navbar.selectItem(navItem) }
    }

    companion object {
        val pages: List<ConfigNode> = ConfigurationReader.findNode("pages").childNodes
    }
}
