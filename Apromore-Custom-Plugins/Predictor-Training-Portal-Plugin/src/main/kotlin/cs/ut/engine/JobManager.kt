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

import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.events.DeployEvent
import cs.ut.engine.events.NirdizatiEvent
import cs.ut.engine.events.StatusUpdateEvent
import cs.ut.engine.events.findCallback
import cs.ut.jobs.Job
import cs.ut.jobs.JobStatus
import cs.ut.jobs.SimulationJob
import cs.ut.logging.NirdizatiLogger
import java.lang.ref.WeakReference
import java.util.concurrent.Future

/**
 * Manages jobs
 */
object JobManager {
    val log = NirdizatiLogger.getLogger(JobManager::class)

    val cache: JobCacheHolder = Cache.jobCache
    private var subscribers: List<WeakReference<Any>> = listOf()
    private val jobStatus: MutableMap<Job, Future<*>> = mutableMapOf()

    val queue: MutableList<SimulationJob> = mutableListOf()

    /**
     * Subscribe to notifications of job updates.
     * Object must have a method annotated with @CallBack annotation in order to receive updates
     *
     * @param caller object that needs to subscribe for updates
     *
     */
    fun subscribe(caller: Any) {
        synchronized(subscribers) {
            log.debug("New subscriber for updates -> ${caller::class.java}")
            subscribers += WeakReference(caller)
        }
    }

    /**
     * Unsubscribe from updates
     *
     * @param caller object to unsubscribe
     */
    fun unsubscribe(caller: Any) {
        synchronized(subscribers) {
            log.debug("Removing subscriber ${caller::class.java}")
            subscribers.firstOrNull { it.get() == caller }?.let { subscribers -= it }
        }
    }

    /**
     * Function that is called by the job when status of a job has been updated
     *
     * @param job status for which has been updated
     */
    fun statusUpdated(job: Job) {
        log.debug("Update event: ${job.id} -> notifying subscribers")

        if (job.status == JobStatus.COMPLETED && job in queue && job is SimulationJob) {
            queue.remove(job)
            cache.addToCache(job.owner, job)
        }

        handleEvent(StatusUpdateEvent(job))
    }

    /**
     * Clean dead subscribers (weak references that have been removed by GC) and then
     * notify subscribers of an event
     *
     * @param event to notify of
     */
    private fun handleEvent(event: NirdizatiEvent) {
        cleanSubscribers()
        synchronized(subscribers) {
            subscribers.forEach {
                notify(it, event)
            }
        }
    }

    /**
     * Clean garbage collected subscribers since we use weak references
     */
    private fun cleanSubscribers() {
        val before: Int = subscribers.size
        subscribers = subscribers.filter { it.get() != null }
        val after: Int = subscribers.size
        if (before > after) {
            log.debug("Unsubscribed ${before - after} callbacks")
        }
    }

    /**
     * Notify subscriber of the event. More specifically find
     * method annotated with @Callback annotation and call it with
     * the event as parameter
     *
     * @param ref reference to the object to notify
     * @param event to notify about
     */
    private fun notify(ref: WeakReference<Any>, event: NirdizatiEvent) {
        val obj = ref.get() ?: return
        findCallback(obj::class.java, event::class)?.invoke(obj, event)
    }

    /**
     * Deploy given jobs with given key into the threadpool
     *
     * @param key client
     * @param jobs list of jobs to deploy
     */
    fun deployJobs(key: String, jobs: Collection<Job>) {
        log.debug("Jobs to be executed for client $key -> $jobs")
        log.debug("Deploying ${jobs.size} jobs")

        synchronized(jobStatus) {
            jobs.forEach {
                jobStatus[it] = NirdizatiThreadPool.execute(it)
                queue.add(it as SimulationJob)
            }
        }

        log.debug("Updating completed job status for $key")

        log.debug("Successfully deployed all jobs to worker")
        handleEvent(DeployEvent(key, jobs))
    }

    /**
     * Gracefully stop given job
     *
     * @param job to stop
     */
    fun stopJob(job: Job) {
        log.debug("Stopping job ${job.id}")
        job.beforeInterrupt()
        log.debug("Completed before interrupt")
        jobStatus[job]?.cancel(true)
        log.debug("Job thread ${job.id} successfully interrupted")
    }

    /**
     * Run a service job such as ACL update or directory structure creation job
     *
     * @param job to run
     *
     * @return future to control the job
     */
    fun runServiceJob(job: Job): Future<*> {
        log.debug("Running service job $job")
        return NirdizatiThreadPool.execute(job)
    }

    /**
     * Returns all jobs for given key completed and uncompleted.
     *
     * @param key to retrieve jobs for
     *
     * @return collection of simulation jobs
     */
    fun getJobsForKey(key: String): List<SimulationJob> {
        val cached: List<SimulationJob> = cache.retrieveFromCache(key).rawData()
        val pending: List<SimulationJob> = queue.filter { it.owner == key }
        return (pending.toList() + cached.toList())
    }

    /**
     * Get jobs for key with an optional predicate (default is complted jobs)
     *
     * @param key to fetch jobs for
     * @param predicate optional predicate to filter jobs by
     *
     * @return collection of jobs matching the predicate
     */
    inline fun getJobByPredicate(key: String, predicate: (SimulationJob) -> Boolean = { it.status == JobStatus.COMPLETED }): List<SimulationJob> =
            getJobsForKey(key).filter(predicate)

    /**
     * Find similar jobs to the given one so they can be used in comparison
     *
     * @param job similar jobs to the given one
     */
    fun findSimilarJobs(job: SimulationJob): List<SimulationJob> {
        val config = ConfigurationReader.findNode("demo").isEnabled()

        val allJobs = if (config) JobCacheHolder.simulationJobs() else JobManager.getJobByPredicate(job.owner)
        return allJobs.filter { it.logFile == job.logFile && it.configuration.outcome.id == job.configuration.outcome.id && it.id != job.id }
    }
}