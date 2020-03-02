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

package cs.ut.engine.tasks

import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.Cache
import cs.ut.logging.NirdizatiLogger
import java.util.TimerTask
import kotlin.system.measureTimeMillis

class CacheCleanTask : TimerTask() {
    override fun run() {
        val time = measureTimeMillis {
            log.debug("Running cache cleaning task")
            log.debug("Cleaning job cache")
            val toCleanUp = mutableListOf<String>()
            Cache.jobCache.cachedItems().forEach { if (it.value.isExpired(timeToLive)) toCleanUp.add(it.key) }
            log.debug("Found ${toCleanUp.size} items that will be cleaned up")
            toCleanUp.forEach { Cache.jobCache.cachedItems().remove(it) }
            log.debug("Job cache cleaned up")


            log.debug("Cleaning up chart cache")
            Cache.chartCache.values.forEach { holder ->
                val chartCleanUp = mutableListOf<String>()
                holder.cachedItems().forEach { if (it.value.isExpired(timeToLive)) chartCleanUp.add(it.key) }
                log.debug("Cleaning up ${chartCleanUp.size} charts")
                chartCleanUp.forEach { holder.cachedItems().remove(it) }
            }
            log.debug("Finished chart clean up")
        }
        log.debug("Clean up task finished in $time ms")
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(CacheCleanTask::class)
        private val timeToLive: Long =
                ConfigurationReader.findNode("tasks/CacheCleanTask").valueWithIdentifier("timeToLive").value()
    }
}