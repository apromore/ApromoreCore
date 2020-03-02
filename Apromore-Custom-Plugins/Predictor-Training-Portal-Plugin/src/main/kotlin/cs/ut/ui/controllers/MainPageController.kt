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

import cs.ut.engine.item.ClientInfo
import cs.ut.jobs.Job
import cs.ut.logging.NirdizatiLogger
import cs.ut.ui.Navigator
import cs.ut.ui.NirdizatiGrid
import cs.ut.ui.UIComponent
import cs.ut.ui.controllers.JobTrackerController.Companion.GRID_ID
import cs.ut.util.Cookies
import cs.ut.util.NAVBAR
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.Session
import org.zkoss.zk.ui.event.BookmarkEvent
import org.zkoss.zk.ui.event.ClientInfoEvent
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Listen
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Borderlayout
import org.zkoss.zul.East
import org.zkoss.zul.Hbox
import java.util.NoSuchElementException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MainPageController : SelectorComposer<Component>(), Redirectable, UIComponent {
    val log = NirdizatiLogger.getLogger(MainPageController::class, getSessionId())
    private var clientInformation: Map<Session, ClientInfo> = mapOf()

    @Wire
    private lateinit var mainLayout: Borderlayout

    private val navigator: Navigator = Navigator()

    @Wire
    private lateinit var trackerEast: East

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)

        mainLayout.addEventListener(Events.ON_BOOKMARK_CHANGE, { event ->
            event as BookmarkEvent
            navigator.resolveRoute(event.bookmark)
        })
    }

    @Listen("onClientInfo = #mainLayout")
    fun gatherInformation(e: ClientInfoEvent) {
        log.debug("Client info event, gathering browser information")
        val info = ClientInfo(
                e.screenWidth,
                e.screenHeight,
                e.desktopWidth,
                e.desktopHeight,
                e.colorDepth,
                e.orientation
        )

        if (e.desktopWidth <= 680) {
            updateHeader(true)
        } else {
            updateHeader()
        }

        Executions.getCurrent().desktop.enableServerPush(true)
        clientInformation += mapOf(Executions.getCurrent().session to info)
        info.configureTracker()
        log.debug("Finished gathering information about browser")

        handleCookie()
    }

    /**
     * Remove text from header if screen size is too small
     *
     * @param collapse should header be collapsed
     */
    private fun updateHeader(collapse: Boolean = false) {
        Executions.getCurrent().desktop.components.firstOrNull { it.id == NAVBAR }?.let {
            it as Hbox //Navbar
            //it.isCollapsed = collapse
        }
    }

    /**
     * Handles users cookie so jobs could be persistent if user refreshes the page.
     */
    @Suppress("UNCHECKED_CAST")
    private fun handleCookie() {
        val request = Executions.getCurrent().nativeRequest as HttpServletRequest
        val cookieKey: String = Cookies.getCookieKey(request)
        if (cookieKey.isBlank()) {
            Cookies.setUpCookie(Executions.getCurrent().nativeResponse as HttpServletResponse)
        } else {
            val jobGrid: NirdizatiGrid<Job> =
                    Executions.getCurrent().desktop.components.first { it.id == GRID_ID } as NirdizatiGrid<Job>
            val jobs: List<Job> = Cookies.getJobsByCookie(request)
            if (jobs.isNotEmpty()) {
                jobGrid.generate(jobs)
                trackerEast.isVisible = true
            }
        }
    }

    /**
     * Configures tracker to be suitable for browsers screen size
     */
    private fun ClientInfo.configureTracker() {
        log.debug("Configuring job tracker for $this")
        trackerEast.size = "${this.windowWidth * 0.3}px"
        trackerEast.isVisible = false
    }

    /**
     * Retreives browser information for current session
     */
    fun getClientInfo(session: Session): ClientInfo = clientInformation[session] ?: throw NoSuchElementException()

    fun getComp(): Component = self
}
