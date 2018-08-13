package cs.ut.engine.events

import cs.ut.jobs.Job

/**
 * Top element of Nirdizati event hierarchy
 */
sealed class NirdizatiEvent

/**
 * Event that is fired whenever job status has been updated
 *
 * @param data job which status has been updated
 */
data class StatusUpdateEvent(val data: Job) : NirdizatiEvent()

/**
 * Event that contains list of jobs that have been deployed
 *
 * @param target client whose jobs have been deployed
 * @param data list of jobs that have been deployed
 */
data class DeployEvent(val target: String, val data: Collection<Job>) : NirdizatiEvent()