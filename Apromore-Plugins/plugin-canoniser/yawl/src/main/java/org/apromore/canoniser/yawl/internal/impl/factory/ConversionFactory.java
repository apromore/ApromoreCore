/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.factory;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler;

/**
 * Factory for ConversionHandlers
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public interface ConversionFactory {

    /**
     * @param obj
     * @param convertedParent
     * @param originalParent
     * @return a Conversion Handler 
     * @throws CanoniserException
     */
    ConversionHandler<? extends Object, ? extends Object> createHandler(Object obj, Object convertedParent, Object originalParent)
            throws CanoniserException;

    /**
     * @param obj
     * @param convertedParent
     * @param originalParent
     * @return
     * @throws CanoniserException
     */
    ConversionHandler<? extends Object, ? extends Object> createHandler(Object obj, Object convertedParent, Object originalParent,
            Class<? extends ConversionHandler<?, ?>> handlerClass) throws CanoniserException;

}
