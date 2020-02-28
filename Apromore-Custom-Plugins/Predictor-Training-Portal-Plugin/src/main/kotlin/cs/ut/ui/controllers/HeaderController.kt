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
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.Page
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;

class HeaderController : SelectorComposer<Component>(), Redirectable {

    private val configNode = ConfigurationReader.findNode("header")

    @Wire
    private lateinit var navbar: Hbox //Navbar

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)
        composeHeader()
    }

    /**
     * Compose header based on configuration node
     */
    private fun composeHeader() {
        val items: List<ConfigNode> = configNode.childNodes

        items.forEach {
            val navItem = Button() //Navitem()
            navItem.label = NirdizatiTranslator.localizeText(it.valueWithIdentifier("label").value)
            navItem.setAttribute(DEST, it.valueWithIdentifier("redirect").value)
            navItem.iconSclass = it.valueWithIdentifier("icon").value
            navItem.sclass = "n-nav-item"
            navItem.addEventListener(Events.ON_CLICK, { _ ->
                setContent(it.valueWithIdentifier("redirect").value, page)
                //navbar.selectItem(navItem)
            })
            navItem.isVisible = it.isEnabled()

            navbar.appendChild(navItem)
        }
    }

    /**
     * Listener - update selected item in navigation bar when clicked
     */
    /*
    @Listen("onClick = #headerLogo")
    fun handleLogoClick() {
        setContent(Page.LANDING.value, page)
        //navbar.selectItem(null)
    }
    */
}
