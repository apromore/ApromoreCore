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

package cs.ut.charts

import org.zkoss.zk.ui.util.Clients
import java.io.File

/**
 * Line chart server side data representation for client side line chart (e.g. accuracy comparison)
 */
class LineChart(val id: String, name: String, payload: String, private val numberOfEvents: Int, file: File) : Chart(name, payload, file) {

    override fun render() {
        Clients.evalJavaScript(
            "lineChart('$payload','$id','$numberOfEvents', '${name.toUpperCase()}')"
        )
    }
}