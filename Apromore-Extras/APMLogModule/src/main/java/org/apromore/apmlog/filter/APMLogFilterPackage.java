
package org.apromore.apmlog.filter;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.deckfour.xes.model.XLog;

import java.util.List;

/**
 * @author Chii Chang (Last modified: 28/01/2020)
 * Modified: Chii Chang (11/04/2020)
 */
public class APMLogFilterPackage { 

    private String filteredLogName;
    private APMLog filteredAPMLog;
    private PLog pLog;
    private XLog filteredXLog;
    private List<LogFilterRule> criteria;
    public APMLogFilterPackage(APMLog apmLog,
                               PLog pLog,
                               XLog filteredXLog,
                               List<LogFilterRule> criteria) {
        this.filteredAPMLog = filteredAPMLog;
        this.pLog = pLog;
        this.filteredXLog = filteredXLog;
        this.criteria = criteria;
    }
    public APMLogFilterPackage(String filteredLogName,
                               APMLog filteredAPMLog,
                               PLog pLog,
                               XLog filteredXLog,
                               List<LogFilterRule> criteria) {
        this.filteredLogName = filteredLogName;
        this.filteredAPMLog = filteredAPMLog;
        this.pLog = pLog;
        this.filteredXLog = filteredXLog;
        this.criteria = criteria;
    }

    public String getFilteredLogName() {
        return filteredLogName;
    }

    public APMLog getFilteredAPMLog() {
        return filteredAPMLog;
    }

    public PLog getPLog() {
        return pLog;
    }

    public XLog getFilteredXLog() {
        return filteredXLog;
    }

    public List<LogFilterRule> getCriteria() {
        return criteria;
    }

}
