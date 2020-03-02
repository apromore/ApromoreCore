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

package cs.ut.ui.controllers

import cs.ut.configuration.ConfigurationReader
import cs.ut.util.Page
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Html

/**
 *  Controller that responds for landing page and controls found on that page
 */
class LandingPageController : SelectorComposer<Component>(), Redirectable {

    @Wire
    private lateinit var upload: Button

    @Wire
    private lateinit var existingLog: Button

    @Wire
    private lateinit var welcomeMessage: Html

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)
        wireButtons()
        appendMessage()
    }

    /**
     * Sets up listeners for wired buttons.
     * In this case it buttons set content of the page based on uri-s defined in configuration.xml
     */
    private fun wireButtons() {
        upload.addEventListener(Events.ON_CLICK, { _ ->
            setContent(Page.UPLOAD.value, page)
        })

        existingLog.addEventListener(Events.ON_CLICK, { _ ->
            setContent(Page.TRAINING.value, page)
        })
    }

    private fun appendMessage() {
        val msg = ConfigurationReader.findNode("messages/welcome").valueWithIdentifier("label").value
        welcomeMessage.content = msg
    }
}