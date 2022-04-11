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

package org.apromore.plugin.portal.processdiscoverer.data;

import static org.apromore.logman.attribute.graph.MeasureType.DURATION;
import static org.apromore.logman.attribute.graph.MeasureType.FREQUENCY;

import lombok.Getter;
import lombok.Setter;
import org.apromore.calendar.model.Calendars;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.logman.attribute.graph.MeasureAggregation;
import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.relation.DirectFollowReader;
import org.apromore.logman.attribute.log.relation.RelationReader;
import org.apromore.portal.util.CostTable;

/**
 * UserOptions contain user inputs on UI
 * 
 * @author Bruce Nguyen
 *
 */
public class UserOptionsData {
    
    // The initial values are set here
    
    protected boolean bpmnMode = false;
    protected boolean inverted_nodes = false;
    protected boolean inverted_arcs = false;
    protected boolean use_dynamic = false;
    protected boolean includeSecondary = false;
    
    protected double nodes_value = 100;
    protected double arcs_value = 10;
    protected double parallelism_value = 40;
 
   
    protected MeasureType fixedType = ConfigData.DEFAULT_MEASURE_TYPE;
    protected MeasureAggregation fixedAggregation = ConfigData.DEFAULT_MEASURE_AGGREGATE;
    protected MeasureRelation fixedRelation = MeasureRelation.ABSOLUTE;
    
    protected MeasureType primaryType = FREQUENCY;
    protected MeasureAggregation primaryAggregation = MeasureAggregation.CASES;
    protected MeasureRelation primaryRelation = MeasureRelation.ABSOLUTE;
    
    protected MeasureType secondaryType = DURATION;
    protected MeasureAggregation secondaryAggregation = MeasureAggregation.MEAN;
    protected MeasureRelation secondaryRelation = MeasureRelation.ABSOLUTE;
    
    protected String mainAttributeKey = "";
    
    protected boolean layoutHierarchy = false;
    protected boolean layoutDagre = false;
    protected int selectedLayout = 0; //0: hierarchical, 1: dagre_LR, 2: dagre_TB, 3: breadth-first
    protected boolean retainZoomPan = false;
    
    protected boolean frequency_total = false;
    protected boolean frequency_case = true;
    protected boolean frequency_min = false;
    protected boolean frequency_max = false;
    protected boolean frequency_mean = false;
    protected boolean frequency_median = false;
    
    protected boolean duration_total = false;
    protected boolean duration_min = false;
    protected boolean duration_max = false;
    protected boolean duration_mean = true;
    protected boolean duration_median = false;
    
    protected RelationReader relationReader = new DirectFollowReader();

    @Getter @Setter
    private CalendarModel calendarModel = Calendars.INSTANCE.absoluteCalendar().immutable();

    @Getter @Setter
    private CostTable costTable = CostTable.EMPTY;
    
    public static UserOptionsData DEFAULT(ConfigData configData) {
        UserOptionsData userOptions = new UserOptionsData();
        userOptions.setPrimaryType(FREQUENCY);
        userOptions.setPrimaryAggregation(MeasureAggregation.CASES);
        userOptions.setSecondaryType(DURATION);
        userOptions.setSecondaryAggregation(MeasureAggregation.MEAN);
        userOptions.setFixedType(FREQUENCY);
        userOptions.setFixedAggregation(MeasureAggregation.CASES);
        userOptions.setInvertedNodesMode(false);
        userOptions.setInvertedArcsMode(false);
        userOptions.setMainAttributeKey(configData.getDefaultAttribute());
        return userOptions;
    }
    
    public String getMainAttributeKey() {
        return this.mainAttributeKey;
    }
    
    public void setMainAttributeKey(String key) {
        this.mainAttributeKey = key;
    }
    
    public double getNodeFilterValue() {
        return this.nodes_value;
    }
    
    public void setNodeFilterValue(double newValue) {
        this.nodes_value = newValue;
    }
    
    public double getArcFilterValue() {
        return this.arcs_value;
    }
    
    public void setArcFilterValue(double newValue) {
        this.arcs_value = newValue;
    }
    
    public double getParallelismFilterValue() {
        return this.parallelism_value;
    }
    
    public void setParallelismFilterValue(double newValue) {
        this.parallelism_value = newValue;
    }
    
    public MeasureType getFixedType() {
        return this.fixedType;
    }
    
    public void setFixedType(MeasureType newType) {
        this.fixedType = newType;
    }
    
    public MeasureAggregation getFixedAggregation() {
        return this.fixedAggregation;
    }
    
    public void setFixedAggregation(MeasureAggregation fixed) {
        this.fixedAggregation = fixed;
    }
    
    public MeasureRelation getFixedRelation() {
        return this.fixedRelation;
    }
    
    public void setFixedRelation(MeasureRelation newRelation) {
        this.fixedRelation = newRelation;
    }
    
    public MeasureType getPrimaryType() {
        return this.primaryType;
    }
    
    public void setPrimaryType(MeasureType newType) {
        this.primaryType = newType;
    }
    
    public MeasureAggregation getPrimaryAggregation() {
        return this.primaryAggregation;
    }
    
    public void setPrimaryAggregation(MeasureAggregation newAggregate) {
        this.primaryAggregation = newAggregate;
    }
    
    public MeasureRelation getPrimaryRelation() {
        return this.primaryRelation;
    }
    
    public void setPrimaryRelation(MeasureRelation newRelation) {
        this.primaryRelation = newRelation;
    }
    
    public MeasureType getSecondaryType() {
        return this.secondaryType;
    }
    
    public void setSecondaryType(MeasureType type) {
        this.secondaryType = type;
    }
    
    public MeasureAggregation getSecondaryAggregation() {
        return this.secondaryAggregation;
    }
    
    public void setSecondaryAggregation(MeasureAggregation newAggregate) {
        this.secondaryAggregation = newAggregate;
    }
    
    public MeasureRelation getSecondaryRelation() {
        return this.secondaryRelation;
    }
    
    public void setSecondaryRelation(MeasureRelation newRelation) {
        this.secondaryRelation = newRelation;
    }
    
    public boolean getBPMNMode() {
        return bpmnMode;
    }
    
    public void setBPMNMode(boolean bpmnMode) {
        this.bpmnMode = bpmnMode;
    }
    
    public boolean getInvertedNodesMode() {
        return this.inverted_nodes;
    }
    
    public void setInvertedNodesMode(boolean inverted_nodes) {
        this.inverted_nodes = inverted_nodes;
    }
    
    public boolean getInvertedArcsMode() {
        return this.inverted_arcs;
    }
    
    public void setInvertedArcsMode(boolean inverted_arcs) {
        this.inverted_arcs = inverted_arcs;
    }
    
    public int getSelectedLayout() {
        return selectedLayout;
    }
    
    public void setSelectedLayout(int layout) {
        selectedLayout = layout;
    }
    
    public boolean getRetainZoomPan() {
        return retainZoomPan;
    }
    
    public void setRetainZoomPan(boolean retainZoomPan) {
        this.retainZoomPan = retainZoomPan;
    }
    
    public boolean getIncludeSecondary() {
        return this.includeSecondary;
    }
    
    public void setIncludeSecondary(boolean includeSecondary) {
        this.includeSecondary = includeSecondary;
    }
    
    public boolean getLayoutHierarchy() {
        return this.layoutHierarchy;
    }
    
    public void setLayoutHierarchy(boolean layoutOn) {
        this.layoutHierarchy = layoutOn;
    }
    
    public boolean getLayoutDagre() {
        return this.layoutDagre;
    }
    
    public void setLayoutDagre(boolean layoutOn) {
        this.layoutDagre = layoutOn;
    }
    
    public boolean getUseDynamic() {
        return this.use_dynamic;
    }
    
    public void setUseDynamic(boolean value) {
        this.use_dynamic = value;
    }
    
    public boolean getFrequencyTotal() {
        return this.frequency_total;
    }
    
    public void setFrequencyTotal(boolean frequency) {
        this.frequency_total = frequency;
    }
    
    public boolean getFrequencyCase() {
        return this.frequency_case;
    }
    
    public void setFrequencyCase(boolean frequency) {
        this.frequency_case = frequency;
    }
    
    public boolean getFrequencyMin() {
        return this.frequency_min;
    }
    
    public void setFrequencyMin(boolean frequency) {
        this.frequency_min = frequency;
    }
    
    public boolean getFrequencyMax() {
        return this.frequency_max;
    }
    
    public void setFrequencyMax(boolean frequency) {
        this.frequency_max = frequency;
    }
    
    public boolean getFrequencyMean() {
        return this.frequency_mean;
    }
    
    public void setFrequencyMean(boolean frequency) {
        this.frequency_mean = frequency;
    }
    
    public boolean getFrequencyMedian() {
        return this.frequency_median;
    }
    
    public void setFrequencyMedian(boolean frequency) {
        this.frequency_median = frequency;
    }
    
    
    
    public boolean getDurationTotal() {
        return this.duration_total;
    }
    
    public void setDurationCumulative(boolean duration) {
        this.duration_total = duration;
    }
    
    public boolean getDurationMin() {
        return this.duration_min;
    }
    
    public void setDurationMin(boolean duration) {
        this.duration_min = duration;
    }
    
    public boolean getDurationMax() {
        return this.duration_max;
    }
    
    public void setDurationMax(boolean duration) {
        this.duration_max = duration;
    }
    
    public boolean getDurationMean() {
        return this.duration_mean;
    }
    
    public void setDurationMean(boolean duration) {
        this.duration_mean = duration;
    }
    
    public boolean getDurationMedian() {
        return this.duration_median;
    }
    
    public void setDurationMedian(boolean duration) {
        this.duration_median = duration;
    }
    
    public RelationReader getRelationReader() {
        return relationReader;
    }

    public void setPrimaryAggregation() {
    }
}
