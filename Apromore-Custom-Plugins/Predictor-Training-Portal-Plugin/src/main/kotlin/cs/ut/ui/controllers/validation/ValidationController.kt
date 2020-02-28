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

package cs.ut.ui.controllers.validation

import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.JobCacheHolder
import cs.ut.engine.JobManager
import cs.ut.engine.events.Callback
import cs.ut.engine.events.StatusUpdateEvent
import cs.ut.exceptions.NirdizatiRuntimeException
import cs.ut.jobs.Job
import cs.ut.jobs.JobStatus
import cs.ut.jobs.SimulationJob
import cs.ut.ui.ComparatorPair
import cs.ut.ui.GridComparator
import cs.ut.ui.NirdizatiGrid
import cs.ut.ui.adapters.ValidationViewAdapter
import cs.ut.ui.context.MenuGenerator
import cs.ut.ui.controllers.Redirectable
import cs.ut.util.Cookies
import cs.ut.util.NirdizatiTranslator
import cs.ut.util.Page
import org.zkoss.zk.ui.Component
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.select.SelectorComposer
import org.zkoss.zk.ui.select.annotation.Wire
import org.zkoss.zul.Button
import org.zkoss.zul.Column
import org.zkoss.zul.Hlayout
import org.zkoss.zul.Label
import org.zkoss.zul.Menupopup
import org.zkoss.zul.Row
import org.zkoss.zul.Vbox
import java.time.Instant

class ValidationController : SelectorComposer<Component>(), Redirectable {

    @Wire
    private lateinit var gridContainer: Vbox

    @Wire
    private lateinit var grid: NirdizatiGrid<Job>

    private val menuMode = MenuGenerator.MenuMode.VALIDATION
    private lateinit var contextMenu: Menupopup

    override fun doAfterCompose(comp: Component?) {
        super.doAfterCompose(comp)
        contextMenu = MenuGenerator<SimulationJob>(menuMode).generate()
        gridContainer.appendChild(contextMenu)

        generate()
        JobManager.subscribe(this)
    }

    /**
     * Generate the content for the controller
     */
    private fun generate() {
        val isDemo = ConfigurationReader.findNode("demo").isEnabled()

        val userJobs =
                if (isDemo)
                    JobCacheHolder.simulationJobs()
                            .sortedByDescending { Instant.parse(it.startTime) }
                else
                    JobManager
                            .cache
                            .retrieveFromCache((Cookies.getCookieKey(Executions.getCurrent().nativeRequest)))
                            .rawData().sortedByDescending { Instant.parse(it.startTime) }

        grid = NirdizatiGrid(ValidationViewAdapter(this, gridContainer), "validation").apply {
            this.configure()
        }

        if (userJobs.isEmpty()) {
            emptyLayout()
            return
        }

        grid.contextMenu = contextMenu
        grid.generate(userJobs)

        gridContainer.appendChild(grid)
    }

    fun page() = this.page ?: throw NirdizatiRuntimeException("No current page set")

    /**
     * Create empty layout when user has no trained models
     */
    private fun emptyLayout() {
        gridContainer.appendChild(Vbox().apply {
            this.align = "center"
            this.pack = "center"
            this.appendChild(
                    Label(NirdizatiTranslator.localizeText("validation.empty1")).apply {
                        this.sclass = "large-text"
                    })
            this.appendChild(
                    Label(NirdizatiTranslator.localizeText("validation.empty2")).apply {
                        this.sclass = "large-text"
                    })
            this.appendChild(
                    Hlayout().apply {
                        this.vflex = "min"
                        this.hflex = "min"
                        this.sclass = "margin-top-7px"
                        this.appendChild(
                                Button(NirdizatiTranslator.localizeText("validation.train")).also {
                                    it.addEventListener(Events.ON_CLICK, { _ ->
                                        this@ValidationController.setContent(Page.TRAINING.value, page)
                                    })
                                    it.sclass = "n-btn"
                                }
                        )
                    }

            )
        }
        )
    }

    /**
     * Configure grid to set up columns, flex and to center the content
     */
    private fun NirdizatiGrid<Job>.configure() {
        this.setColumns(
                listOf(
                        NirdizatiGrid.ColumnArgument(name = "logfile"),
                        NirdizatiGrid.ColumnArgument(name = "predictiontype"),
                        NirdizatiGrid.ColumnArgument(name = "bucketing"),
                        NirdizatiGrid.ColumnArgument(name = "encoding"),
                        NirdizatiGrid.ColumnArgument(name = "learner"),
                        NirdizatiGrid.ColumnArgument(name = "hyperparameters"),
                        NirdizatiGrid.ColumnArgument(name = "timestamp", comp = jobDateComparator()),
                        NirdizatiGrid.ColumnArgument(name = "accuracy", flex = "1", comp = accuracyComparator()),
                        NirdizatiGrid.ColumnArgument(flex = "min")
                )
        )

        this.hflex = "1"
        this.vflex = "1"
        this.id = gridId
        this.columns.getChildren<Column>().forEach { it.align = "center" }
    }

    private fun jobDateComparator(): GridComparator {
        return ComparatorPair<Row>(
                Comparator { r0, r1 ->
                    val job0: SimulationJob = r0.getValue<SimulationJob>()
                    val job1: SimulationJob = r1.getValue<SimulationJob>()

                    job0.date.compareTo(job1.date)
                },

                Comparator { r0, r1 ->
                    val job0: SimulationJob = r0.getValue<SimulationJob>()
                    val job1: SimulationJob = r1.getValue<SimulationJob>()

                    job1.date.compareTo(job0.date)
                }
        )
    }

    private fun accuracyComparator(): GridComparator =
            ComparatorPair<Row>(
                    Comparator { r0, r1 ->
                        val job0: SimulationJob = r0.getValue<SimulationJob>()
                        val job1: SimulationJob = r1.getValue<SimulationJob>()

                        job0.configuration.evaluation.value.compareTo(job1.configuration.evaluation.value)
                    },

                    Comparator { r0, r1 ->
                        val job0: SimulationJob = r0.getValue<SimulationJob>()
                        val job1: SimulationJob = r1.getValue<SimulationJob>()

                        job1.configuration.evaluation.value.compareTo(job0.configuration.evaluation.value)
                    }
            )

    /**
     * Call back function to receive updates from JobManager
     *
     * @see JobManager
     */
    @Callback(StatusUpdateEvent::class)
    fun updateContent(event: StatusUpdateEvent) {
        if (self.desktop == null || !self.desktop.isAlive) {
            return
        }

        when (event.data) {
            is SimulationJob -> {
                if (event.data.status == JobStatus.COMPLETED) {
                    Executions.schedule(
                            self.desktop, { _ ->
                        val userJobs =
                                JobManager
                                        .cache
                                        .retrieveFromCache(Cookies.getCookieKey(Executions.getCurrent().nativeRequest))
                                        .rawData()
                                        .reversed()
                        if (grid.parent != gridContainer) {
                            gridContainer.getChildren<Component>().clear()
                            gridContainer.appendChild(grid)
                        }

                        grid.generate(userJobs, true)
                    },
                            Event("content_update")
                    )
                }
            }
        }
    }

    companion object {
        const val gridId = "validationGrid"
    }
}