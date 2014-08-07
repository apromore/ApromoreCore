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
package org.apromore.canoniser.yawl.internal.impl.context;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.MessageManager;
import org.apromore.canoniser.yawl.internal.impl.factory.ConversionFactory;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler;
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

    private final MessageManager messageInterface;

    /**
     * Create a new ConversionContext for use during one conversion.
     * @param messageInterface
     */
    public ConversionContext(final MessageManager messageInterface) {
        super();
        this.messageInterface = messageInterface;
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
    private ConversionFactory getHandlerFactory() {
        return handlerFactory;
    }

    public ConversionHandler<? extends Object, ? extends Object> createHandler(final Object obj, final Object convertedParent, final Object originalParent) throws CanoniserException {
        return getHandlerFactory().createHandler(obj, convertedParent, originalParent);
    }

    public ConversionHandler<? extends Object, ? extends Object> createHandler(final Object obj, final Object convertedParent, final Object originalParent,
            final Class<? extends ConversionHandler<?, ?>> handlerClass) throws CanoniserException {
        return getHandlerFactory().createHandler(obj, convertedParent, originalParent, handlerClass);
    }

    /**
     * An unique id generator that keeps track of generated IDs
     *
     * @return ConversionUUIDGenerator
     */
    public ConversionUUIDGenerator getUuidGenerator() {
        return uuidGenerator;
    }

    public MessageManager getMessageInterface() {
        return messageInterface;
    }

}
