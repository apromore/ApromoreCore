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
package org.apromore.apmlog;

import org.apromore.apmlog.immutable.ImmutableActivity;
import org.apromore.apmlog.stats.AAttributeGraph;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (06/03/2020) - public APMLog(List<ATrace> inputTraceList)
 * Modified: Chii Chang (12/05/2020)
 * Modified: Chii Chang (27/10/2020)
 */
public interface APMLog {


    DefaultChartDataCollection getDefaultChartDataCollection();
    UnifiedMap<String, Integer> getActivityMaxOccurMap();

    ActivityNameMapper getActivityNameMapper();

    ATrace get(String caseId);

    UnifiedMap<String, ATrace> getTraceUnifiedMap();

    int getUniqueActivitySize();

    UnifiedMap<Integer, Integer> getCaseVariantIdFrequencyMap();

    UnifiedMap<String, UnifiedMap<String, Integer>> getCaseAttributeValueFreqMap();

    UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap();

    UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueCasesFreqMap();

    List<String> getCaseAttributeNameList();

    long getEventSize();

    void setEventSize(int eventSize);

    long getCaseVariantSize();

    void setCaseVariantSize(int caseVariantSize);

    List<String> getActivityNameList(int caseVariantId);

    void setVariantIdFreqMap(UnifiedMap<Integer, Integer> variantIdFreqMap);

    UnifiedMap<Integer, Integer> getVariantIdFreqMap();

    double getMinDuration();

    void setMinDuration(double minDuration);

    double getMaxDuration();

    void setMaxDuration(double maxDuration);

    List<ATrace> getImmutableTraces();

    List<ATrace> getTraceList();

    void setTraceList(List<ATrace> traceList);


    UnifiedSet<String> getEventAttributeNameSet();

    int size();

    ATrace get(int index);

    ATrace getImmutable(int index);

    int immutableSize();

    long getStartTime();

    void setStartTime(long startTime);

    long getEndTime();

    void setEndTime(long endTime);

    String getTimeZone();

    String getMinDurationString();

    String getMaxDurationString();

//    HashBiMap<Integer, String> getActIdNameMap();

    double getAverageDuration();

    double getMedianDuration();

    String getAverageDurationString();

    String getMedianDurationString();

    String getStartTimeString();

    String getEndTimeString();

    APMLog clone();

    XLog toXLog();

    AAttributeGraph getAAttributeGraph();

    UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> getEventAttributeOccurMap();

    void add(ATrace trace);

    HashBiMap<String, Integer> getActivityNameBiMap();

    void setActivityNameBiMap(HashBiMap<String, Integer> activityNameBiMap);
}
