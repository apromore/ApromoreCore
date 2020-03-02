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

package cs.ut.engine

import cs.ut.configuration.ConfigFetcher
import cs.ut.configuration.ConfigurationReader
import cs.ut.jobs.Job
import cs.ut.logging.NirdizatiLogger
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * Thread pool that executes jobs for Nirdizati Training
 */
object NirdizatiThreadPool : ServletContextListener {
    private val log = NirdizatiLogger.getLogger(NirdizatiLogger::class)

    internal lateinit var threadPool: ExecutorService

    internal val configNode by ConfigFetcher("threadPool")

    /**
     * Execute a runnable in this thread pool
     *
     * @param runnable to execute
     *
     * @return future to control the job status
     */
    fun execute(runnable: Runnable): Future<*> = threadPool.submit(runnable)

    fun runStartUpRoutine() {
        val node = configNode.childNodes.first { it.identifier == "onStartUp" }

        val pkg = node.valueWithIdentifier("package").value
        log.debug("Looking for jobs in package: $pkg")
        val jobs = node.itemListValues()
        log.debug("Jobs to execute on start up -> ${jobs.size}")

        jobs.forEach {
            log.debug("Executing job $it")
            val instance = Class.forName("$pkg.$it").newInstance() as Job
            JobManager.runServiceJob(instance)
            log.debug("Job submitted to worker")
        }
    }

    override fun contextInitialized(p0: ServletContextEvent) {}
    override fun contextDestroyed(p0: ServletContextEvent) {}
}

@WebListener
class NirdizatiContextInitializer : ServletContextListener {
    private val log = NirdizatiLogger.getLogger(NirdizatiContextInitializer::class)
    private lateinit var timer: Timer

    override fun contextInitialized(sce: ServletContextEvent?) {
        log.debug("Initializing thread pool")
        val size: Int = NirdizatiThreadPool.configNode.valueWithIdentifier("capacity").value()
        log.debug("Thread pool size: $size")
        NirdizatiThreadPool.threadPool = Executors.newFixedThreadPool(size, { runnable -> Thread(runnable) })

        NirdizatiThreadPool.runStartUpRoutine()
        log.debug("Finished thread pool initialization")

        timer = Timer("tasksScheduler", true)
        val tasksNode = ConfigurationReader.findNode("tasks")
        val pkg = tasksNode.valueWithIdentifier("package").value
        tasksNode.childNodes.forEach {
            log.debug("Task: ${it.identifier} -> enabled: ${it.isEnabled()}")
            if (it.isEnabled()) {
                val task = Class.forName("$pkg.${it.identifier}").newInstance() as TimerTask
                timer.schedule(
                        task,
                        1,
                        it.valueWithIdentifier("period").value())

                log.debug("Scheduled $task")
            }
        }
    }

    override fun contextDestroyed(sce: ServletContextEvent?) {
        log.debug("Shutting down thread pool")
        NirdizatiThreadPool.threadPool.shutdown()
        log.debug("Thread pool successfully stopped")
    }
}
