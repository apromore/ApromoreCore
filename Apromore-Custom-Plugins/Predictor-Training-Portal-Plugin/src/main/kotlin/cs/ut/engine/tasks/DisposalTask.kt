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
import cs.ut.engine.JobCacheHolder
import cs.ut.engine.LogManager
import cs.ut.exceptions.Left
import cs.ut.exceptions.Right
import cs.ut.jobs.SimulationJob
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import java.io.File
import java.time.Instant
import java.util.Date
import java.util.TimerTask
import kotlin.system.measureTimeMillis

class DisposalTask : TimerTask() {
    override fun run() {
        val time = measureTimeMillis {
            log.debug("Running disposal task")

            JobCacheHolder.simulationJobs().forEach { if (it.isExpired()) this dispose it }

            log.debug("Disposed of $disposed jobs")
            disposed = 0

            log.debug("Flushing caches")
            Cache.jobCache.flush()
            Cache.chartCache.clear()
            log.debug("Finished flushing caches")
        }
        log.debug("Finished running disposal task in $time ms")
    }

    private fun SimulationJob.isExpired() = Date().time - Date.from(Instant.parse(this.startTime)).time >= age

    infix fun dispose(job: SimulationJob) {
        log.debug("${job.id} is expired, disposing of the job")

        File(DirectoryConfiguration.dirPath(Dir.TRAIN_DIR) + "${job.id}.json").apply {
            this.safeDelete()
            log.debug("Deleted training file for job ${this.absoluteFile}")
        }

        LogManager.getDetailedFile(job, safe = true).apply {
            when (this) {
                is Right -> {
                    this.result.safeDelete()
                    log.debug("Deleted detailed file for job -> ${this.result.absoluteFile}")
                }
                is Left -> {
                    log.error("Error occurred during disposal task", this.error)
                }
            }
        }

        LogManager.getFeatureImportanceFiles(job).apply {
            when (this) {
                is Right -> {
                    log.debug("Deleting ${this.result.size} feature importance file")
                    this.result.forEach { it.safeDelete() }
                    log.debug("Finished feature importance file deletion")
                }

                is Left -> log.error("Error occurred during disposal task", this.error)
            }
        }

        LogManager.getDetailedFile(job, safe = true).apply {
            when (this) {
                is Right -> {
                    this.result.safeDelete()
                    log.debug("Deleted detailed file -> ${this.result.absoluteFile}")
                }
                is Left -> {
                    log.error("Error occurred during disposal task", this.error)
                }
            }
        }

        log.debug("Finished disposal of job ${job.id}")
        disposed++
    }

    private fun File.safeDelete() {
        try {
            this.delete()
        } catch (e: Exception) {
            log.error("Could not delete ${this.absoluteFile}", e)
        }
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(DisposalTask::class)
        private val age: Long =
                ConfigurationReader.findNode("tasks/DisposalTask").valueWithIdentifier("age").value()

        var disposed: Int = 0
    }

}
