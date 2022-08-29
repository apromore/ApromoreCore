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
package org.apromore.plugin.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This was a drop-in replacement for {@link LoggerFactory} for use in the ZK framework
 * based Apromore presentation layer.
 *
 * The {@link Logger} instances produced by this factory will make sure that the
 * ZK session attributes (particularly the authenticated user) are made available
 * to the logging system's MDC.
 * Its function has been replaced by ZkExecutionListener, so it's pending removal.
 */
public abstract class PortalLoggerFactory {

    /**
     * This method is a drop-in replacement for {@link org.slf4j.LoggerFactory#getLogger(Class)}.
     *
     * @param klass  the class in which the logged events will occur
     * @return a logger which maintains the MDC according to the ZK session
     */
    public static Logger getLogger(Class klass) {
        return LoggerFactory.getLogger(klass);
    }

    /**
     * This method is a drop-in replacement for {@link org.slf4j.LoggerFactory#getLogger(String)}.
     *
     * @param name  of the created logger
     * @return a logger which maintains the MDC according to the ZK session
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}
