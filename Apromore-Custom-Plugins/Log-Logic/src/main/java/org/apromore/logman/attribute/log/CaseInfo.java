/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.logman.attribute.log;

public class CaseInfo {
    private String caseID;
    private int caseLength;
    private int variantIndex;
    private double variantFrequency;
    
    public CaseInfo(String caseID, int caseLength, int variantIndex, double variantFrequency) {
        this.caseID=caseID;
        this.caseLength=caseLength;
        this.variantIndex=variantIndex;
        this.variantFrequency=variantFrequency;
    }
    
    public String getCaseID() {
        return this.caseID;
    }
    
    public int getCaseLength() {
        return this.caseLength;
    }
    
    public int getVariantIndex() {
        return this.variantIndex;
    }
    
    public double getVariantFrequency() {
        return variantFrequency;
    }
}
