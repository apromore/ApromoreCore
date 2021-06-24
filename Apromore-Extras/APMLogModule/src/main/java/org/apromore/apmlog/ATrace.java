/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.BitSet;
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
 * Modified: Chii Chang (21/06/2021)
 */
public interface ATrace {


    UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap();

    void addActivity(AActivity aActivity);

    int getImmutableIndex();

    int getMutableIndex();

    void setMutableIndex(int mutableIndex);

    String getCaseId();

    void setCaseVariantId(int caseVariantId);

    int getCaseVariantId();

    int getEventSize();

    long getStartTimeMilli();

    long getEndTimeMilli();

    double getDuration();

    boolean isHasActivity();

    void setHasActivity(boolean opt);

    List<AActivity> getActivityList();

    List<String> getActivityNameList();

    UnifiedSet<String> getEventNameSet();

    UnifiedMap<String, String> getAttributeMap();

    List<AEvent> getEventList();

    int size();

    AEvent get(int index);

    double getTotalProcessingTime();

    double getAverageProcessingTime();

    double getMaxProcessingTime();

    double getTotalWaitingTime();

    double getAverageWaitingTime();

    double getMaxWaitingTime();

    double getCaseUtilization();

    BitSet getValidEventIndexBitSet();

    String getStartTimeString();

    String getEndTimeString();

    String getDurationString();

    long getCaseIdDigit();

    List<Integer> getActivityNameIndexList();

    void setCaseVariantIdForDisplay(int caseVariantIdForDisplay);

    int getCaseVariantIdForDisplay();

    void addEvent(AEvent event);

    void setEventList(List<AEvent> eventList);

    List<AEvent> getImmutableEvents();

    void setImmutableEvents(List<AEvent> events);

    DoubleArrayList getWaitingTimes();
    DoubleArrayList getProcessingTimes();

    void setStartTimeMilli(long startTimeMilli);
    void setEndTimeMilli(long endTimeMilli);
    String getActivityNameIndexString(HashBiMap<String, Integer> nameIndexBiMap);
}
