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

package cs.ut.util

import org.zkoss.util.resource.Labels
import org.zkoss.zk.ui.Desktop
import org.zkoss.zk.ui.Executions
import org.zkoss.zk.ui.event.Event
import org.zkoss.zk.ui.util.Clients

class NirdizatiTranslator {
    companion object {
        fun localizeText(text: String, vararg args: Any): String = Labels.getLabel(text, args) ?: text

        fun showNotificationAsync(text: String, desktop: Desktop, type: String = "info") {
            Executions.schedule(
                desktop,
                { _ ->
                    Clients.showNotification(
                        text,
                        type,
                        desktop.components.first { it.id == MAINLAYOUT },
                        "bottom_left",
                        3000,
                        true
                    )
                },
                Event("notification", null, "push")
            )
        }
    }
}