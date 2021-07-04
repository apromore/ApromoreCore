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

import java.text.DecimalFormat;

public class CaseDetails {

    private final String caseId;
    private Number caseIdDigit;
    private final int caseEvents;
    private final int caseVariantId;
    private final double caseVariantFreq;
    private final String caseVariantFreqStr;

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");

    private CaseDetails(String caseId, Number caseIdDigit, int caseEvents, int caseVariantId, double caseVariantFreq) {
        this.caseId = caseId;
        this.caseIdDigit = caseIdDigit;
        this.caseEvents = caseEvents;
        this.caseVariantId = caseVariantId;
        this.caseVariantFreq = caseVariantFreq;
        this.caseVariantFreqStr = decimalFormat.format(100 * caseVariantFreq);
    }
    
    public static CaseDetails valueOf(String caseId, Number caseIdDigit, int caseEvents, int caseVariantId, double caseVariantFreq) {
        return new CaseDetails(caseId, caseIdDigit, caseEvents, caseVariantId, caseVariantFreq);
    }

    public String getCaseId () {
        return caseId;
    }

    public Number getCaseIdDigit() {
        return caseIdDigit;
    }

    public int getCaseEvents() {
        return caseEvents;
    }

    public int getCaseVariantId() {
        return caseVariantId;
    }

    public double getCaseVariantFreq() {
        return caseVariantFreq;
    }

    public String getCaseVariantFreqStr() {
        return caseVariantFreqStr;
    }

}
