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

import cs.ut.jobs.SimulationJob
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.util.Clients

class CopyURLOperation(context: SimulationJob) : Operation<SimulationJob>(context) {
    enum class URL(val value: String) {
        VALIDATION("validation?id=%s")
    }

    override fun perform() {
        val url = formBaseUrl() + URL.VALIDATION.value.format(context.id)
        Clients.evalJavaScript("copyToClipboard('$url')")
    }

    private fun formBaseUrl(): String {
        val sb = StringBuilder(Executions.getCurrent().scheme)
        sb.append("://")
        sb.append(Executions.getCurrent().serverName)

        val port = Executions.getCurrent().serverPort
        if (port != 80 && port != 443) {
            sb.append(":$port")
        }

        // Separator
        sb.append("/#")

        return sb.toString()
    }

    /**
     * Only allow on SSL port
     */
    override fun isEnabled(): Boolean = Executions.getCurrent().serverPort == 443
}