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

package cs.ut.ui.context.operations

import cs.ut.engine.JobManager
import cs.ut.engine.tasks.DisposalTask
import cs.ut.jobs.SimulationJob
import cs.ut.ui.NirdizatiGrid
import cs.ut.ui.controllers.validation.ValidationController
import cs.ut.util.Cookies
import org.zkoss.zk.ui.Executions

class DeleteJobOperation(context: SimulationJob) : Operation<SimulationJob>(context) {

    @Suppress("UNCHECKED_CAST")
    override fun perform() {
        DisposalTask() dispose context
        JobManager.cache.apply {
            val key = Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
            val items = this.cachedItems()[key]
            items?.removeItem(context)

            Executions.getCurrent().desktop.components.firstOrNull { it.id == ValidationController.gridId }?.also {
                it as NirdizatiGrid<SimulationJob>
                it.generate(items?.rawData() ?: listOf(), true)
            }
        }
    }

    override fun isEnabled(): Boolean {
        return context.owner == Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
    }
}