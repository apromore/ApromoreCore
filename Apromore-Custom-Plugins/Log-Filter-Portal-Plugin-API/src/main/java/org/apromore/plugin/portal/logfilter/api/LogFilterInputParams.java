package org.apromore.plugin.portal.logfilter.api;

import java.util.Arrays;
import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XLog;

public class LogFilterInputParams extends PluginParams {
    private XLog log;
    private String classifierAttribute;
    private List<LogFilterCriterion> filterCriteria;
    
    public LogFilterInputParams(Object...objects) throws WrongInputParamsException {
        super(objects);
        List<Object> objectList = Arrays.asList(objects);
        try {
            this.log = (XLog)objectList.get(0);
            this.classifierAttribute = (String)objectList.get(1);
            this.filterCriteria = (List<LogFilterCriterion>)objectList.get(2);
        }
        catch (Exception ex) {
            throw new WrongInputParamsException("Wrong input parameters passed to LogFilter plugin");
        }
    }
    
    public XLog getLog() {
        return this.log;
    }
    
    public String getClassifierAttribute() {
        return this.classifierAttribute;
    }
    
    public List<LogFilterCriterion> getFilterCriteria() {
        return this.filterCriteria;
    }
    
}
