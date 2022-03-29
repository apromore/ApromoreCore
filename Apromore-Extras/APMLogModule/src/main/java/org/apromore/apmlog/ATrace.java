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

package org.apromore.apmlog;

import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.logobjects.ImmutableEvent;
import org.apromore.calendar.model.CalendarModel;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.List;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (06/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (17/02/2020)
 * Modified: Chii Chang (20/02/2020)
 * Modified: Chii Chang (11/04/2020)
 * Modified: Chii Chang (07/05/2020)
 * Modified: Chii Chang (19/05/2020)
 * Modified: Chii Chang (24/05/2020)
 * Modified: Chii Chang (01/06/2020)
 * Modified: Chii Chang (05/06/2020)
 * Modified: Chii Chang (07/10/2020) - include "schedule" event to activity
 * Modified: Chii Chang (13/10/2020)
 * Modified: Chii Chang (27/10/2020)
 * Modified: Chii Chang (11/11/2020)
 * Modified: Chii Chang (22/01/2021)
 * Modified: Chii Chang (26/01/2021)
 * Modified: Chii Chang (17/03/2021)
 * Modified: Chii Chang (06/05/2021)
 * Modified: Chii Chang (21/11/2021)
 *
 * ATrace is the common interface of the trace objects.
 * For immutable trace, implement it with ImmutableTrace.
 * For mutable trace, implement it with PTrace.
 */
public interface ATrace {

    // ========================================================
    // GET methods
    // ========================================================
    int getImmutableIndex();
    String getCaseId();

    /**
     * returns null if the case ID is not numeric
     * @return
     */
    Number getCaseIdDigit();

    List<ActivityInstance> getActivityInstances();

    /**
     * A string contains the activity instance name indicator.
     * For example, suppose this log has three unique activities - 'Act1', 'Act2' and 'Act3'.
     * During the initiation phase, the log can assign Act1 as 1, Act2 as 2 and Act3 as 3
     * in the ActivityNameIndicatorMap of APMLog.
     * Suppose this ATrace contains the activity instances in the order of 'Act2'->'Act3'->'Act1'
     * then this method shall returns "231'.
     * @return
     */
    String getCaseVariantIndicator();

    UnifiedMap<String, String> getAttributes();

    /**
     *
     * @return Epoch/Unix time
     */
    long getStartTime();

    /**
     *
     * @return Epoch/Unix time
     */
    long getEndTime();

    double getDuration();

    /**
     * XEvent list is immutable
     * @return the data source of the activity instances
     */
    List<ImmutableEvent> getImmutableEvents();

    /**
     *
     * @return first activity instance
     */
    ActivityInstance getFirst();

    /**
     *
     * @return last activity instance
     */
    ActivityInstance getLast();

    /**
     *
     * @param activityInstance
     * @return next activity instance of the parameter
     */
    ActivityInstance getNextOf(ActivityInstance activityInstance);

    /**
     *
     * @param activityInstance
     * @return previous activity instance of the parameter
     */
    ActivityInstance getPreviousOf(ActivityInstance activityInstance);

    /**
     *
     * @return the original log this Trace belongs
     */
    APMLog getSourceLog();

    /**
     *
     * @return a String represents the activity instance indicators
     */
    String getActivityInstancesIndicator();

    /**
     *
     * @return the activity instance indicators in array
     */
    int[] getActivityInstancesIndicatorArray();

    /**
     *
     * @return Calendar model of the source log
     */
    CalendarModel getCalendarModel();

    HashBiMap<ActivityInstance, Integer> getActivityInstanceIndexMap();

    // ========================================================
    // Operation methods
    // ========================================================

    ATrace deepClone();

}
