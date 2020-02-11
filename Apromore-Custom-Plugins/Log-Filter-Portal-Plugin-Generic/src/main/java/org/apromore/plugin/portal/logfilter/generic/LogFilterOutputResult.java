package org.apromore.plugin.portal.logfilter.generic;

import java.util.Arrays;
import java.util.List;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.plugin.portal.generic.PluginOutputResult;
import org.deckfour.xes.model.XLog;

/**
 * @author Bruce Hoang Nguyen (30/08/2019)
 */
public class LogFilterOutputResult extends PluginOutputResult {
    private XLog log;
    private List<LogFilterCriterion> filterCriteria;
    
    public LogFilterOutputResult(Object...objects) throws LogFilterWrongOutputException {
        super(objects);
        List<Object> objectList = Arrays.asList(objects);
        try {
            this.log = (XLog)objectList.get(0);
            this.filterCriteria = (List<LogFilterCriterion>)objectList.get(1);
            this.resultCode = SUCCESS_CODE;
            this.resultMessage = "Success";
        }
        catch (Exception ex) {
            this.resultMessage = "Wrong output parameters returned from LogFilter plugin";
            this.resultCode = -1;
            throw new LogFilterWrongOutputException(this.resultMessage);
        }
    }
    
    public LogFilterOutputResult(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
    
    public XLog getLog() {
        return this.log;
    }
    
    public List<LogFilterCriterion> getFilterCriteria() {
        return this.filterCriteria;
    }
}
