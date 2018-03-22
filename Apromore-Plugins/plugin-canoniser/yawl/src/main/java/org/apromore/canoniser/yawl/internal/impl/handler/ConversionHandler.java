/*
 * Copyright © 2009-2018 The Apromore Initiative.
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
package org.apromore.canoniser.yawl.internal.impl.handler;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.ConversionContext;

/**
 * Conversion Handler interface for conversion from CPF to YAWL
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 * @param <T>
 *            type of Element to be converted
 * @param <E>
 *            type of the already converted Parent
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
