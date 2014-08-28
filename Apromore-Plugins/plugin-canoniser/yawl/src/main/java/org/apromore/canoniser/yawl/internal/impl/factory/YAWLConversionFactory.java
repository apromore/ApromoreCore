/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.YAWLConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.NetHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.SpecificationHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLNoOpHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.ConditionHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.InputConditionHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.OutputConditionHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.TaskHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.TimerTaskHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow.WSInvokerTaskHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.data.InputVariableHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.data.LocalVariableHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.data.OutputVariableHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.resource.AutomatedTaskResourceingHandler;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.resource.ResourceingHandler;
import org.yawlfoundation.yawlschema.DecompositionType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.MultipleInstanceExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.ResourcingExternalInteractionType;
import org.yawlfoundation.yawlschema.ResourcingFactsType;
import org.yawlfoundation.yawlschema.VariableFactsType;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

/**
 * Factory Class for YAWL through which all Handlers are created. Basically the mapping between Input objects and their Handler classes.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class YAWLConversionFactory implements ConversionFactory {

    @SuppressWarnings("serial")
    static final Map<String, Class<? extends YAWLConversionHandler<?, ?>>> HANDLER_MAP = new HashMap<String, Class<? extends YAWLConversionHandler<?, ?>>>() {
        {
            // Root
            put(YAWLSpecificationFactsType.class.getName(), SpecificationHandler.class);

            // Control Flow
            put(NetFactsType.class.getName(), NetHandler.class);
            put(OutputConditionFactsType.class.getName(), OutputConditionHandler.class);
            put(ExternalTaskFactsType.class.getName(), TaskHandler.class);
            put(MultipleInstanceExternalTaskFactsType.class.getName(), TaskHandler.class);
            put(WebServiceGatewayFactsType.class.getName(), YAWLNoOpHandler.class);

            // Data
            put(VariableFactsType.class.getName(), LocalVariableHandler.class);
            put(InputParameterFactsType.class.getName(), InputVariableHandler.class);
            put(OutputParameterFactsType.class.getName(), OutputVariableHandler.class);
        }
    };

    /**
     * Context of the conversion acts as "glue" between all Handlers
     */
    private YAWLConversionContext context;

    public YAWLConversionFactory(final YAWLConversionContext context) {
        this.setContext(context);
        getContext().setHandlerFactory(this);
    }

    public YAWLConversionContext getContext() {
        return context;
    }

    private void setContext(final YAWLConversionContext context) {
        this.context = context;
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

        YAWLConversionHandler<?, ?> conversionHandler = null;

        if (obj != null) {
            if (obj instanceof ExternalConditionFactsType) {
                if (isInputCondition((ExternalConditionFactsType) obj)) {
                    conversionHandler = new InputConditionHandler();
                } else {
                    conversionHandler = new ConditionHandler();
                }
            } else if (obj instanceof ExternalTaskFactsType && isWSInvokerTask((ExternalTaskFactsType) obj)) {
                conversionHandler = new WSInvokerTaskHandler();
            } else if (obj instanceof ExternalTaskFactsType && isTimerTask((ExternalTaskFactsType) obj)) {
                conversionHandler = new TimerTaskHandler();
            } else if (obj instanceof ResourcingFactsType) {
                if (isAutomaticTask((ExternalTaskFactsType) originalParent)) {
                    conversionHandler = new AutomatedTaskResourceingHandler();
                } else {
                    conversionHandler = new ResourceingHandler();
                }
            } else {
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
            }
        } else {
            throw new IllegalArgumentException("Can not create Handler for null Object!");
        }

        if (conversionHandler != null) {
            // LOGGER.debug("Converting YAWL object " + obj.toString() + " using Handler " + conversionHandler.getClass().getSimpleName());
            initialiseHandler(obj, convertedParent, originalParent, conversionHandler);
            return conversionHandler;
        }

        throw new CanoniserException("Could not find conversion handler for object of class " + obj.getClass().getName());
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

    private boolean isTimerTask(final ExternalTaskFactsType obj) {
        return obj.getTimer() != null;
    }

    private boolean isWSInvokerTask(final ExternalTaskFactsType obj) {
        if (obj.getDecomposesTo() != null) {
            final DecompositionType d = getContext().getDecompositionByID(obj.getDecomposesTo().getId());
            if (d instanceof WebServiceGatewayFactsType) {
                final WebServiceGatewayFactsType taskDecomposition = (WebServiceGatewayFactsType) d;
                return taskDecomposition.getYawlService() != null && taskDecomposition.getYawlService().getId().contains("WSInvoker");
            }
        }
        return false;
    }

    private boolean isInputCondition(final ExternalConditionFactsType obj) {
        return getContext().getPreSet(obj).size() == 0;
    }

    protected boolean isAutomaticTask(final ExternalTaskFactsType task) {
        return task.getDecomposesTo() != null
                && ((WebServiceGatewayFactsType) getContext().getDecompositionByID(task.getDecomposesTo().getId())).getExternalInteraction() != null
                && ((WebServiceGatewayFactsType) getContext().getDecompositionByID(task.getDecomposesTo().getId())).getExternalInteraction().equals(
                        ResourcingExternalInteractionType.AUTOMATED);
    }

}
