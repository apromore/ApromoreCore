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

import java.util.HashMap;
import java.util.Map;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalNoOpHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalProcessHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.EdgeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.EventTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.MessageTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.NetTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.StateTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.TaskTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations.AnnotationsTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations.EdgeGraphicsTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations.NetGraphicsTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations.NodeGraphicsTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.data.InputExpressionTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.data.ObjectRefTypeHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.data.OutputExpressionTypeHandler;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory Class for Canonical Format through which all Handlers are created. Basically the mapping between Input objects and their Handler classes.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class CanonicalConversionFactory implements ConversionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalConversionFactory.class.getName());

    @SuppressWarnings("serial")
    static final Map<String, Class<? extends CanonicalElementHandler<?, ?>>> HANDLER_MAP = new HashMap<String, Class<? extends CanonicalElementHandler<?, ?>>>() {
        {
            // Root
            put(CanonicalProcessType.class.getName(), CanonicalProcessHandler.class);
            // Control Flow
            put(TaskType.class.getName(), TaskTypeHandler.class);
            put(EventType.class.getName(), EventTypeHandler.class);
            put(StateType.class.getName(), StateTypeHandler.class);
            put(MessageType.class.getName(), MessageTypeHandler.class);
            // Edges
            put(EdgeType.class.getName(), EdgeHandler.class);
            // Already rewritten by Macro
            put(XORJoinType.class.getName(), CanonicalNoOpHandler.class);
            put(XORSplitType.class.getName(), CanonicalNoOpHandler.class);
            put(ORSplitType.class.getName(), CanonicalNoOpHandler.class);
            put(ORJoinType.class.getName(), CanonicalNoOpHandler.class);
            put(ANDSplitType.class.getName(), CanonicalNoOpHandler.class);
            put(ANDJoinType.class.getName(), CanonicalNoOpHandler.class);
            // Data
            put(InputExpressionType.class.getName(), InputExpressionTypeHandler.class);
            put(OutputExpressionType.class.getName(), OutputExpressionTypeHandler.class);
            put(ObjectRefType.class.getName(), ObjectRefTypeHandler.class);
            // Annotations
            put(AnnotationsType.class.getName(), AnnotationsTypeHandler.class);
            put(DocumentationType.class.getName(), CanonicalNoOpHandler.class);
            // Ignore all unkown Annotations
            put(AnnotationType.class.getName(), CanonicalNoOpHandler.class);
        }
    };

    private final CanonicalConversionContext context;

    public CanonicalConversionFactory(final CanonicalConversionContext context) {
        this.context = context;
    }

    public CanonicalConversionContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.factory.ConversionFactory#createHandler(java.lang.Object, java.lang.Object, java.lang.Object,
     * java.lang.Class)
     */
    @Override
    public ConversionHandler<? extends Object, ? extends Object> createHandler(final Object obj, final Object convertedParent,
            final Object originalParent, final Class<? extends ConversionHandler<?, ?>> handlerClass) throws CanoniserException {
        try {
            final ConversionHandler<?, ?> handler = handlerClass.newInstance();
            initialiseHandler(obj, convertedParent, originalParent, handler);
            return handler;
        } catch (final InstantiationException e) {
            throw new CanoniserException(e);
        } catch (final IllegalAccessException e) {
            throw new CanoniserException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.factory.ConversionFactory#createHandler(java.lang.Object, java.lang.Object, java.util.Set)
     */
    @Override
    public ConversionHandler<?, ?> createHandler(final Object obj, final Object convertedParent, final Object originalParent)
            throws CanoniserException {

        CanonicalElementHandler<?, ?> conversionHandler = null;

        if (obj != null) {

            if (obj instanceof NetType) {
                conversionHandler = new NetTypeHandler();
            } else if (obj instanceof GraphicsType) {
                if (isNetLayout((GraphicsType) obj)) {
                    conversionHandler = new NetGraphicsTypeHandler();
                } else if (isElementLayout((GraphicsType) obj)) {
                    conversionHandler = new NodeGraphicsTypeHandler();
                } else if (isEdgeLayout((GraphicsType) obj)) {
                    conversionHandler = new EdgeGraphicsTypeHandler();
                } else {
                    LOGGER.warn("Ignoring layout information as no Handler matches! For object: " + obj.toString());
                    conversionHandler = new CanonicalNoOpHandler();
                }
            } else if (obj instanceof NodeType) {
                if (getContext().getControlFlowContext().getElementInfo(((NodeType) obj).getId()) != null) {
                    // Node has already been converted i.e. merged with another Node -> Do nothing
                    conversionHandler = new CanonicalNoOpHandler();
                }
            }

            // Usual mapping based on JAXB types
            try {
                if (HANDLER_MAP.get(obj.getClass().getName()) != null) {
                    conversionHandler = HANDLER_MAP.get(obj.getClass().getName()).newInstance();
                }
            } catch (final InstantiationException e) {
                throw new CanoniserException(e);
            } catch (final IllegalAccessException e) {
                throw new CanoniserException(e);
            }
        } else {
            throw new IllegalArgumentException("Can not create Handler for NULL object!");
        }

        if (conversionHandler != null) {
            // LOGGER.debug("Converting CPF/ANF object " + obj.toString() + " using Handler " + conversionHandler.getClass().getSimpleName());
            initialiseHandler(obj, convertedParent, originalParent, conversionHandler);
            return conversionHandler;
        }

        throw new CanoniserException("Could not find conversion handler for object of class " + obj.getClass().getName());
    }

    private boolean isEdgeLayout(final GraphicsType obj) {
        return getContext().getEdgeById(obj.getCpfId()) != null;
    }

    private boolean isElementLayout(final GraphicsType obj) {
        return getContext().getNodeById(obj.getCpfId()) != null;
    }

    private boolean isNetLayout(final GraphicsType obj) {
        return getContext().getNetById(obj.getCpfId()) != null;
    }

    private void initialiseHandler(final Object obj, final Object convertedParent, final Object originalParent, final ConversionHandler<?, ?> handler) {
        handler.setContext(getContext());
        handler.setObject(obj);
        if (convertedParent != null) {
            handler.setConvertedParent(convertedParent);
        }
        if (originalParent != null) {
            handler.setOriginalParent(originalParent);
        }
    }

}
