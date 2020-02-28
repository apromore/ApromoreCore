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

package cs.ut.util

import cs.ut.configuration.ConfigFetcher
import cs.ut.engine.IdProvider
import cs.ut.engine.JobManager
import cs.ut.jobs.Job
import cs.ut.jobs.JobStatus
import cs.ut.logging.NirdizatiLogger
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object Cookies {
    private val configNode by ConfigFetcher("cookies")
    val log = NirdizatiLogger.getLogger(Cookies::class)

    fun setUpCookie(response: HttpServletResponse) {
        log.debug("Setting up new cookie")
        val cookie = Cookie(JOBS_KEY, IdProvider.getNextId())
        cookie.maxAge = configNode.valueWithIdentifier("maxAge").value()
        response.addCookie(cookie)
        log.debug("Successfully generated new cookie and added it to response")
    }

    fun getCookieKey(request: Any): String {
        request as HttpServletRequest
        return request.cookies?.firstOrNull { it.name == JOBS_KEY }?.value ?: ""
    }

    fun getJobsByCookie(request: HttpServletRequest): List<Job> {
        val key: String = getCookieKey(request)
        log.debug("Looking for jobs with cookie key: $key")
        return JobManager.getJobByPredicate(key, { it.status != JobStatus.COMPLETED && it.status != JobStatus.FAILED })
    }
}