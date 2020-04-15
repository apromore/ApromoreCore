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

package org.apromore.logman.attribute.log;

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
    
    protected void add(AttributeTrace trace, boolean isActive) {
        originalVariants.add(trace);
        if (isActive && !trace.isEmpty()) activeVariants.add(trace);
    }
    
    protected void resetActive() {
        activeVariants.reset();
    }
    
    protected void addActive(AttributeTrace trace, boolean isActive) {
        if (isActive && !trace.isEmpty()) activeVariants.add(trace);
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
