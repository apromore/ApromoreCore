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

package org.apromore.plugin.portal.predictortraining

// Java 2 Standard Edition
import java.io.File;
import java.util.Locale

// Java 2 Enterprise Edition
import javax.inject.Inject

// Third party packages
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.`annotation`.Qualifier;
import org.springframework.stereotype.Component
import org.zkoss.zk.ui.Executions

// Local packages
import org.apromore.plugin.portal.DefaultPortalPlugin
import org.apromore.plugin.portal.PortalContext
import org.apromore.service.EventLogService
import org.apromore.service.predictivemonitor.PredictiveMonitorService
import org.apromore.service.predictivemonitor.Predictor

import org.apromore.model.ProcessSummaryType
import org.apromore.model.SummaryType
import org.apromore.model.LogSummaryType
import org.apromore.model.VersionSummaryType

@Component
public class PortalPlugin() : DefaultPortalPlugin() {

    private val LOGGER = LoggerFactory.getLogger("org.apromore.plugin.portal.predictortraining.PortalPlugin")

    private var label      = "Train predictor with log"
    private var groupLabel = "Monitor"

    @Inject private val eventLogService : EventLogService? = null
    @Inject private val predictiveMonitorService : PredictiveMonitorService? = null

    @Inject @Qualifier("python")  private var _python  : String? = null
    @Inject @Qualifier("backend") private var _backend : String? = null
    @Inject @Qualifier("tmpDir")  private var _tmpDir  : String? = null
    @Inject @Qualifier("logFile") private var _logFile : String? = null

    /** This is a kludge used to propagate the service to the other instance of ZK running at /trainPredictor */
    companion object {
        @JvmStatic public var globalEventLogService: EventLogService? = null
        @JvmStatic public var globalPredictiveMonitorService: PredictiveMonitorService? = null
        @JvmStatic public var globalSelectedLogSummaryList: List<LogSummaryType> = java.util.Collections.emptyList<LogSummaryType>()

        @JvmStatic public var python:  String? = null
        @JvmStatic public var backend: String? = null
        @JvmStatic public var tmpDir:  String? = null
        @JvmStatic public var logFile: String? = null
    }

    override public fun getLabel(locale: Locale): String {
        return label
    }

    public fun setLabel(label: String) {
        this.label = label
    }

    override public fun getGroupLabel(locale: Locale): String {
        return groupLabel
    }

    public fun setGroupLabel(groupLabel: String) {
        this.groupLabel = groupLabel
    }

    override public fun execute(portalContext: PortalContext) {

        globalEventLogService = eventLogService
        globalPredictiveMonitorService = predictiveMonitorService
        globalSelectedLogSummaryList = portalContext.getSelection()
                                                    .getSelectedProcessModelVersions()
                                                    .keys
                                                    .map { it as LogSummaryType }
        python  = _python
        backend = _backend
        tmpDir  = _tmpDir
        logFile = _logFile

        LOGGER.info("Execute predictor training UI with backend directory " + backend)
        Executions.sendRedirect("/trainPredictor/#training")
    }
}
