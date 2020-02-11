
package de.hpi.bpmn2_0.transformation;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicNode;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.oryxeditor.server.diagram.label.LabelSettings;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.Documentation;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.AdHocSubProcess;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.Transaction;
import de.hpi.bpmn2_0.model.activity.loop.LoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.MultiInstanceLoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.StandardLoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.type.BusinessRuleTask;
import de.hpi.bpmn2_0.model.activity.type.ManualTask;
import de.hpi.bpmn2_0.model.activity.type.ReceiveTask;
import de.hpi.bpmn2_0.model.activity.type.ScriptTask;
import de.hpi.bpmn2_0.model.activity.type.SendTask;
import de.hpi.bpmn2_0.model.activity.type.ServiceTask;
import de.hpi.bpmn2_0.model.activity.type.UserTask;
import de.hpi.bpmn2_0.model.artifacts.Group;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.BPMNPlane;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.bpmndi.di.LabeledEdge;
import de.hpi.bpmn2_0.model.bpmndi.di.Node;
import de.hpi.bpmn2_0.model.bpmndi.di.Shape;
import de.hpi.bpmn2_0.model.choreography.CallChoreography;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.DataAssociation;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.conversation.CallConversation;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.conversation.SubConversation;
import de.hpi.bpmn2_0.model.data_object.AbstractDataObject;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.bpmn2_0.model.data_object.InputOutputSpecification;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CancelEventDefinition;
import de.hpi.bpmn2_0.model.event.CatchEvent;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.ConditionalEventDefinition;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.ErrorEventDefinition;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.EventDefinition;
import de.hpi.bpmn2_0.model.event.IntermediateCatchEvent;
import de.hpi.bpmn2_0.model.event.IntermediateThrowEvent;
import de.hpi.bpmn2_0.model.event.LinkEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.event.TerminateEventDefinition;
import de.hpi.bpmn2_0.model.event.TimerEventDefinition;
import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.PropertyListItem;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioDataObjectType;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioLabel;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMessageName;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioType;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;
import de.hpi.bpmn2_0.model.gateway.ComplexGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayWithDefaultFlow;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import de.hpi.bpmn2_0.model.misc.Assignment;
import de.hpi.bpmn2_0.model.misc.Property;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.Participant;

/**
 * A @link Visitor which converts a BPMN element to a JSON object, used
 * by @link BPMN2DiagramConverter.
 *
 * Only diagram and process elements are treated; choreography and conversation elements are largely ignored.
 *
 * @author Simon Raboczi
 */
class BPMN2DiagramConverterVisitor extends AbstractVisitor {

	/** Logger, named after the canonical class name. */
	private static final Logger logger = Logger.getLogger(BPMN2DiagramConverterVisitor.class.getCanonicalName());

	/**
	 * The diagram which @link shape is intended to contain.
	 *
	 * This class doesn't actually add @link shape to the diagram.
	 */
	private BasicShape diagram;

	/** A reverse mapping of the <code>bpmnElement</code> attribute links from the BPMN XML. */
	private Map<BaseElement,DiagramElement> bpmndiMap;

	/** Sequence flows which are configured to be absent. */
	private Set<SequenceFlow> absentInConfiguration;

	/** X-offset of the graphical origin. */
	private double parentOriginX;

	/** Y-offset of the graphical origin. */
	private double parentOriginY;

	/** The shape this visitor is populating. */
	private BasicEdge shape = new BasicEdge("UNIDENTIFED-EDGE");

	/**
	 * Constructor.
	 *
         * @param diagram  the diagram under construction
	 * @param bpmndiMap  reverse mapping for the <code>bpmnElement</code> attribute
	 * @param absentInConfiguration  sequence flows which have been configured absent
	 * @throws IllegalArgumentException if <var>diagram</var> or <var>bpmndiMap</var> are <code>null</code>
         */
	BPMN2DiagramConverterVisitor(BasicShape diagram, Map<BaseElement,DiagramElement> bpmndiMap, Set<SequenceFlow> absentInConfiguration, double originX, double originY) {
		if (diagram               == null) { throw new IllegalArgumentException("Null diagram"); }
		if (bpmndiMap             == null) { throw new IllegalArgumentException("Null bpmndiMap"); }
		assert absentInConfiguration != null;

		this.diagram               = diagram;
		this.bpmndiMap             = bpmndiMap;
		this.absentInConfiguration = absentInConfiguration;
		this.parentOriginX         = originX;
		this.parentOriginY         = originY;
	}
	
	/**
	 * @return the constructed JSON object
	 */
	BasicShape getShape() { return shape; }

	// Visit diagram elements

	@Override public void visitBpmnEdge(BPMNEdge that) {
		super.visitBpmnEdge(that);
		
		logger.finer("BPMNEdge.bpmnElement=" + that.getBpmnElement());

                if (that.getBpmnElement() == null) {
                    logger.warning(that.getId() + " lacks a bpmnElement");
                    return;
                }

		deriveDockersFromWaypoints(that,
		                           shape,
		                           that.getSourceElement() instanceof Shape ? ((Shape) that.getSourceElement()).getBounds() : null,
		                           that.getTargetElement() instanceof Shape ? ((Shape) that.getTargetElement()).getBounds() : null);

		that.getBpmnElement().acceptVisitor(this);
	}

	private static void deriveDockersFromWaypoints(LabeledEdge that,
	                                               BasicEdge shape,
	                                               de.hpi.bpmn2_0.model.bpmndi.dc.Bounds sourceBounds,
	                                               de.hpi.bpmn2_0.model.bpmndi.dc.Bounds targetBounds) {

		// Bounding box limits
		double minX = Double.POSITIVE_INFINITY,
		       maxX = Double.NEGATIVE_INFINITY,
		       minY = Double.POSITIVE_INFINITY,
		       maxY = Double.NEGATIVE_INFINITY;

		// Traverse middle waypoints, whose docker coordinates are relative to the plane
		List<de.hpi.bpmn2_0.model.bpmndi.dc.Point> waypointList = that.getWaypoint();
		assert waypointList.size() >= 2;
		// The first and last waypoints correspond to the source and target of the edge, so we ignore them
		for (de.hpi.bpmn2_0.model.bpmndi.dc.Point bpmnPoint : that.getWaypoint()) {
			logger.finer("Waypoint (" + bpmnPoint.getX() + ", " + bpmnPoint.getY() + ")");

			// Extend bounds to include this waypoint
			if (bpmnPoint.getX() < minX) { minX = bpmnPoint.getX(); }
			if (bpmnPoint.getX() > maxX) { maxX = bpmnPoint.getX(); }
			if (bpmnPoint.getY() < minY) { minY = bpmnPoint.getY(); }
			if (bpmnPoint.getY() > maxY) { maxY = bpmnPoint.getY(); }
			logger.finer("X = [" + minX + ", " + maxX + "]; Y = [" + minY + ", " + maxY + "]");

			// Determine the origin of JSON's coordinate system for this waypoint
			double originX, originY;
			if (bpmnPoint == waypointList.get(0) && sourceBounds != null) {
				// Relative to the edge's source shape
				de.hpi.bpmn2_0.model.bpmndi.dc.Bounds bounds = sourceBounds;
				originX = bounds.getX();
				originY = bounds.getY();
			}
			else if ((bpmnPoint == waypointList.get(waypointList.size() - 1) && targetBounds != null)) {
				// Relative to the edge's target shape
				de.hpi.bpmn2_0.model.bpmndi.dc.Bounds bounds = targetBounds;
				originX = bounds.getX();
				originY = bounds.getY();
			}
			else {
				// Relative to the BPMN plane
				originX = 0;
				originY = 0;
			}

			shape.addDocker(new Point(bpmnPoint.getX() - originX, bpmnPoint.getY() - originY));
		}

		// Set the bounding box for this edge
		{
			Point upperLeft  = new Point(minX, minY),
			      lowerRight = new Point(maxX, maxY);
			shape.setBounds(new Bounds(new Point(upperLeft), new Point(lowerRight)));
		}
	}

	@Override public void visitBpmnShape(BPMNShape that) {
		super.visitBpmnShape(that);

		if (that.getExtension() != null) {

			BasicShape casShape = null;

			for (Object o : that.getExtension().getAny()) {
				if (o instanceof ConfigurationAnnotationShape) {
					ConfigurationAnnotationShape cas = (ConfigurationAnnotationShape) o;
					de.hpi.bpmn2_0.model.bpmndi.dc.Bounds bounds = cas.getBounds();

					// Construct the JSON configuration annotation
					casShape = new BasicNode(that.getBpmnElement().getId() + "_cas");
					casShape.setStencilId("ConfigurationAnnotation");
					casShape.setBounds(new Bounds(new Point(bounds.getX(),
					                                        bounds.getY()),
					                              new Point(bounds.getX() + bounds.getWidth(),
					                                        bounds.getY() + bounds.getHeight())));

					diagram.addChildShape(casShape);
				}
			}

			// If there's also a configuration annotation association, it's implicitly connected to the annotation
			// (casShape remains in scope here, and will be used to do this)
			for (Object o : that.getExtension().getAny()) {
				if (o instanceof ConfigurationAnnotationAssociation) {
					ConfigurationAnnotationAssociation caa = (ConfigurationAnnotationAssociation) o;

					// Construct the Json configuration annotation association
					BasicEdge childShape = new BasicEdge(that.getBpmnElement().getId() + "_caa");
					childShape.setStencilId("Association_Undirected");
					if (casShape != null) {
						casShape.addOutgoingAndUpdateItsIncomings(childShape);
						childShape.setSourceAndUpdateIncomings(casShape);
					}
					else {
						logger.warning(caa.getId() + " does not have a corresponding annotation");
					}
					childShape.setTargetAndUpdateOutgoings(shape);

					deriveDockersFromWaypoints(caa,
					                           childShape,
					                           casShape == null ? null : toBounds(casShape.getBounds()),
					                           toBounds(shape.getBounds()));

					diagram.addChildShape(childShape);
				}
			}
		}

                if (that.getBpmnElement() == null) {
                    logger.warning(that.getId() + " has no bpmnElement");
                } else {
		    that.getBpmnElement().acceptVisitor(this);
                }
	}

	/**
	 * Convert Oryx @link Bounds to BPMN {@link de.hpi.bpmn2_0.model.bpmndi.dc.Bounds}.
	 */
	private static de.hpi.bpmn2_0.model.bpmndi.dc.Bounds toBounds(Bounds in) {

		de.hpi.bpmn2_0.model.bpmndi.dc.Bounds out = new de.hpi.bpmn2_0.model.bpmndi.dc.Bounds();

		out.setX(in.getUpperLeft().getX());
		out.setY(in.getUpperLeft().getY());
		out.setWidth(in.getWidth());
		out.setHeight(in.getHeight());

		return out;
	}

	@Override public void visitDiagramElement(DiagramElement that) {
		super.visitDiagramElement(that);

		if (that.getExtension() != null) {
			for (Object o : that.getExtension().getAny()) {
				logger.finer(that.getId() + " has extension " + o);
			}
		}

		for (Map.Entry<QName, String> entry  : that.getOtherAttributes().entrySet()) {
			logger.finer(that.getId() + " has other attribute " + entry);
		}
	}

	@Override public void visitShape(Shape that) {
		super.visitShape(that);

		Bounds bounds = new Bounds();
        try {
            bounds.setCoordinates(that.getBounds().getX() - parentOriginX,
                    that.getBounds().getY() - parentOriginY,
                    that.getBounds().getX() + that.getBounds().getWidth() - parentOriginX,
                    that.getBounds().getY() + that.getBounds().getHeight() - parentOriginY);
        }catch (NullPointerException e){
            bounds.setCoordinates(0,0,100,100);
        }
		shape.setBounds(bounds);
	}

	// Visit process elements

	@Override public void visitAbstractDataObject(AbstractDataObject that) {
		super.visitAbstractDataObject(that);

		shape.setProperty("iscollection", that.isIsCollection());
		if (that.getDataState() != null) {
			shape.setProperty("state", that.getDataState().getName());
		}
	}

	@Override public void visitActivity(Activity that) {
		super.visitActivity(that);

		logger.finer(that.getId() + " has " + that.getAdditionalProperties().size() + " additional properties");
		for (PropertyListItem item : that.getAdditionalProperties()) {
			logger.finest("* " + item);
		}

		logger.finer("Activity has " + that.getBoundaryEventRefs().size() + " boundary events");
		for (BoundaryEvent boundaryEvent : that.getBoundaryEventRefs()) {
			shape.addOutgoingAndUpdateItsIncomings(new BasicEdge(boundaryEvent.getId()));
		}

		shape.setProperty("completionquantity", that.getCompletionQuantity().longValue());

		logger.finer("Activity has " + that.getDataInputAssociation().size() + " data input associations");
		for (DataInputAssociation dataInputAssociation : that.getDataInputAssociation()) {
                        if (bpmndiMap.get(dataInputAssociation) == null) {
				logger.warning("DataInputAssociation " + dataInputAssociation.getId() + " lacks a BPMNEdge");
                        }
			else {
				BPMN2DiagramConverterVisitor subVisitor = new BPMN2DiagramConverterVisitor(diagram, bpmndiMap, absentInConfiguration, parentOriginX, parentOriginY);
				bpmndiMap.get(dataInputAssociation).acceptVisitor(subVisitor);
				diagram.addChildShape(subVisitor.getShape());
			}
		}

		logger.finer("Activity has " + that.getDataOutputAssociation().size() + " data output associations");
		for (DataOutputAssociation dataOutputAssociation : that.getDataOutputAssociation()) {
                        if (bpmndiMap.get(dataOutputAssociation) == null) {
				logger.warning("DataOutputAssociation " + dataOutputAssociation.getId() + " lacks a BPMNEdge");
                        }
			else {
				BPMN2DiagramConverterVisitor subVisitor = new BPMN2DiagramConverterVisitor(diagram, bpmndiMap, absentInConfiguration, parentOriginX, parentOriginY);
				bpmndiMap.get(dataOutputAssociation).acceptVisitor(subVisitor);
				diagram.addChildShape(subVisitor.getShape());
			}
		}

		if (that.getLoopCharacteristics() == null) {
			shape.setProperty("looptype", "None");
		}
		else {
			if (that.getLoopCharacteristics() instanceof MultiInstanceLoopCharacteristics) {
				MultiInstanceLoopCharacteristics milc = (MultiInstanceLoopCharacteristics) that.getLoopCharacteristics();

				if (milc.getBehavior() != null) {
					shape.setProperty("behavior", milc.getBehavior().value());
				}

				if (milc.getCompletionCondition() != null) {
					shape.setProperty("completioncondition", milc.getCompletionCondition().toExportString());
				}

				if (milc.getComplexBehaviorDefinition() != null && milc.getComplexBehaviorDefinition().size() > 0) {
					if (milc.getComplexBehaviorDefinition().size() > 1) 
						throw new IllegalArgumentException(that.getId() + " has a multi instance loop characteristic with multiple complex behaviors, the handling of which is not implemented");
					shape.setProperty("complexbehaviordefinition",
						milc.getComplexBehaviorDefinition().get(0).getCondition().toExportString());
				}

				if (milc.getInputDataItem() != null) {
					shape.setProperty("inputdataitem", milc.getInputDataItem().getName());
				}

				if (milc.getLoopCardinality() != null) {
					shape.setProperty("loopcardinality", milc.getLoopCardinality().toExportString());
				}

				if (milc.getLoopDataInput() != null) {
					shape.setProperty("loopdatainput", milc.getLoopDataInput().getItemSubjectRef());
				}

				if (milc.getLoopDataOutput() != null) {
					shape.setProperty("loopdataoutput", milc.getLoopDataOutput().getItemSubjectRef());
				}

				if (milc.getNoneBehaviorEventRef() != null) {
					shape.setProperty("nonebehavioreventRef", milc.getNoneBehaviorEventRef().getId());
				}

				if (milc.getOneBehaviorEventRef() != null) {
					shape.setProperty("onebehavioreventRef", milc.getOneBehaviorEventRef().getId());
				}

				if (milc.getOutputDataItem() != null) {
					shape.setProperty("outputdataitem", milc.getOutputDataItem().getName());
				}

				shape.setProperty("looptype", milc.isIsSequential() ? "Sequential" : "Parallel");
			}
			else if (that.getLoopCharacteristics() instanceof StandardLoopCharacteristics) {
				StandardLoopCharacteristics slc = (StandardLoopCharacteristics) that.getLoopCharacteristics();

				shape.setProperty("looptype", "Standard");
			}
		}

		logger.finer(that.getId() + " has " + that.getProperty().size() + " properties");
		for (Property property : that.getProperty()) {
			logger.finest("* " + property);
		}

		shape.setProperty("startquantity", that.getStartQuantity().longValue());

		shape.setProperty("isforcompensation", that.isForCompensation());
	}

	@Override public void visitAdHocSubProcess(AdHocSubProcess that) {
		super.visitAdHocSubProcess(that);

		shape.setProperty("isadhoc", true);
		shape.setProperty("adhoccancelremaininginstances", that.isCancelRemainingInstances());
		shape.setProperty("adhoccompletioncondition",      that.getCompletionCondition().toExportString());
		shape.setProperty("adhocordering",                 that.getOrdering().value());
	}

	@Override public void visitAssociation(Association that) {
		super.visitAssociation(that);

		switch (that.getAssociationDirection()) {
		case NONE:
			shape.setStencilId("Association_Undirected");
			break;
		case ONE:
			shape.setStencilId("Association_Unidirectional");
			break;
		case BOTH:
			shape.setStencilId("Association_Bidirectional");
			break;
		}
	}

	@Override public void visitBaseElement(BaseElement that) {
		super.visitBaseElement(that);

		// BPMN supports multiple documentation elements, but JSON only supports one
		if (that.getDocumentation().size() > 0) {
			shape.setProperty("documentation", that.getDocumentation().get(0).getText());
			if (that.getDocumentation().size() > 1) {
				logger.warning("Discarding all but the first documentation element from " + that.getId());
			}
		}

		if (that.getExtensionElements() != null) {
			for (AbstractExtensionElement extensionElement : that.getExtensionElements().getAny()) {

				if (extensionElement instanceof Configurable) {
					Configurable configurable = (Configurable) extensionElement;
					shape.setProperty("configurable", true);

					if (configurable.getConfiguration() != null && configurable.getConfiguration().getType() != null) {
						shape.setProperty("configuration", configurable.getConfiguration().getType().value());
					}
				}

				if (extensionElement instanceof ConfigurationAnnotation) {
					try {
						ConfigurationAnnotation configurationAnnotation = (ConfigurationAnnotation) extensionElement;
						JSONObject variants = new JSONObject();
						JSONArray items = new JSONArray();
						variants.put("totalCount", configurationAnnotation.getConfiguration().size());
						variants.put("items", items);
						for (ConfigurationAnnotation.Configuration configuration : configurationAnnotation.getConfiguration()) {
							Variants.Variant variant = (Variants.Variant) configuration.getVariantRef();

							JSONObject item = new JSONObject();

							item.put("id", variant.getName());
							if (that instanceof FlowElement) { item.put("name", configuration.getName()); }
							if (that instanceof Gateway) { item.put("type", configuration.getType().value()); }

							items.put(item);
						}
						shape.setProperty("variants", variants);
					}
					catch (JSONException e) {
						logger.log(SEVERE, "Unable to generate variants property for " + that.getId(), e);
					}
				}

				if (extensionElement instanceof SignavioLabel) {
					SignavioLabel label = (SignavioLabel) extensionElement;

					// Perform a clunky type conversion
					Map<String,String> settings = new HashMap<String,String>();
					for (QName qName : label.getLabelAttributes().keySet()) {
						settings.put(qName.getLocalPart(), label.getLabelAttributes().get(qName));
					}

					shape.setLabelSettings(Collections.singleton(new LabelSettings(settings)));
				}

				if (extensionElement instanceof SignavioMessageName) {
					SignavioMessageName messageName = (SignavioMessageName) extensionElement;
					if (that instanceof Message) {
						shape.setProperty("name", messageName.getName());
					}
					else {
						logger.warning(that.getId() + " has signavio:messageName but is not a Message");
					}
				}

				if (extensionElement instanceof SignavioMetaData) {
					SignavioMetaData metaData = (SignavioMetaData) extensionElement;
					shape.setProperty(metaData.getMetaKey(), metaData.getMetaValue());
				}
			}
		}

		// Replace the initial placeholder "UNIDENTIFIED-EDGE" value
		shape.setResourceId(that.getId());
	}

	@Override public void visitBoundaryEvent(BoundaryEvent that) {
		super.visitBoundaryEvent(that);

		// Dock to the attachedTo activity
		de.hpi.bpmn2_0.model.bpmndi.dc.Bounds bounds = ((Shape) bpmndiMap.get(that)).getBounds();
		de.hpi.bpmn2_0.model.bpmndi.dc.Bounds origin = ((Shape) bpmndiMap.get(that.getAttachedToRef())).getBounds();
		Bounds b = shape.getBounds();
		shape.addDocker(new Point(bounds.getX() + bounds.getWidth() / 2 - origin.getX(),
		                          bounds.getY() + bounds.getHeight() / 2 - origin.getY()));
		shape.setBounds(b);

		shape.setProperty("boundarycancelactivity2", that.isCancelActivity());
	}

	@Override public void visitBusinessRuleTask(BusinessRuleTask that) {
		super.visitBusinessRuleTask(that);

		shape.setProperty("tasktype", "Business Rule");

                if (that.getImplementation() != null) {
		    shape.setProperty("implementation", that.getImplementation().value());
                }
	}

	@Override public void visitCallActivity(CallActivity that) {
		super.visitCallActivity(that);

                shape.setStencilId("Task");
		shape.setProperty("callacitivity", true);  // ...yes, this IS misspelled
                shape.setProperty("tasktype", "None");
		// that.getCallableElement()
	}

	@Override public void visitCallChoreography(CallChoreography that) {
		super.visitCallChoreography(that);

		logger.warning("CallChoreography elements are not supported: " + that.getId());
	}

	@Override public void visitCallConversation(CallConversation that) {
		super.visitCallConversation(that);

		logger.warning("CallConversation elements are not supported: " + that.getId());
	}

	@Override public void visitChoreographyActivity(ChoreographyActivity that) {
		super.visitChoreographyActivity(that);

		logger.warning("ChoreographyActivity elements are not supported: " + that.getId());
	}

	@Override public void visitChoreographyTask(ChoreographyTask that) {
		super.visitChoreographyTask(that);

		logger.warning("ChoreographyTask elements are not supported: " + that.getId());
	}

	@Override public void visitComplexGateway(ComplexGateway that) {
		super.visitComplexGateway(that);

		shape.setStencilId("ComplexGateway");
		shape.setProperty("activationcondition", that.getActivationCondition().toExportString());
		shape.setProperty("gatewaytype", "Complex");
	}

	@Override public void visitConversation(Conversation that) {
		super.visitConversation(that);

		logger.warning("Conversation elements are not supported: " + that.getId());
	}

	@Override public void visitConversationLink(ConversationLink that) {
		super.visitConversationLink(that);

		logger.warning("ConversationLink elements are not supported: " + that.getId());
	}

	@Override public void visitConversationNode(ConversationNode that) {
		super.visitConversationNode(that);

		logger.warning("ConversationNode elements are not supported: " + that.getId());
	}

	@Override public void visitDataAssociation(DataAssociation that) {
		super.visitDataAssociation(that);

		if (that.getAssignment() != null) {
			try {
				JSONObject assignments = new JSONObject();
				assignments.put("totalCount", + that.getAssignment().size());
				JSONArray items = new JSONArray();
				for(Assignment assignment : that.getAssignment()) {
					items.put(new JSONObject().put("from",     assignment.getFrom())
				        	                  .put("to",       assignment.getTo())
				                	          .put("language", assignment.getLanguage()));
				}
				assignments.put("items", items);
				shape.setProperty("assignments", assignments);
			}
			catch (JSONException e) {
				logger.log(SEVERE, "Unable to serialize DataAssociation assignments for " + that.getId(), e);
			}
		}

		if (that.getTransformation() != null) {
			shape.setProperty("transformation", that.getTransformation().toExportString());
		}
	}

	@Override public void visitDataInput(DataInput that) {
		super.visitDataInput(that);

		shape.setStencilId("DataObject");
		shape.setProperty("input_output", "Input");
		// that.getItemSubjectRef()
	}

	@Override public void visitDataInputAssociation(DataInputAssociation that) {
		super.visitDataInputAssociation(that);

		shape.setStencilId("Association_Unidirectional");
	}

	@Override public void visitDataObject(DataObject that) {
		super.visitAbstractDataObject(that);

		SignavioType type = that.getExtensionElements() == null ? null : that.getExtensionElements().getFirstExtensionElementOfType(SignavioType.class);
		switch (type == null ? SignavioDataObjectType.DEFAULT : type.getDataObjectType()) {
		case PROCESSPARTICIPANT:
			shape.setStencilId("processparticipant");
			break;
		case DEFAULT:
			shape.setStencilId("DataObject");
			break;
		case ITSYSTEM:
			shape.setStencilId("ITSystem");
			break;
		}
	}

	@Override public void visitDataOutput(DataOutput that) {
		super.visitDataOutput(that);

		shape.setStencilId("DataObject");
		shape.setProperty("input_output", "Output");
		// that.getItemSubjectRef()
	}

	@Override public void visitDataOutputAssociation(DataOutputAssociation that) {
		super.visitDataOutputAssociation(that);

		shape.setStencilId("Association_Unidirectional");
	}

	@Override public void visitDataStoreReference(DataStoreReference that) {
		super.visitDataStoreReference(that);

		shape.setStencilId("DataStore");

		if (that.getDataStoreRef() == null) {
			logger.warning(that.getId() + " is a DataStoreReference without a data store!");
		}
		else {
			shape.setProperty("capacity",       that.getDataStoreRef().getCapacity());
			shape.setProperty("datastate",      that.getDataStoreRef().getDataState().getName());
			shape.setProperty("datastoreref",   that.getDataStoreRef().getName());
			shape.setProperty("isunlimited",    that.getDataStoreRef().isUnlimited());
			shape.setProperty("itemsubjectref", that.getDataStoreRef().getItemSubjectRef().toString());
		}
	}

	@Override public void visitEdge(Edge that) {
		super.visitEdge(that);

		// Although source and target references reside on this class, they are handled in various other methods

		if (that.getTargetRef() == null) {
			logger.warning(that.getId() + " has no targetRef");
		}
		else {
			shape.connectToATarget(new BasicNode(that.getTargetRef().getId()));
		}
	}

	@Override public void visitEndEvent(EndEvent that) {
		super.visitEndEvent(that);

		//shape.setStencilId("EndNoneEvent");
	}

	@Override public void visitEvent(Event that) {
		super.visitEvent(that);

		/*
		if (that.getAdditionalProperties() != null) for (PropertyListItem item : that.getAdditionalProperties()) {
			// item.getContent();
		}
		*/

		// Do we need to add a docker at the center of this shape's bounding box?
		boolean needsCentralDocker = false;

		// Signavio stencil names are mostly systematic
		String prefix, suffix;
		if (that instanceof StartEvent) {
                        shape.setStencilId("StartNoneEvent");
			prefix = "Start";
			suffix = "";
		}
		else if (that instanceof IntermediateCatchEvent) {
                        shape.setStencilId("IntermediateEvent");
			prefix = "Intermediate";
			suffix = "Catching";
			needsCentralDocker = !(that instanceof BoundaryEvent);
		}
		else if (that instanceof IntermediateThrowEvent) {
                        shape.setStencilId("IntermediateEvent");
			prefix = "Intermediate";
			suffix = "Throwing";
			needsCentralDocker = true;
		}
		else if (that instanceof EndEvent) {
                        shape.setStencilId("EndNoneEvent");
			prefix = "End";
			suffix = "";
		}
		else throw new IllegalArgumentException(that.getId() + " has an unsupported event type: " + that.getClass().getCanonicalName());

		if (that.getEventDefinition().size() == 0) {
			// stencil id has been set already
			shape.setProperty("trigger", "None");
		}
		else if (that.getEventDefinition().size() > 1) {
			if (that instanceof CatchEvent && ((CatchEvent) that).isParallelMultiple()) {
				shape.setStencilId(prefix + "ParallelMultipleEvent" + suffix);
				shape.setProperty("trigger", "Parallel Multiple");
			}
			else {
				shape.setStencilId(prefix + "MultipleEvent" + suffix);
				shape.setProperty("trigger", "Multiple");
			}
		}
		else if (that.getEventDefinition().size() == 1) {
			EventDefinition eventDefinition = that.getEventDefinition().get(0);

			// Determine whether a central docker is required in this case
			if (!(that instanceof BoundaryEvent)) {
				if (eventDefinition instanceof CompensateEventDefinition  && that instanceof StartEvent ||
				    eventDefinition instanceof ErrorEventDefinition                                     ||
				    eventDefinition instanceof EscalationEventDefinition  && that instanceof EndEvent   ||
				    eventDefinition instanceof SignalEventDefinition      && that instanceof EndEvent)
				{
					needsCentralDocker = true;
				}
			}

			if (eventDefinition instanceof CancelEventDefinition) {
				shape.setStencilId(prefix + "CancelEvent");
				shape.setProperty("trigger", "Cancel");
			}
			else if (eventDefinition instanceof TerminateEventDefinition) {
				shape.setStencilId(prefix + "TerminateEvent");
				shape.setProperty("trigger", "Terminate");
			}
			else if (eventDefinition instanceof CompensateEventDefinition) {
				CompensateEventDefinition compensateEventDefinition = (CompensateEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "CompensationEvent" + suffix);
				shape.setProperty("trigger", "Compensation");

				if (compensateEventDefinition.getActivityRef() != null) {
					shape.setProperty("activityref", compensateEventDefinition.getActivityRef().getName());
				}

				shape.setProperty("waitforcompletion", compensateEventDefinition.isWaitForCompletion());
			}
			else if (eventDefinition instanceof ConditionalEventDefinition) {
				ConditionalEventDefinition conditionalEventDefinition = (ConditionalEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "ConditionalEvent");
				shape.setProperty("trigger", "Conditional");

				if (conditionalEventDefinition.getCondition() != null) {
					shape.setProperty("condition", conditionalEventDefinition.getCondition().toExportString());
				}
			}
			else if (eventDefinition instanceof ErrorEventDefinition) {
				ErrorEventDefinition errorEventDefinition = (ErrorEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "ErrorEvent");
				shape.setProperty("trigger", "Error");

				if (errorEventDefinition.getErrorRef() != null) {
					shape.setProperty("errorcode", errorEventDefinition.getErrorRef().getErrorCode());
					shape.setProperty("errorname", errorEventDefinition.getErrorRef().getName());
				}
			}
			else if (eventDefinition instanceof EscalationEventDefinition) {
				EscalationEventDefinition escalationEventDefinition = (EscalationEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "EscalationEvent" + (that instanceof IntermediateCatchEvent ? "" : suffix));
				shape.setProperty("trigger", "Escalation");

				if (escalationEventDefinition.getEscalationRef() != null) {
					shape.setProperty("escalationcode", escalationEventDefinition.getEscalationRef().getEscalationCode());
					shape.setProperty("escalationname", escalationEventDefinition.getEscalationRef().getName());
				}
			}
			else if (eventDefinition instanceof LinkEventDefinition) {
				LinkEventDefinition linkEventDefinition = (LinkEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "LinkEvent" + suffix);
				shape.setProperty("trigger", "Link");

				shape.setProperty("name", linkEventDefinition.getName());  // duplicates name of the parent element (!)
				//linkEventDefinition.getSource()
				//linkEventDefinition.getTarget()
			}
			else if (eventDefinition instanceof MessageEventDefinition) {
				MessageEventDefinition messageEventDefinition = (MessageEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "MessageEvent" + suffix);
				shape.setProperty("trigger", "Message");

				if (messageEventDefinition.getMessageRef() != null) {
					if (that instanceof BoundaryEvent) {
						shape.setProperty("trigger", "Message");
					}
					else {
						shape.setProperty("messageref", messageEventDefinition.getMessageRef().getName());
					}
				}

				if (messageEventDefinition.getOperationRef() != null) {
					shape.setProperty("operationref", messageEventDefinition.getOperationRef().getName());
				}
			}
			else if (eventDefinition instanceof SignalEventDefinition) {
				SignalEventDefinition signalEventDefinition = (SignalEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "SignalEvent" + suffix);
				shape.setProperty("trigger", "Signal");

				if (signalEventDefinition.getSignalRef() != null) {
					shape.setProperty("signalname", signalEventDefinition.getSignalRef().getName());
				}
			}
			else if (eventDefinition instanceof TimerEventDefinition) {
				TimerEventDefinition timerEventDefinition = (TimerEventDefinition) eventDefinition;

				shape.setStencilId(prefix + "TimerEvent");
				shape.setProperty("trigger", "Timer");

				shape.setProperty("timecycle", timerEventDefinition.getTimeCycle());
				if (timerEventDefinition.getTimeDate() != null) {
					shape.setProperty("timedate", timerEventDefinition.getTimeDate().toExportString());
				}
				shape.setProperty("timeduration", timerEventDefinition.getTimeDuration());
			}
			else logger.warning(that.getId() + " uses an unimplemented event definition: " + eventDefinition);
		}
		else throw new IllegalArgumentException(that.getId() + " uses unsupported event definitions: " + that.getEventDefinition());

		if (needsCentralDocker) {
			de.hpi.bpmn2_0.model.bpmndi.dc.Bounds bounds = ((Shape) bpmndiMap.get(that)).getBounds();
			Bounds b = shape.getBounds();
			shape.addDocker(new Point(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2));
			shape.setBounds(b);
		}
	}

	@Override public void visitEventBasedGateway(EventBasedGateway that) {
		super.visitEventBasedGateway(that);

		shape.setStencilId("EventbasedGateway");

		switch (that.getEventGatewayType()) {
		case EXCLUSIVE:
			shape.setProperty("eventtype", that.isInstantiate() ? "instantiate_exclusive" : "exclusive");
			break;
		case PARALLEL:
			if (!that.isInstantiate()) {
				logger.severe("Treating parallel non-instantiate event gateway " + that.getId() + " as instantiate");
			}
			shape.setProperty("eventtype", "instantiate_parallel");
			break;
		}
	}

	@Override public void visitExclusiveGateway(ExclusiveGateway that) {
		super.visitExclusiveGateway(that);

		shape.setStencilId("Exclusive_Databased_Gateway");
		shape.setProperty("gatewaytype", "XOR");
		shape.setProperty("markervisible", ((BPMNShape) bpmndiMap.get(that)).isIsMarkerVisible());
	}

	@Override public void visitFlowElement(FlowElement that) {
		super.visitFlowElement(that);

		if (that.getAuditing() != null) {
			shape.setProperty("auditing", that.getAuditing().toExportString());
		}

		if (that.getMonitoring() != null) {
			shape.setProperty("monitoring", that.getMonitoring().toExportString());
		}

		shape.setProperty(that instanceof Association ? "text" : "name", (that.getName()!= null ? that.getName() : ""));

		logger.finer(that.getId() + " outgoing=" + that.getOutgoing());
		for(Edge edge : that.getOutgoing()) {
			logger.finer("Outgoing from " + that.getId() + " to " + edge.getId());
			shape.addOutgoingAndUpdateItsIncomings(new BasicEdge(edge.getId()));
		}
	}

	/*
	@Override public void visitGateway(Gateway that) {
		super.visitGateway(that);

		switch (that.getGatewayDirection()) {
		case CONVERGING:  break;
		case DIVERGING:   break;
		case MIXED:       break;
		case UNSPECIFIED: break;
		}
	}

	@Override public void visitGatewayWithDefaultFlow(GatewayWithDefaultFlow that) {
		super.visitGatewayWithDefaultFlow(that);
	}
	*/

	@Override public void visitGroup(Group that) {
		super.visitGroup(that);

		shape.setStencilId("Group");
		// that.getCategoryRef().getValue()
	}

	@Override public void visitInclusiveGateway(InclusiveGateway that) {
		super.visitInclusiveGateway(that);

		shape.setStencilId("InclusiveGateway");
		shape.setProperty("gatewaytype", "OR");
	}

	@Override public void visitIntermediateCatchEvent(IntermediateCatchEvent that) {
		super.visitIntermediateCatchEvent(that);

		//shape.setStencilId("IntermediateEvent");
		// that.getCancelActivity();
	}

	@Override public void visitIntermediateThrowEvent(IntermediateThrowEvent that) {
		super.visitIntermediateThrowEvent(that);

		//shape.setStencilId("IntermediateEvent");
	}

	@Override public void visitLane(Lane that) {
		super.visitLane(that);

		shape.setStencilId("Lane");
	}

	@Override public void visitManualTask(ManualTask that) {
		super.visitManualTask(that);

		shape.setProperty("tasktype", "Manual");
	}

	@Override public void visitMessage(Message that) {
		super.visitMessage(that);

		shape.setStencilId("Message");
		shape.setProperty("initiating", that.isInitiating());

		if (that.getStructureRef() != null) {
			shape.setProperty("structureref", that.getStructureRef().getStructure());
		}
	}

	@Override public void visitMessageFlow(MessageFlow that) {
		super.visitMessageFlow(that);

		shape.setStencilId("MessageFlow");

		if (that.getMessageRef() != null) {
			if (bpmndiMap.get(that.getMessageRef()) == null) {
				logger.severe("Message for flow " + that.getId() + " has no diagram shape");
			}
			else {
				BPMN2DiagramConverterVisitor visitor = new BPMN2DiagramConverterVisitor(diagram, bpmndiMap, absentInConfiguration, parentOriginX, parentOriginY);
				bpmndiMap.get(that.getMessageRef()).acceptVisitor(visitor);
				shape.addChildShape(visitor.getShape());
			}
		}
	}

	@Override public void visitParallelGateway(ParallelGateway that) {
		super.visitParallelGateway(that);

		shape.setStencilId("ParallelGateway");
		shape.setProperty("gatewaytype", "AND");
	}

	@Override public void visitParticipant(Participant that) {
		super.visitParticipant(that);

                Process process = that.getProcessRef();
		if (process == null) {
			shape.setStencilId("CollapsedPool");
		}
		else {
			shape.setStencilId("Pool");
			shape.setProperty("isclosed",             process.isIsClosed());
			shape.setProperty("isexecutable",         process.isExecutable());
			//shape.setProperty("processdocumentation", process.getDocumentation());
			shape.setProperty("processtype",          process.getProcessType().value());
		}

                if (that.getParticipantMultiplicity() != null) {
			shape.setProperty("maximum", that.getParticipantMultiplicity().getMaximum());
			shape.setProperty("minimum", that.getParticipantMultiplicity().getMinimum());
                }
	}

	@Override public void visitReceiveTask(ReceiveTask that) {
		super.visitReceiveTask(that);

		shape.setProperty("tasktype", "Receive");

                if (that.getImplementation() != null) {
		    shape.setProperty("implementation", that.getImplementation().value());
                }
	}

	@Override public void visitScriptTask(ScriptTask that) {
		super.visitScriptTask(that);

		shape.setProperty("script", that.getScript());
		shape.setProperty("scriptformat", that.getScriptFormat());
		shape.setProperty("tasktype", "Script");
	}

	@Override public void visitSendTask(SendTask that) {
		super.visitSendTask(that);

		shape.setProperty("tasktype", "Send");

                if (that.getImplementation() != null) {
		    shape.setProperty("implementation", that.getImplementation().value());
                }
	}

	@Override public void visitServiceTask(ServiceTask that) {
		super.visitServiceTask(that);

		shape.setProperty("tasktype", "Service");

                if (that.getImplementation() != null) {
		    shape.setProperty("implementation", that.getImplementation().value());
                }
	}

	@Override public void visitSequenceFlow(SequenceFlow that) {
		super.visitSequenceFlow(that);

		shape.setStencilId("SequenceFlow");

		that.setDefaultSequenceFlow(
			that.getSourceRef() != null &&
			(( that.getSourceRef() instanceof Activity &&
			   that.equals(((Activity)               that.getSourceRef()).getDefault()) ) ||
			 ( that.getSourceRef() instanceof GatewayWithDefaultFlow &&
			   that.equals(((GatewayWithDefaultFlow) that.getSourceRef()).getDefault()) ))
		);

		if (that.isDefaultSequenceFlow()) {
			shape.setProperty("conditiontype", "Default");
		}
		else if (that.getConditionExpression() != null) {
			shape.setProperty("conditionexpression", that.getConditionExpression().toExportString());
			shape.setProperty("conditiontype", "Expression");
			shape.setProperty("showdiamondmarker", that.getSourceRef() != null && !(that.getSourceRef() instanceof ExclusiveGateway));
		}
		else {
			shape.setProperty("conditiontype", "None");
		}
		shape.setProperty("isimmediate", that.isIsImmediate());

		if (absentInConfiguration.contains(that)) {
			shape.setProperty("absentinconfiguration", true);
		}
	}

	@Override public void visitStartEvent(StartEvent that) {
		super.visitStartEvent(that);

		// Default start event in the absence of an event definition element in the BPMN
		//shape.setStencilId("StartNoneEvent");
		//shape.setProperty("trigger", "None");
		// that.isIsInterrupting()
	}

	@Override public void visitSubChoreography(SubChoreography that) {
		super.visitSubChoreography(that);

		logger.warning("SubChoreography elements not supported: " + that.getId());
	}

	@Override public void visitSubConversation(SubConversation that) {
		super.visitSubConversation(that);

		logger.warning("SubConversation elements not supported: " + that.getId());
	}

	@Override public void visitSubProcess(SubProcess that) {
		super.visitSubProcess(that);
        BPMNShape bpmnShape= (BPMNShape) bpmndiMap.get(that);
        Boolean isExpanded=bpmnShape.isIsExpanded();
            if (isExpanded!=null&&isExpanded) {
                shape.setStencilId(that.isTriggeredByEvent() ? "EventSubprocess" : "Subprocess");
            } else {
                shape.setStencilId(that.isTriggeredByEvent() ? "CollapsedEventSubprocess" : "CollapsedSubprocess");
            }
		// that.isTriggeredByEvent()
	}

	@Override public void visitTask(Task that) {
		super.visitTask(that);

		shape.setStencilId("Task");
		shape.setProperty("tasktype", "None");
	}

	@Override public void visitTextAnnotation(TextAnnotation that) {
		super.visitTextAnnotation(that);

		shape.setStencilId("TextAnnotation");
		shape.setProperty("text", that.getText());
		shape.setProperty("textformat", that.getTextFormat());
	}

	@Override public void visitTransaction(Transaction that) {
		super.visitTransaction(that);

		shape.setProperty("isatransaction", true);
		shape.setProperty("transactionmethod", that.getMethod().value());
	}

	@Override public void visitUserTask(UserTask that) {
		super.visitUserTask(that);

		shape.setProperty("tasktype", "User");

                if (that.getImplementation() != null) {
		    shape.setProperty("implementation", that.getImplementation().value());
                }
	}
}
