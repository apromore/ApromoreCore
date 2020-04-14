package org.apromore.apmlog.filter.rules;


import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.Section;

import java.util.Set;

public interface LogFilterRule {

    Choice getChoice();
    Inclusion getInclusion();
    Section getSection();
    String getKey();
    FilterType getFilterType();
    Set<RuleValue> getPrimaryValues();
    Set<RuleValue> getSecondaryValues();
    Set<String> getPrimaryValuesInString();
    Set<String> getSecondaryValuesInString();
    LogFilterRule clone();
}
