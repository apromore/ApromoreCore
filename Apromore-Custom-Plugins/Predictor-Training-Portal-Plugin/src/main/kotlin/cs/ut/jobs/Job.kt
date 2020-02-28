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

package cs.ut.jobs


import cs.ut.engine.IdProvider
import cs.ut.engine.JobManager
import cs.ut.exceptions.Left
import cs.ut.exceptions.ProcessErrorException
import cs.ut.exceptions.Right
import cs.ut.exceptions.perform
import cs.ut.logging.NirdizatiLogger
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Enum that represents job status
 */
enum class JobStatus {
    QUEUED,
    PREPARING,
    RUNNING,
    FINISHING,
    COMPLETED,
    FAILED
}

/**
 * Abstract class that represents job structure
 */
abstract class Job protected constructor(generatedId: String = "") : Runnable {
    val log = NirdizatiLogger.getLogger(Job::class)

    val id: String = if (generatedId.isBlank()) IdProvider.getNextId() else generatedId

    var status: JobStatus = JobStatus.QUEUED

    var startTime: String = start()

    /**
     * Action to be performed before job execution
     */
    open fun preProcess() = Unit

    /**
     * Action to be performed in execution stage
     */
    open fun execute() = Unit

    /**
     * Action to be performed after execute stage
     */
    open fun postExecute() = Unit

    /**
     * Should user be notified of job completion
     */
    open fun isNotificationRequired() = false

    /**
     * Notification message to show to the user
     */
    open fun getNotificationMessage() = ""

    /**
     * Function that is called before interrupting the thread on graceful shutdown
     */
    open fun beforeInterrupt() = Unit

    /**
     * In case job wants to handle errors itself
     */
    open fun onError() = Unit

    /**
     * Running the job
     */
    override fun run() {
        val start = System.currentTimeMillis()
        log.debug("Started job execution: $this")
        startTime = start()


        perform {
            log.debug("Stared pre process stage")
            status = JobStatus.PREPARING

            updateEvent()
            preProcess()
        }.apply {
            when (this) {
                is Right -> log.debug("Job $id finished pre process step")
                is Left -> {
                    handleError(this)
                    return
                }
            }
        }

        perform {
            log.debug("Job $id started execute stage")
            status = JobStatus.RUNNING

            updateEvent()
            execute()

            if (errorOccurred()) {
                throw ProcessErrorException()
            }
        }.apply {
            when (this) {
                is Right -> log.debug("Job $id finished execute step")

                is Left -> {

                    perform {
                        onError()
                    }.apply {
                        when (this) {
                            is Left -> log.error("Error occurred when handling exception for job $id", this.error)
                        }
                    }

                    log.error("Job $id failed in execute stage", this.error)
                    status = JobStatus.FAILED
                    updateEvent()
                    return
                }
            }
        }

        perform {
            log.debug("Job $id started post execute step")
            status = JobStatus.FINISHING

            updateEvent()

            postExecute()
        }.apply {
            when (this) {
                is Right -> log.debug("Job $id completed successfully")
                is Left -> {
                    handleError(this)
                    return
                }
            }
        }

        status = JobStatus.COMPLETED
        updateEvent()

        val end = System.currentTimeMillis()
        log.debug("$this finished running in ${end - start} ms")
    }

    private fun handleError(left: Left<Exception>) {
        log.debug("Job $id failed", left.error)
        status = JobStatus.FAILED
        updateEvent()
    }

    /**
     * Status have been updated, notify job manager
     */
    private fun updateEvent() {
        JobManager.statusUpdated(this)
    }

    open fun errorOccurred() = false

    /**
     * Get start time for the job
     *
     * @return start time in ISO format as string
     */
    private fun start(): String {
        val date = Date()
        val df = DateTimeFormatter.ISO_INSTANT
        return df.format(date.toInstant())
    }
}