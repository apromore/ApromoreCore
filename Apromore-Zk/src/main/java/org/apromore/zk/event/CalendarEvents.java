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

package org.apromore.zk.event;

public abstract class CalendarEvents {

    private CalendarEvents() {
    }

    public static final String TOPIC = "org/apromore/plugin/calendar";

    public static final String ON_CALENDAR_PUBLISH = "onCalendarPublish";
    public static final String ON_CALENDAR_ABANDON = "onCalendarAbandon";
    public static final String ON_CALENDAR_BEFORE_REMOVE = "onCalendarBeforeRemove";
    public static final String ON_CALENDAR_REMOVE = "onCalendarRemove";
    public static final String ON_CALENDAR_CHANGED = "onCalendarChanged";
    public static final String ON_CALENDAR_REFRESH = "onCalendarRefresh";
    public static final String ON_CALENDAR_LINK = "onCalendarLink";
    public static final String ON_CALENDAR_UNLINK = "onCalendarUnlink";
}
