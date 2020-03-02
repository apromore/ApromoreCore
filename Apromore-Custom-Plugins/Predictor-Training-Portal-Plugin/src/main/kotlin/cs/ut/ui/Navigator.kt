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

import cs.ut.engine.JobManager
import cs.ut.logging.NirdizatiLogger
import cs.ut.ui.controllers.Redirectable
import cs.ut.util.Cookies
import cs.ut.util.Page
import org.zkoss.zk.ui.Executions

class Navigator : Redirectable {
    enum class RequestParameter(val value: String) {
        JOB("id");

        companion object {
            fun fromString(string: String): RequestParameter? = RequestParameter.values().firstOrNull { it.value == string }
        }
    }

    fun resolveRoute(route: String) {
        log.debug("Resolving route $route")
        val routeParams = route.split("?")
        if (routeParams.size > 1) {
            log.debug("Route $route has ${routeParams.size} parameters")
            val p = Redirectable.pages.firstOrNull { it.identifier == routeParams[0] }
            if (p != null) {
                log.debug("Request parameters $routeParams")

                val parameters = routeParams.subList(1, routeParams.size)
                parameters.forEach {
                    if (!resolveParameters(it)) return
                }
                navigateTo(p.identifier, parameters.joinToString())
            } else {
                navigateTo(Page.LANDING.value)
            }
        } else {
            val p = Redirectable.pages.firstOrNull { it.identifier == route && !it.valueWithIdentifier("parameters").value<Boolean>() }
                    ?: Redirectable.pages.first { it.identifier == Page.LANDING.value }
            log.debug("Route $route resolved to ${p.identifier}")
            navigateTo(p.identifier)
        }
    }

    private fun resolveParameters(it: String): Boolean {
        val params = it.split("=")
        return if (params.size != 2) {
            false
        } else {
            RequestParameter.fromString(params[0])?.run {
                when (this) {
                    RequestParameter.JOB -> {
                        val start = System.currentTimeMillis()

                        val matching = JobManager.cache.findJob(params[1])
                        if (matching != null) {
                            log.debug("Matching job found $matching")
                            Executions.getCurrent().setAttribute(this.name, matching)

                            System.currentTimeMillis().apply {
                                log.debug("Parameter resolved in ${this - start} ms")
                            }
                            true
                        } else {
                            log.debug("Matching job not found, redirecting to landing page")
                            navigateTo(Page.LANDING.value)

                            System.currentTimeMillis().apply {
                                log.debug("Failed to resolve parameter in ${this - start} ms")
                            }
                            false
                        }
                    }
                }
            } ?: false
        }
    }

    private fun navigateTo(page: String, params: String = "") {
        setContent(page, Executions.getCurrent().desktop.firstPage, params)
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(Navigator::class, Cookies.getCookieKey(Executions.getCurrent().nativeRequest))

        fun createParameters(vararg p: Pair<String, String>): String = p.joinToString(separator = "&") { "${it.first}=${it.second}" }
    }
}