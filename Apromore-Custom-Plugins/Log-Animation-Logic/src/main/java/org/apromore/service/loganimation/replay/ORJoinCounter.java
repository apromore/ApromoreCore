/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.service.loganimation.replay;

/*
* Implement counter for one ORJoinLabel
*/
public class ORJoinCounter {
    private int value = 0;
    private boolean isIgnored = false;
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int newValue) {
        this.value = newValue;
    }
    
    public boolean isIgnored() {
        return isIgnored;
    }
    
    public void setIgnored(boolean isIgnored) {
        this.isIgnored = isIgnored;
    }
    
    public void reset() {
        value = 0;
        isIgnored = false;
    }
}
