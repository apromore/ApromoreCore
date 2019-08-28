package org.apromore.plugin.portal.logfilter.api;

import java.util.Arrays;
import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XLog;

public class LogFilterOuputParams extends PluginParams {
    private XLog log;
    private List<LogFilterCriterion> filterCriteria;
    
    public LogFilterOuputParams(Object...objects) throws WrongOutputParamsException {
        super(objects);
        List<Object> objectList = Arrays.asList(objects);
        try {
            this.log = (XLog)objectList.get(0);
            this.filterCriteria = (List<LogFilterCriterion>)objectList.get(1);
        }
        catch (Exception ex) {
            throw new WrongOutputParamsException("Wrong output parameters returned from LogFilter plugin");
        }
    }
    
    public XLog getLog() {
        return this.log;
    }
    
    public List<LogFilterCriterion> getFilterCriteria() {
        return this.filterCriteria;
    }
}
