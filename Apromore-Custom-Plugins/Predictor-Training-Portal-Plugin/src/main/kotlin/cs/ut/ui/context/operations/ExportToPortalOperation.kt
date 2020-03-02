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

package cs.ut.ui.context.operations

import cs.ut.json.JSONHandler
import cs.ut.jobs.SimulationJob
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import java.io.File
import java.io.FileInputStream
import org.apromore.plugin.portal.predictortraining.PortalPlugin

class ExportToPortalOperation(context: SimulationJob) : Operation<SimulationJob>(context) {

    private val log = NirdizatiLogger.getLogger(ExportToPortalOperation::class)

    override fun perform() {

        // Transfer the pkl to MySQL
        val file = File(DirectoryConfiguration.dirPath(Dir.PKL_DIR)).listFiles().firstOrNull { it.name.contains(context.id) }
        val pkl = FileInputStream(file)
        val predictorName = uniqueLogName("${context.logName} (${context.configuration.bucketing.parameter}, ${context.configuration.encoding.parameter}, ${context.configuration.learner.parameter})")
        val predictor = PortalPlugin.globalPredictiveMonitorService!!.createPredictor(predictorName, context.configuration.outcome.parameter, pkl)
    }

    private fun logNameExists(logName: String): Boolean {
        return false
    }

    private fun uniqueLogName(name: String): String {
        var count = 1
        var result = name
        while (logNameExists(result)) {
            count++
            result = "${name} #${count}"
        }
        return result
    }
}
