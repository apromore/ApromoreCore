/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.zk.notification;

import org.zkoss.zk.ui.util.Clients;

/**
 * Utilities to abstract notification
 */
public final class Notification {

    /**
     * Escape characters for JS string within single quotes
     *
     * @param message Message to be sanitized
     * @return
     */
    public static final String sanitize(String message) {
        return message.replace("'", "\\'");
    }

    /**
     * Display notification
     *
     * @param message String message
     * @param type NotificationType.INFO or NotificationType.ERROR
     */
    public static final void show(String message, NotificationType type) {
        Clients.evalJavaScript("Ap.common.notify('" + Notification.sanitize(message) + "','" + type.toString() + "');");
    }

    public static final void info(String message) {
        Notification.show(message, NotificationType.INFO);
    }

    public static final void error(String message) {
        Notification.show(message, NotificationType.ERROR);
    }
}
