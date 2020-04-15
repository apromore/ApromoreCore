/*
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2018-2020 The University of Melbourne.
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.processdiscoverer.data;

import java.text.DecimalFormat;

public class CaseDetails {

    private String caseId;
    private int caseEvents;
    private int caseVariantId;
    private double caseVariantFreq;
    private String caseVariantFreqStr;

    private final DecimalFormat decimalFormat = new DecimalFormat("##############0.##");

    public CaseDetails(String caseId, int caseEvents, int caseVariantId, double caseVariantFreq) {
        this.caseId = caseId;
        this.caseEvents = caseEvents;
        this.caseVariantId = caseVariantId;
        this.caseVariantFreq = caseVariantFreq;
        this.caseVariantFreqStr = decimalFormat.format(100 * caseVariantFreq);
    }

    public String getCaseId () {
        return caseId;
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
