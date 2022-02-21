package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.xes.XESAttributeCodes;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeDurationFilterTest {

    private static final String ATTR_VAL = "Prepare package";

    @Test
    public void filter() throws Exception {
        APMLog log5cases = APMLogUnitTest.getImmutableLog("5cases", "files/5cases.xes");
        List<LogFilterRule> criteria = getFilterCriteria(Choice.REMOVE);
        APMLogFilter apmLogFilter = new APMLogFilter(log5cases);
        apmLogFilter.filter(criteria);
        assertTrue(apmLogFilter.getAPMLog().getActivityInstances().stream()
                .filter(x -> x.getAttributeValue(XESAttributeCodes.CONCEPT_NAME).equals(ATTR_VAL))
                .collect(Collectors.summarizingDouble(ActivityInstance::getDuration)).getMin() >= 40 * (1000 * 60));

        criteria = getFilterCriteria(Choice.RETAIN);
        apmLogFilter.filter(criteria);
        assertTrue(apmLogFilter.getAPMLog().getActivityInstances().stream()
                .filter(x -> x.getAttributeValue(XESAttributeCodes.CONCEPT_NAME).equals(ATTR_VAL))
                .collect(Collectors.summarizingDouble(ActivityInstance::getDuration)).getMax() <= 40 * (1000 * 60));
    }

    private List<LogFilterRule> getFilterCriteria(Choice choice) {
        FilterType filterType = FilterType.EVENT_ATTRIBUTE_DURATION;
        Inclusion inclusion = Inclusion.ALL_VALUES;

        double lowBoundVal = 0d;
        double upBoundVal = 40 * (1000 * 60);

        String lowBoundUnit = "days";
        String upBoundUnit = "days";

        Set<RuleValue> primaryValues = new HashSet<>();

        String attrKey = XESAttributeCodes.CONCEPT_NAME;

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, ATTR_VAL, lowBoundVal);

        ruleValue1.getCustomAttributes().put("unit", lowBoundUnit);

        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, ATTR_VAL, upBoundVal);

        ruleValue2.getCustomAttributes().put("unit", upBoundUnit);

        primaryValues.add(ruleValue1);
        primaryValues.add(ruleValue2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        return List.of(logFilterRule);
    }
}
