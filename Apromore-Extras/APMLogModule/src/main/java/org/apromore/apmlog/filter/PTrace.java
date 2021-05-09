/*-
 * #%L
 * Process Discoverer Logic
 *
 * This file is part of "Apromore".
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


package org.apromore.apmlog.filter;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.immutable.ImmutableTrace;
import org.apromore.apmlog.stats.StatsUtil;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides pointers of ATrace (of APMLog) used in filterlogic.
 * It can output a new ATrace (of APMLog) based on the valid event Id index (validEventIndexBS)
 * @author Chii Chang
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (06/03/2020)
 * Modified: Chii Chang (12/03/2020)
 * Modified: Chii Chang (24/05/2020)
 * Modified: Chii Chang (26/05/2020)
 * Modified: Chii Chang (07/10/2020) - include "schedule" event to activity
 * Modified: Chii Chang (11/11/2020)
 * Modified: Chii Chang (23/12/2020)
 * Modified: Chii Chang (26/01/2021)
 * Modified: Chii Chang (17/03/2021)
 * Modified: Chii Chang (21/04/2021)
 * Modified: Chii Chang (05/05/2021)
 */
public class PTrace implements Comparable<PTrace>, ATrace{

    private ATrace aTrace;
    private BitSet validEventIndexBS;
    private String ratio;
    private double oppActivitySize;

    private int mutableIndex;

    private PLog pLog;
    private final List<AActivity> activityList = new ArrayList<>();

    // ==================================================
    // Case variant ID of PTrace is a mutable value
    // ==================================================
    private int caseVariantId;

    public PTrace(int mutableIndex, ATrace aTrace, PLog pLog) {
        this.mutableIndex = mutableIndex;
        this.aTrace = aTrace;
        this.pLog = pLog;

        caseVariantId = aTrace.getCaseVariantId();

        int[] existIndexesArray = aTrace.getEventList().stream().mapToInt(x -> x.getIndex()).toArray();
        IntArrayList ial = new IntArrayList(existIndexesArray);

        validEventIndexBS = new BitSet(ial.max() + 1);
        for (AEvent event : aTrace.getEventList()) {
            validEventIndexBS.set(event.getIndex());
        }

        setActivities(aTrace.getActivityList());
    }

    public void setActivities(List<AActivity> activities) {
        this.activityList.clear();
        this.activityList.addAll(activities);

        int index = 0;
        for (AActivity activity : this.activityList) {
            activity.setMutableTraceIndex(mutableIndex);
            activity.setMutableIndex(index);
            index += 1;
        }
    }

    public ATrace getOriginalATrace() {
        return aTrace;
    }

    public DoubleArrayList getWaitingTimes() {
        List<AActivity> validActs = StatsUtil.getValidActivitiesOf(this);
        double[] waitTimesArray = validActs.stream()
                .filter(x -> StatsUtil.getValidPreviousActivity(x, this) != null)
                .mapToDouble(x -> StatsUtil.getArcDurationOf(Objects.requireNonNull(StatsUtil.getValidPreviousActivity(x, this)), x))
                .toArray();
        DoubleArrayList wtDal = new DoubleArrayList(waitTimesArray);
        return wtDal;
    }

    public DoubleArrayList getProcessingTimes() {
        List<AActivity> validActs = StatsUtil.getValidActivitiesOf(this);
        double[] procTimesArray = validActs.stream()
                .mapToDouble(AActivity::getDuration)
                .toArray();
        DoubleArrayList ptDal = new DoubleArrayList(procTimesArray);
        return ptDal;
    }

    public void updateStats(int mutableTraceIndex) {
        this.mutableIndex = mutableTraceIndex;
        List<AActivity> updatedActs = aTrace.getActivityList().stream()
                .filter(x -> validEventIndexBS.get(x.getEventIndexes().get(0)))
                .collect(Collectors.toList());



        setActivities(updatedActs);
    }

    @Override
    public void setStartTimeMilli(long startTimeMilli) {

    }

    @Override
    public void setEndTimeMilli(long endTimeMilli) {

    }

    @Override
    public ATrace clone() {
        return null;
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return null;
    }

    @Override
    public void addActivity(AActivity aActivity) {

    }

    public int getImmutableIndex() {
        return aTrace.getImmutableIndex();
    }

    public int getMutableIndex() {
        return mutableIndex;
    }

    @Override
    public void setMutableIndex(int mutableIndex) {

    }

    public String getCaseId() {
        return aTrace.getCaseId();
    }

    @Override
    public void setCaseVariantId(int caseVariantId) {
        this.caseVariantId = caseVariantId;
    }

    public long getCaseIdDigit() {
        return aTrace.getCaseIdDigit();
    }

    @Override
    public List<Integer> getActivityNameIndexList() {
        return null;
    }

    @Override
    public void setCaseVariantIdForDisplay(int caseVariantIdForDisplay) {

    }

    @Override
    public int getCaseVariantIdForDisplay() {
        return this.caseVariantId;
    }

    @Override
    public void addEvent(AEvent event) {

    }

    @Override
    public void setEventList(List<AEvent> eventList) {

    }

    @Override
    public List<AEvent> getImmutableEvents() {
        return null;
    }

    @Override
    public void setImmutableEvents(List<AEvent> events) {

    }

    public int getCaseVariantId() {
        return this.caseVariantId;
    }

    public void setValidEventIndexBS(BitSet validEventIndexBS) {
        this.validEventIndexBS = validEventIndexBS;
    }

    public int getEventSize() {
        return validEventIndexBS.cardinality();
    }

    public long getStartTimeMilli() {
        return getActivityList().get(0).getStartTimeMilli();
    }

    public long getEndTimeMilli() {
        List<AActivity> activities = getActivityList();
        return activities.get(activities.size() - 1).getEndTimeMilli();
    }

    public double getDuration() {
        long et = getEndTimeMilli();
        long st = getStartTimeMilli();
        return et - st > 0 ? et - st : 0;
    }

    @Override
    public boolean isHasActivity() {
        return false;
    }

    @Override
    public void setHasActivity(boolean opt) {

    }

    private DoubleArrayList getAllTimestamps() {
        double[] array = getEventList().stream()
                .mapToDouble(x -> x.getTimestampMilli())
                .toArray();
        DoubleArrayList dal = new DoubleArrayList(array);
        return dal;
    }

    public List<AEvent> getOriginalEventList() {
        return aTrace.getEventList();
    }

    public List<AActivity> getOriginalActivityList() {
        return aTrace.getActivityList();
    }

    public List<AActivity> getActivityList() {
        return StatsUtil.getValidActivitiesOf(this);
    }

    public List<String> getActivityNameList() {
        return getActivityList().stream().map(x -> x.getName()).collect(Collectors.toList());
    }

    @Override
    public UnifiedSet<String> getEventNameSet() {
        return null;
    }

    public UnifiedMap<String, String> getAttributeMap() {
        Map<String, String> collect = aTrace.getAttributeMap().entrySet().stream()
                .filter(x -> !x.getKey().equals("concept:name") && !x.equals("case:variant") )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new UnifiedMap<>(collect);
    }

    public List<AEvent> getEventList() {
        return aTrace.getImmutableEvents().stream()
                .filter(x -> validEventIndexBS.get(x.getIndex()))
                .collect(Collectors.toList());
    }

    public int size() {
        return getEventSize();
    }

    public AEvent get(int index) {
        return getEventList().get(index);
    }

    public double getTotalProcessingTime() {
        return getProcessingTimes().sum();
    }

    public double getAverageProcessingTime() {
        return getProcessingTimes().average();
    }

    public double getMaxProcessingTime() {
        return getProcessingTimes().max();
    }

    public double getTotalWaitingTime() {
        return getWaitingTimes().sum();
    }

    public double getAverageWaitingTime() {
        return getWaitingTimes().average();
    }

    public double getMaxWaitingTime() {
        return getWaitingTimes().max();
    }

    public double getCaseUtilization() {
        double ttlPT = getTotalProcessingTime();
        double ttlWT = getTotalWaitingTime();
        double dur = getDuration();

        double utilization = ttlPT > 0 && ttlWT > 0 ? ttlPT / (ttlPT + ttlWT) :
                (ttlPT > 0 && ttlPT < dur ? ttlPT / dur : 1.0);

        return utilization;
    }

    public BitSet getValidEventIndexBitSet() {
        return validEventIndexBS;
    }

    public String getStartTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(getStartTimeMilli()));
    }

    public String getEndTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(getEndTimeMilli()));
    }

    public String getDurationString() {
        return Util.durationShortStringOf(getDuration());
    }

    public BitSet getOriginalValidEventIndexBS() {

        int[] existIndexesArray = aTrace.getEventList().stream().mapToInt(x -> x.getIndex()).toArray();
        IntArrayList ial = new IntArrayList(existIndexesArray);

        BitSet bs = new BitSet(ial.max() + 1);
        for (AEvent event : aTrace.getEventList()) {
            bs.set(event.getIndex());
        }

        return bs;
    }

    public UnifiedMap<String, String> getAllAttributes() { return aTrace.getAttributeMap(); }

    public long getActivitySize() {
        return getActivityList().size();
    }

    public String getActivitySizeRatio() {
        return ratio;
    }

    public void setActivitySizeRatio(String ratio) {
        this.ratio = ratio;
    }

    public void setOppActivitySize(double oppActivitySize) {
        this.oppActivitySize = oppActivitySize;
    }

    public double getOppActivitySize() {
        return oppActivitySize;
    }

    public String getActivityNameIndexString(HashBiMap<String, Integer> nameIndexBiMap) {
        try {
            StringBuilder sb = new StringBuilder();
            for (AActivity activity : getActivityList()) {
                sb.append(nameIndexBiMap.get(activity.getName()));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public AActivity getNextActivityOf(AActivity activity) {
        // ========================================================
        // Ensure to updateStats() before calling this method
        // ========================================================

        return activityList.stream()
                .filter(x -> x.getImmutableIndex() > activity.getImmutableIndex())
                .findFirst()
                .orElse(null);
    }

    public AActivity getPreviousActivityOf(AActivity activity) {
        // ========================================================
        // Ensure to updateStats() before calling this method
        // ========================================================
        if (activityList.get(0) == activity) return null;

        List<AActivity> reversed = new ArrayList<>(activityList);
        Collections.reverse(reversed);

        return reversed.stream()
                .filter(x -> x.getImmutableIndex() < activity.getImmutableIndex())
                .findFirst()
                .orElse(null);
    }

    public ATrace toATrace() {

        ImmutableTrace trace = new ImmutableTrace(aTrace.getImmutableIndex(), mutableIndex, aTrace.getCaseId(),
                aTrace.getAttributeMap());

        for (AActivity activity : getActivityList()) {
            trace.addActivity(activity);
        }

        trace.setEventList(getEventList());
        trace.setImmutableEvents(aTrace.getImmutableEvents());
        trace.setCaseVariantId(getCaseVariantId());
        trace.setHasActivity(true);
        trace.setWaitingTimes(getWaitingTimes());
        trace.setProcessingTimes(getProcessingTimes());
        trace.setStartTimeMilli(getStartTimeMilli());
        trace.setEndTimeMilli(getEndTimeMilli());

        return trace;
    }

    @Override
    public int compareTo(PTrace o) {
        if (Util.isNumeric(getCaseId()) && Util.isNumeric(o.getCaseId())) {
            if (getCaseIdDigit() > o.getCaseIdDigit()) return 1;
            else if (getCaseIdDigit() < o.getCaseIdDigit()) return -1;
            else return 0;
        } else {
            return getCaseId().compareTo(o.getCaseId());
        }
    }
}
