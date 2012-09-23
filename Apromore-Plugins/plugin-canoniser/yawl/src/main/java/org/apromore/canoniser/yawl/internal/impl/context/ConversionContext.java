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

import org.apromore.canoniser.yawl.internal.impl.factory.ConversionFactory;
import org.apromore.canoniser.yawl.internal.utils.ConversionUUIDGenerator;

/**
 * Base class for both conversion contexts containing common functionality.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class ConversionContext {

    /**
     * Factory to create conversion Handlers
     */
    private ConversionFactory handlerFactory;

    /**
     * Unique Universal Identifier Generator for YAWL elements
     */
    private final ConversionUUIDGenerator uuidGenerator;

    /**
     * Create a new ConversionContext for use during one conversion.
     */
    public ConversionContext() {
        super();
        this.uuidGenerator = new ConversionUUIDGenerator();
    }

    /**
     * Set the HandlerFactory that should be used to do the conversion.
     * 
     * @param handlerFactory
     */
    public void setHandlerFactory(final ConversionFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    /**
     * @return factory through which Handlers for every YAWL element is created
     */
    public ConversionFactory getHandlerFactory() {
        return handlerFactory;
    }

    /**
     * An unique id generator that keeps track of generated IDs
     * 
     * @return ConversionUUIDGenerator
     */
    public ConversionUUIDGenerator getUuidGenerator() {
        return uuidGenerator;
    }

    /**
     * Returns a specially concatenated Edge id
     * 
     * @param sourceId
     * @param targetId
     * @return concatenated Edge id
     */
    public String buildEdgeId(final String sourceId, final String targetId) {
        return sourceId + "-" + targetId;
    }

}
