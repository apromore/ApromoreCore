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

import cs.ut.engine.JobManager
import cs.ut.engine.events.Callback
import cs.ut.engine.events.DeployEvent
import cs.ut.engine.events.StatusUpdateEvent
import cs.ut.jobs.Job
import cs.ut.jobs.JobStatus
import cs.ut.jobs.SimulationJob
import cs.ut.ui.NirdizatiGrid
import cs.ut.ui.adapters.JobValueAdapter
import cs.ut.util.Cookies
import cs.ut.util.MAINLAYOUT
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.TRACKER_EAST
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zk.ui.util.Clients
import org.zkoss.zul.Button
import org.zkoss.zul.East
import org.zkoss.zul.Hbox
import org.zkoss.zul.Label
import org.zkoss.zul.Row
import java.time.Instant

class JobTrackerController : SelectorComposer<Component>(), Redirectable {
    @Wire
    private lateinit var jobTracker: Hbox

    companion object {
        const val GRID_ID = "tracker_grid"
    }

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)

        self.desktop.enableServerPush(true)
        JobManager.subscribe(this)

        val jobGrid = NirdizatiGrid(JobValueAdapter)
        jobGrid.vflex = "1"
        jobGrid.id = GRID_ID
        jobTracker.appendChild(jobGrid)
    }

    /**
     * Call back function to receive updates from job manager
     */
    @Suppress("UNCHECKED_CAST")
    @Callback(StatusUpdateEvent::class)
    fun updateJobStatus(event: StatusUpdateEvent) {
        if (self.desktop == null || !self.desktop.isAlive || event.data !is SimulationJob) {
            return
        }

        Executions.schedule(
                self.desktop,
                { _ ->
                    val subKey: String =
                            Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
                    if (subKey == event.data.owner) {
                        val grid =
                                Executions.getCurrent().desktop.components.first { it.id == GRID_ID } as NirdizatiGrid<Job>
                        event.data.updateJobStatus(grid.rows.getChildren())
                    }
                },
                Event("job_update")
        )
    }

    /**
     * Call back function to receive updates from job manager
     */
    @Callback(DeployEvent::class)
    @Suppress("UNCHECKED_CAST")
    fun updateDeployment(event: DeployEvent) {
        if (self.desktop == null || !self.desktop.isAlive) {
            return
        }

        event.data.sortedByDescending { Instant.parse(it.startTime) }

        Executions.schedule(
                self.desktop,
                { _ ->
                    val subKey: String =
                            Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
                    if (subKey == event.target) {

                        val comps = Executions.getCurrent().desktop.components
                        val tracker = comps.first { it.id == TRACKER_EAST } as East
                        tracker.isVisible = true

                        val grid = comps.first { it.id == GRID_ID } as NirdizatiGrid<Job>
                        grid.generate(event.data, false, true)

                        val main = comps.first { it.id == MAINLAYOUT }
                        Clients.showNotification(
                                NirdizatiTranslator.localizeText("job_tracker.start")
                                , "info", main, "middle_right", 10000, true)
                    }
                },
                Event("deployment")
        )
    }

    /**
     * Update job status label to match new status
     *
     * @param rows where to look for needed label
     */
    private tailrec fun Job.updateJobStatus(rows: List<Row>) {
        if (rows.isNotEmpty()) {
            val row = rows.first()
            val buttons = row.lastChild.lastChild.getChildren<Component>()
            val statusLabel = row.firstChild.getChildren<Component>()[1].lastChild.firstChild as Label

            if (this == row.getValue()) {
                statusLabel.value = this.status.name
                buttons.forEach { (it as Button).isDisabled = this.status != JobStatus.COMPLETED }

                if (this.status == JobStatus.COMPLETED) {
                    NirdizatiTranslator.showNotificationAsync(
                            NirdizatiTranslator.localizeText("job.completed.simulation", this),
                            Executions.getCurrent().desktop
                    )
                }
            } else {
                updateJobStatus(rows.drop(1))
            }
        }
    }
}