package org.apromore.plugin.portal.logfilter.generic;

import java.util.Arrays;
import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.plugin.portal.generic.PluginInputParams;
import org.deckfour.xes.model.XLog;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public class LogFilterInputParams extends PluginInputParams {
    private XLog log;
    private String classifierAttribute;
    private List<LogFilterCriterion> filterCriteria;
    
    public LogFilterInputParams(Object...objects) throws LogFilterWrongInputException {
        super(objects);
        List<Object> objectList = Arrays.asList(objects);
        
        if (this.checkInputParamsValidity()) {
            this.log = (XLog)objectList.get(0);
            this.classifierAttribute = (String)objectList.get(1);
            this.filterCriteria = (List<LogFilterCriterion>)objectList.get(2);
        }
        else {
            throw new LogFilterWrongInputException("Wrong input parameters passed to LogFilter plugin");
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

    @Override
    public boolean checkInputParamsValidity() {
        if (this.size() == 3 && 
                this.get(0) instanceof XLog && 
                this.get(1) instanceof String &&
                this.get(2) instanceof List<?>) {
            return true;
        }
        else {
            return false;
        }
    }
    
}
