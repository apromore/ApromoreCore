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