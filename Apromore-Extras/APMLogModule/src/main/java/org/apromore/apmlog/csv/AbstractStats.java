/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.apmlog.csv;

import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractStats {

    private AbstractStats() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> getCaseAttrNames(PLog log) {
        List<String> originalAttrNames = log.getImmutableCaseAttributeValues().keySet().stream().collect(Collectors.toList());
        List<String> caseAttrNames = new ArrayList<>();
        for (String s : originalAttrNames) {
            if (!s.equals("case:variant")) caseAttrNames.add(s);
        }
        return caseAttrNames;
    }

    public static String getCaseAttributeString(PTrace trace, List<String> caseAttrNames) {
        UnifiedMap<String, String> caseAttributes = trace.getAttributes();

        StringBuilder caseAttrsString = new StringBuilder();
        for (int j = 0; j < caseAttrNames.size(); j++) {
            String attrKey = caseAttrNames.get(j);
            String val = "";
            if (caseAttributes.containsKey(attrKey)) {
                val= caseAttributes.get(attrKey);
            }
            caseAttrsString.append(val + ", ");
        }
        return caseAttrsString.toString();
    }
}
