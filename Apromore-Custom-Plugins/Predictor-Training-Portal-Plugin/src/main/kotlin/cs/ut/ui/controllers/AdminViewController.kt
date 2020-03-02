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

import cs.ut.configuration.ConfigFetcher
import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.Cache
import cs.ut.engine.JobCacheHolder
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import cs.ut.ui.UIComponent
import org.zkoss.util.resource.Labels
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Textbox
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.charset.Charset
import javax.xml.bind.DatatypeConverter

class AdminViewController : SelectorComposer<Component>(), UIComponent {
    val log = NirdizatiLogger.getLogger(AdminViewController::class, getSessionId())

    @Wire
    private lateinit var flushConfig: Button

    @Wire
    private lateinit var flushMessages: Button

    @Wire
    private lateinit var passwordField: Textbox

    @Wire
    private lateinit var showLogs: Button

    @Wire
    private lateinit var logData: Textbox

    @Wire
    private lateinit var flushCache: Button

    private val configNode by ConfigFetcher("userPreferences/adminFunctionality")

    private val logFile: File = File(DirectoryConfiguration.dirPath(Dir.LOG_FILE))

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)

        flushConfig.addEventListener(Events.ON_CLICK, { _ ->
            log.debug("Flushing master configuration")
            ConfigurationReader.reload()
            log.debug("Master configuration flushed")
        })

        flushMessages.addEventListener(Events.ON_CLICK, { _ ->
            performTask(flushMessages())
        })

        showLogs.addEventListener(Events.ON_CLICK, { _ ->
            performTask(readLogFile())
        })

        flushCache.addEventListener(Events.ON_CLICK, { _ ->
            performTask(flushCache())
        })
    }

    private fun performTask(task: Runnable) {
        if (isAuthorized()) {
            task.run()
        } else {
            log.debug("Not authorized: ${passwordField.value}")
            passwordField.errorMessage = "Invalid key"
        }
    }

    private fun flushMessages(): Runnable = Runnable {
        log.debug("Flushing messages files")
        Labels.reset()
        log.debug("Successfully flushed messages file")
    }

    private fun readLogFile(): Runnable = Runnable {
        if (logFile.exists() && logFile.isFile) {
            logData.isVisible = true
            log.debug("File exists and is a log file")
            logData.value = BufferedReader(FileReader(logFile)).lineSequence().joinToString("\n")
            log.debug("Finished parsing log file")
        }
    }

    private fun flushCache(): Runnable = Runnable {
        log.debug("Flushing cache")
        Cache.jobCache = JobCacheHolder()
        Cache.chartCache = mutableMapOf()
        log.debug("Caches successfully flushed")
    }

    private fun isAuthorized(): Boolean {
        passwordField.clearErrorMessage()
        return configNode.isEnabled() && (!configNode.valueWithIdentifier("isPasswordRequired").value<Boolean>() ||
                DatatypeConverter.printBase64Binary(
                    (passwordField.value ?: "").toByteArray(Charset.forName("UTF-8"))
                ) == configNode.valueWithIdentifier("password").value)
    }

}
