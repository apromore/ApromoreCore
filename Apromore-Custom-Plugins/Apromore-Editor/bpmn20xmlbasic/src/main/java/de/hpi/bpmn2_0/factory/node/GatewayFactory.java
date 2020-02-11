
package de.hpi.bpmn2_0.factory.node;

/*-
 * #%L
 * Signavio Core Components
 * %%
 * Copyright (C) 2006 - 2020 Philipp Berger, Martin Czuchra, Gero Decker,
 * Ole Eckermann, Lutz Gericke,
 * Alexander Hold, Alexander Koglin, Oliver Kopp, Stefan Krumnow,
 * Matthias Kunze, Philipp Maschke, Falko Menge, Christoph Neijenhuis,
 * Hagen Overdick, Zhen Peng, Nicolas Peters, Kerstin Pfitzner, Daniel Polak,
 * Steffen Ryll, Kai Schlichting, Jan-Felix Schwarz, Daniel Taschik,
 * Willi Tscheschner, Björn Wagner, Sven Wagner-Boysen, Matthias Weidlich
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * 
 * Ext JS (http://extjs.com/) is used under the terms of the Open Source LGPL 3.0
 * license.
 * The license and the source files can be found in our SVN repository at:
 * http://oryx-editor.googlecode.com/.
 * #L%
 */

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.TGatewayType;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;
import de.hpi.bpmn2_0.model.gateway.ComplexGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGatewayType;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayDirection;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;

/**
 * The factory to create {@link Gateway} BPMN 2.0 elements
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId({ 
	"Exclusive_Databased_Gateway",  
	"ParallelGateway", 
	"EventbasedGateway", 
	"InclusiveGateway", 
	"ComplexGateway" })
public class GatewayFactory extends AbstractShapeFactory {
	
	// @Override
	public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent, State state)
			throws BpmnConverterException {
		
		BPMNElement element = super.createBpmnElement(shape, parent, state);
		if(element.getNode() instanceof ExclusiveGateway) {
			BPMNShape bpmnShape = (BPMNShape) element.getShape();
			String markerVisible = shape.getProperty("markervisible");
			if(markerVisible != null && markerVisible.equals("true")) {
				bpmnShape.setIsMarkerVisible(Boolean.TRUE);
			} else {
				bpmnShape.setIsMarkerVisible(Boolean.FALSE);
			}
		}

		return element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		try {
			Gateway gateway = (Gateway) this.invokeCreatorMethod(shape);
			this.identifyGatewayDirection(gateway, shape);

			// Synergia "configurable" extension element
			if ("true".equals(shape.getProperty("configurable"))) {
				Configurable configurable = new Configurable();
                                gateway.getOrCreateExtensionElements().getAny().add(configurable);

                                // If there's a valid configuration, add a <configuration> child to the <configurable>
                                String type = shape.getProperty("configuration");
                                if (type != null && !"unconfigured".equals(type)) {
                                        try {
                                                Configurable.Configuration c = new Configurable.Configuration();
				                if ("InclusiveGateway".equals(shape.getStencilId())) {
                                                        c.setType(TGatewayType.fromValue(type));
					        }

					        switch (gateway.getGatewayDirection()) {
					        case CONVERGING:
					        case MIXED:
					                Set configuredIncomings = new HashSet();
						        for (Object incoming : shape.getIncomingsReadOnly()) {
							        if (!"true".equals(((GenericShape) incoming).getPropertyBoolean("absentinconfiguration"))) {
							                 configuredIncomings.add(incoming);
							         }
						        }
						        if (!configuredIncomings.isEmpty()) {
							        state.configurableGatewaySourceMap.put(c, configuredIncomings);
						        }
					        }

					        switch (gateway.getGatewayDirection()) {
					        case DIVERGING:
					        case MIXED:
						        Set configuredOutgoings = new HashSet();
						        for (Object outgoing : shape.getOutgoingsReadOnly()) {
							        if (!"true".equals(((GenericShape) outgoing).getProperty("absentinconfiguration"))) {
								        configuredOutgoings.add(outgoing);
							        }
						        }
						        if (!configuredOutgoings.isEmpty()) {
							        state.configurableGatewayTargetMap.put(c, configuredOutgoings);
						        }
					        }

                                                configurable.setConfiguration(c);
                                        }
                                        catch(IllegalArgumentException e) {
                                                throw new BpmnConverterException("\"" + type + "\" is not a gateway type in configuration of " + gateway.getId(), e);
                                        }
                                        catch(NullPointerException e) {
                                                throw new BpmnConverterException("Null gateway type in configuration of " + gateway.getId(), e);
                                        }
                                }
			}

			return gateway;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}

	/**
	 * Creator method for an exclusive databased Gateway.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The resulting {@link ExclusiveGateway}
	 */
	@StencilId("Exclusive_Databased_Gateway")
	public ExclusiveGateway createExclusiveGateway(GenericShape shape) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}

	@StencilId("ParallelGateway")
	public ParallelGateway createParallelGateway(GenericShape shape) {
		ParallelGateway gateway = new ParallelGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}
	
	@StencilId("EventbasedGateway")
	public EventBasedGateway createEventBasedGateway(GenericShape shape) {
		EventBasedGateway gateway = new EventBasedGateway();
		
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
//		String instantiate = shape.getProperty("instantiate");
//		
//		if(instantiate != null && instantiate.equals("true"))
//			gateway.setInstantiate(true);
//		else
//			gateway.setInstantiate(false);
		
		/* Set gateway type and instantiation */
		gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
		gateway.setInstantiate(false);
		
		String type = shape.getProperty("eventtype");
		if(type != null) {
			if(type.equalsIgnoreCase("instantiate_parallel")) {
				gateway.setEventGatewayType(EventBasedGatewayType.PARALLEL);
				gateway.setInstantiate(true);
			} else if(type.equalsIgnoreCase("instantiate_exclusive")) {
				gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
				gateway.setInstantiate(true);
			}
		}
		
//		if(type != null && type.equalsIgnoreCase("instantiate_parallel")) 
//			gateway.setEventGatewayType(EventBasedGatewayType.PARALLEL);
//		else 
//			gateway.setEventGatewayType(EventBasedGatewayType.EXCLUSIVE);
		
		return gateway;
	}
	
	@StencilId("InclusiveGateway")
	public InclusiveGateway createInclusiveGateway(GenericShape shape) {
		InclusiveGateway gateway = new InclusiveGateway();
		
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
		return gateway;
	}
	
	@StencilId("ComplexGateway")
	public ComplexGateway createComplexGateway(GenericShape shape) {
		ComplexGateway gateway = new ComplexGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		
		String activationCondition = shape.getProperty("activationcondition");
		if(activationCondition != null && !activationCondition.equals("")) {
			gateway.setActivationCondition(new FormalExpression(activationCondition));
		}
		
		return gateway;
	}
	
	/**
	 * Determines and sets the {@link GatewayDirection}
	 */
	private void identifyGatewayDirection(Gateway gateway, GenericShape shape) {

		/* Determine the direction of the Gateway */

		int numIncomming = countSequenceFlows(shape.getIncomingsReadOnly());
		int numOutgoing = countSequenceFlows(shape.getOutgoingsReadOnly());

		GatewayDirection direction = GatewayDirection.UNSPECIFIED;

		if (numIncomming > 1 && numOutgoing > 1)
			direction = GatewayDirection.MIXED;
		else if (numIncomming <= 1 && numOutgoing > 1)
			direction = GatewayDirection.DIVERGING;
		else if (numIncomming > 1 && numOutgoing <= 1)
			direction = GatewayDirection.CONVERGING;

		/* Set the gateway direction */
		gateway.setGatewayDirection(direction);
	}
	
	/**
	 * Counts the number of sequence flows contained in the list.
	 * @param edges
	 */
	private int countSequenceFlows(List<GenericShape> edges) {
		int i = 0;
		
		for(GenericShape edge : edges) {
			if(edge.getStencilId().equals("SequenceFlow")) {
				i++;
			}
		}
		
		return i;
	}

}
