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
package org.apromore.canoniser.yawl.internal.impl.handler;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.ConversionContext;

/**
 * 
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 * @param <T>
 * @param <E>
 */
public interface ConversionHandler<T, E> {

    /**
     * @throws CanoniserException
     */
    void convert() throws CanoniserException;

    /**
     * @param obj
     */
    void setObject(Object obj);

    /**
     * @param parent
     */
    void setConvertedParent(Object parent);

    /**
     * @param parent
     */
    void setOriginalParent(Object parent);

    /**
     * @param context
     */
    void setContext(ConversionContext context);

}
