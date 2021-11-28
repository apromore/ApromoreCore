/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import org.apromore.apmlog.util.Util;

import java.util.regex.Pattern;

public class CaseDetails {

    private final String caseId;
    private Number caseIdDigit;
    private final String caseIdString;
    private final int caseEvents;
    private final int caseVariantId;

    private final Pattern nonNumPattern = Pattern.compile("[^0-9.]+");
    private final Pattern numPattern = Pattern.compile("[0-9.]+");

    private CaseDetails(String caseId, int caseEvents, int caseVariantId) {
        this.caseId = caseId;
        this.caseEvents = caseEvents;
        this.caseVariantId = caseVariantId;
        this.caseIdString = numPattern.matcher(caseId).replaceAll("");
        String numberOnly = nonNumPattern.matcher(caseId).replaceAll("");
        this.caseIdDigit = Util.isNumeric(numberOnly) ? Double.valueOf(numberOnly) : Long.MIN_VALUE;
    }
    
    public static CaseDetails valueOf(String caseId, int caseEvents, int caseVariantId) {
        return new CaseDetails(caseId, caseEvents, caseVariantId);
    }

    public String getCaseId () {
        return caseId;
    }

    public Number getCaseIdDigit() {
        return caseIdDigit;
    }

    public String getCaseIdString() {
        return caseIdString;
    }

    public int getCaseEvents() {
        return caseEvents;
    }

    public int getCaseVariantId() {
        return caseVariantId;
    }

}
