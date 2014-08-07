/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl;

import org.apromore.canoniser.yawl.internal.impl.context.YAWLConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandlerImpl;

/**
 * Abstract base class for a YAWL -> CPF handler
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T>
 *            type of Element to be converted
 * @param <E>
 *            type of the already converted Parent
 */
public abstract class YAWLConversionHandler<T, E> extends ConversionHandlerImpl<T, E> {

    protected static final String NET_ID_PREFIX = "N";

    protected static final String CONTROLFLOW_ID_PREFIX = "C";

    protected static final String RESOURCE_ID_PREFIX = "R";

    protected static final String DATA_ID_PREFIX = "D";

    /**
     * @return the YAWL conversion context
     */
    protected YAWLConversionContext getContext() {
        return (YAWLConversionContext) context;
    }


}