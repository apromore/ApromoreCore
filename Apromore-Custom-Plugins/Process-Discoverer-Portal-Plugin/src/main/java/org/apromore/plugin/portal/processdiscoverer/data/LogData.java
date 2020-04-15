/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer.data;

import java.util.ArrayList;
import java.util.List;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.logman.ALog;
import org.apromore.logman.attribute.AbstractAttribute;
import org.apromore.logman.attribute.AttributeType;
import org.apromore.logman.attribute.IndexableAttribute;
import org.apromore.logman.attribute.log.AttributeInfo;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeLogSummary;
import org.apromore.logman.attribute.log.CaseInfo;
import org.apromore.logman.utils.TimeConverter;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.ListIterable;

/**
 * LogData contains all log data needed for calculation or display on UI
 * 
 * @author Bruce Nguyen
 *
 */
public class LogData {
    protected ALog aLog;
    protected AttributeLog attLog;
    protected AttributeLogSummary attLogSummary;
    protected Object currentFilterCriteria = new ArrayList<LogFilterRule>(); // list of log filter criteria
    protected IndexableAttribute mainAttribute;
    protected ImmutableList<AbstractAttribute> indexableAttributes;
    protected TimeConverter timeConverter = new TimeConverter();

    public LogData(ConfigData configData, ALog log) {
        this.aLog = log;
        indexableAttributes = aLog.getAttributeStore().getIndexableEventAttributeWithLimits(
                configData.getMaxNumberOfUniqueValues(), AttributeType.BOOLEAN);
    }
    
    public ALog getLog() {
        return this.aLog;
    }
    
    public AttributeLog getAttributeLog() {
        return this.attLog;
    }       
    
    public List<CaseDetails> getCaseDetails() {
        return new ArrayList<CaseDetails>();
    }
    
    public List<PerspectiveDetails> getActivityDetails() {
        return new ArrayList<PerspectiveDetails>();
    }
    
    //////////////////////// Data /////////////////////////////
    
    public void setMainAttribute(String key) throws NotFoundAttributeException  {
        IndexableAttribute newAttribute = null;
        for (AbstractAttribute att : indexableAttributes) {
            if (att.getKey().equals(key)) {
                newAttribute = (IndexableAttribute)att;
                break;
            }
        }
        
        long timer = 0;
        if (newAttribute != null) {
            if (mainAttribute != newAttribute) {
                mainAttribute = newAttribute;
                if (attLog == null) {
                    timer = System.currentTimeMillis();
                    attLog = new AttributeLog(aLog, mainAttribute);
                    attLogSummary = attLog.getLogSummary();
                    System.out.println("Create AttributeLog for the perspective attribute: " + (System.currentTimeMillis() - timer) + "ms.");
                }
                else {
                    timer = System.currentTimeMillis();
                    attLog.setAttribute(mainAttribute);
                    attLogSummary = attLog.getLogSummary();
                    System.out.println("Update AttributeLog to the new perspective attribute: " + (System.currentTimeMillis() - timer) + "ms.");
                }
                
            }
        }
        else {
            throw new NotFoundAttributeException("Cannot find an attribute in ALog with key = " + key);
        }
    }
    
    public AttributeLogSummary getLogSummary() {
        return this.attLog.getLogSummary();
    }
    
    public ImmutableList<AbstractAttribute> getAvailableAttributes() {
        return this.indexableAttributes;
    }
    
    public IndexableAttribute getMainAttribute() {
        return this.mainAttribute;
    }
    
    public ListIterable<CaseInfo> getCaseInfoList() {
        return this.attLog.getCaseInfoList();
    }
    
    public ListIterable<AttributeInfo> getAttributeInfoList() {
        return this.attLog.getAttributeInfoList();
    }

    //////////////////////// Filter /////////////////////////////
    
    public Object getCurrentFilterCriteria() {
        return this.currentFilterCriteria;
    }
    
    public void setCurrentFilterCriteria(Object criteria) {
        this.currentFilterCriteria = criteria;
    }
    
    public boolean filter_RemoveTracesAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return false;
    }
    
    public boolean filter_RetainTracesAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return false;
    }
    
    public boolean filter_RemoveTracesAllValueOfEventAttribute(String value, String attKey) throws Exception {
        return false;
    }
    
    public boolean filter_RetainTracesAllValueOfEventAttribute(String value, String attKey) throws Exception {
        return false;
    }
    
    public boolean filter_RemoveEventsAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return false;
    }
    
    public boolean filter_RetainEventsAnyValueOfEventAttribute(String value, String attKey) throws Exception {
        return false;
    }
    
    public boolean filter_RemoveTracesAnyValueOfDirectFollowRelation(String value, String attKey) throws Exception {
        return false;
    }
    
    public boolean filter_RetainTracesAnyValueOfDirectFollowRelation(String value, String attKey) throws Exception {
        return false;
    }

    
    
    //////////////////////// Statistics /////////////////////////////
    
    public String getFilteredStartTime() {
        return timeConverter.convertMilliseconds(attLogSummary.getLogMinTime());
    }

    public String getFilteredEndTime() {
        return timeConverter.convertMilliseconds(attLogSummary.getLogMaxTime());
    }

    public String getFilteredMinDuration() {
        return timeConverter.convertMilliseconds(attLogSummary.getTraceDurationMin());
    }

    public String getFilteredMedianDuration() {
        return timeConverter.convertMilliseconds(attLogSummary.getTraceDurationMedian());
    }

    public String getFilteredMeanDuration() {
        return timeConverter.convertMilliseconds(attLogSummary.getTraceDurationMean());
    }

    public String getFilteredMaxDuration() {
        return timeConverter.convertMilliseconds(attLogSummary.getTraceDurationMax());
    }
    
}
