/**
 * Copyright 2012, Felix Mannhardt
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.context;

import org.yawlfoundation.yawlschema.ExternalNetElementType;

/**
 * Adapter used to keep "ExternalNetElementType" objects in HashMaps unique by their ID.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class ElementAdapter {

    private final ExternalNetElementType obj;

    /**
     * Wrap a ExternalNetElementType object
     * 
     * @param obj
     */
    public ElementAdapter(final ExternalNetElementType obj) {
        this.obj = obj;
    }

    /**
     * Get underlying object
     * 
     * @return
     */
    public ExternalNetElementType getObj() {
        return obj;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getObj().getId().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ElementAdapter) {
            return getObj().getId().equals(((ElementAdapter) obj).getObj().getId());
        } else {
            return false;
        }
    }

}
