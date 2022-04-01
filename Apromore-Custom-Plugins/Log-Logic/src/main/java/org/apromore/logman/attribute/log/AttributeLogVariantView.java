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

import org.apromore.logman.attribute.log.variants.AttributeTraceVariants;

public class AttributeLogVariantView {
    private AttributeTraceVariants originalVariants;
    private AttributeTraceVariants activeVariants;
    
    public AttributeLogVariantView(AttributeLog attLog) {
        originalVariants = new AttributeTraceVariants(attLog, true);
        activeVariants = new AttributeTraceVariants(attLog, false);
    }
    
    protected void reset() {
        originalVariants.reset();
        activeVariants.reset();
    }
    
    protected void addOriginalTrace(AttributeTrace trace) {
        originalVariants.add(trace);
    }
    
    protected void addActiveTrace(AttributeTrace trace) {
        activeVariants.add(trace);
    }
    
    protected void resetActiveData() {
        activeVariants.reset();
    }
    
    protected void finalUpdate() {
        activeVariants.sortVariantsByFrequency();
    }
    
    public AttributeTraceVariants getOriginalVariants() {
        return originalVariants;
    }
    
    public AttributeTraceVariants getActiveVariants() {
        return activeVariants;
    } 
}
