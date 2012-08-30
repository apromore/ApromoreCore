/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.transformation;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.generic.GenericDiagram;
import org.oryxeditor.server.diagram.generic.GenericEdge;
import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.annotations.ContainerElement;
import de.hpi.bpmn2_0.annotations.SSetExtension;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.factory.configuration.Configuration;
import de.hpi.bpmn2_0.factory.configuration.LinkedModel;
import de.hpi.bpmn2_0.factory.node.IntermediateCatchEventFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Collaboration;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.model.bpmndi.BPMNPlane;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.callable.GlobalChoreographyTask;
import de.hpi.bpmn2_0.model.choreography.CallChoreography;
import de.hpi.bpmn2_0.model.choreography.Choreography;
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
import de.hpi.bpmn2_0.model.conversation.ConversationElement;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.data_object.AbstractDataObject;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.bpmn2_0.model.data_object.InputOutputSpecification;
import de.hpi.bpmn2_0.model.data_object.InputSet;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.data_object.OutputSet;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMessageName;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayWithDefaultFlow;
import de.hpi.bpmn2_0.model.misc.ProcessType;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.LaneSet;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.util.DiagramHelper;
import de.hpi.bpmn2_0.util.SignavioIDChecker;
import de.hpi.diagram.SignavioUUID;

/**
 * Converter class for Diagram to BPMN 2.0 transformation.
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
public class Diagram2BpmnConverter {
	/* Hash map of factories for BPMN 2.0 element to enable lazy initialization */
	private HashMap<String, AbstractBpmnFactory> factories;
	private HashMap<String, BPMNElement> bpmnElements;
	private GenericDiagram<?,?> diagram;
	private List<BPMNElement> diagramChilds;
	private List<Process> processes;
	
	private static Constants constants;
	
	/* Processes just used to identify all processes in the diagram (used i.e. 
	 * for CallActivities). They are removed afterwards, to avoid duplicates.
	 */
	private List<Process> tmpProcesses;
	
	private Definitions definitions;
	private Configuration configuration;
	private String editorVersion;

	private Collaboration collaboration;

	private List<Choreography> choreography;

	private List<Class<? extends AbstractBpmnFactory>> factoryClasses;

	/* Define edge ids */
	private final static String[] edgeIdsArray = { "SequenceFlow",
			"Association_Undirected", "Association_Unidirectional",
			"Association_Bidirectional", "MessageFlow", "ConversationLink" };

	public final static HashSet<String> edgeIds = new HashSet<String>(Arrays
			.asList(edgeIdsArray));

	/* Define data related objects ids */
	private final static String[] dataObjectIdsArray = { "DataObject",
			"DataStore", "Message", "ITSystem" };

	public final static HashSet<String> dataObjectIds = new HashSet<String>(
			Arrays.asList(dataObjectIdsArray));

	public Diagram2BpmnConverter(GenericDiagram diagram,
			List<Class<? extends AbstractBpmnFactory>> factoryClasses) {
		this.factories = new HashMap<String, AbstractBpmnFactory>();
		this.bpmnElements = new HashMap<String, BPMNElement>();
		this.definitions = new Definitions();
		this.definitions.setId(SignavioUUID.generate());
		this.diagram = diagram;
		this.factoryClasses = factoryClasses;
		this.configuration = new Configuration();
	}
	
	/**
	 * Also sets the configuration on reformation the element IDs.
	 * 
	 * @param diagram
	 * @param factoryClasses
	 */
	public Diagram2BpmnConverter(GenericDiagram diagram,
			List<Class<? extends AbstractBpmnFactory>> factoryClasses, boolean checkOnSignavioIDs) {
		this(diagram, factoryClasses);
		this.configuration.ensureSignavioStyle = checkOnSignavioIDs;
	}

	/**
	 * Constructor receiving an additional configuration map. This map can
	 * contain linked diagram or the current editor version.
	 * 
	 * @param diagram2
	 * @param factoryClasses2
	 * @param configuration
	 */
	public Diagram2BpmnConverter(GenericDiagram diagram,
			List<Class<? extends AbstractBpmnFactory>> factoryClasses,
			Map<String, Object> configuration) {
		this(diagram, factoryClasses);

		if (configuration == null)
			return;

		/* Process configuration */
		this.configuration = new Configuration(configuration);

		/* Editor version */
		setEditorVersion(this.configuration);
	}

	public Diagram2BpmnConverter(Map<String, LinkedModel> linkedModels,
			GenericDiagram diagram,
			List<Class<? extends AbstractBpmnFactory>> factoryClasses) {
		this(diagram, factoryClasses);

		this.configuration.getLinkedModels().putAll(linkedModels);

	}

	private void setEditorVersion(Configuration configuration) {
		if (configuration == null) {
			return;
		}
		this.editorVersion = configuration.getEditorVersion();
	}

	/**
	 * Retrieves the stencil id related hashed factory.
	 * 
	 * @param stencilId
	 *            The stencil id
	 * @return The related factory
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private AbstractBpmnFactory getFactoryForStencilId(String stencilId)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		/* Create a new factory instance if necessary */
		if (!factories.containsKey(stencilId)) {
			this.factories.put(stencilId, createFactoryForStencilId(stencilId));
		}

		return this.factories.get(stencilId);
	}

	/**
	 * Creates a new factory instance for a stencil id.
	 * 
	 * @param stencilId
	 *            The stencil id
	 * @return The created factory
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	private AbstractBpmnFactory createFactoryForStencilId(String stencilId)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		/* Find factory for stencil id */
		Class<? extends AbstractBpmnFactory> factory = null;
		for (Class<? extends AbstractBpmnFactory> factoryClass : factoryClasses) {
			StencilId stencilIdA = (StencilId) factoryClass
					.getAnnotation(StencilId.class);
			if (stencilIdA == null)
				continue;

			/* Check if appropriate stencil id is contained */
			List<String> stencilIds = Arrays.asList(stencilIdA.value());
			if (stencilIds.contains(stencilId)) {
				if (factory == null)
					factory = factoryClass;
				else {
					/*
					 * Prefer the general factory class if the necessary stencil
					 * set extension of the specialized factory class is not
					 * loaded in the diagram.
					 */
					SSetExtension oldSSetExtension = factory
							.getAnnotation(SSetExtension.class);
					if (oldSSetExtension != null) {
						if (!this.diagram.getSsextensions().containsAll(
								Arrays.asList(oldSSetExtension.value()))) {
							factory = factoryClass;
							continue;
						}
					}

					/*
					 * Check if there is a specialized factory for an loaded
					 * extension
					 */
					SSetExtension ssetExtension = factoryClass
							.getAnnotation(SSetExtension.class);

					if (ssetExtension == null)
						continue;
					if (this.diagram.getSsextensions().containsAll(
							Arrays.asList(ssetExtension.value())))
						factory = factoryClass;
				}
			}
		}

		if (factory != null)
			return factory.newInstance();

		throw new ClassNotFoundException("Factory for stencil id: '"
				+ stencilId + "' not found!");
	}

	/**
	 * Secures uniqueness of an BPMN Element.
	 * 
	 * @param el
	 * @throws InvalidKeyException
	 */
	private void addBpmnElement(BPMNElement el) throws InvalidKeyException {
		if (this.bpmnElements.containsKey(el.getId())) {
			throw new InvalidKeyException(
					"Key already exists for BPMN element!");
		}

		this.bpmnElements.put(el.getId(), el);
	}

	/**
	 * Creates the BPMN 2.0 elements for the parent's child shapes recursively.
	 * 
	 * @param childShapes
	 *            The list of parent's child shapes
	 * @param parent
	 *            The parent {@link BPMNElement}
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BpmnConverterException
	 * @throws InvalidKeyException
	 */
	private BPMNElement createBpmnElementsRecursively(GenericShape<?,?> shape)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, BpmnConverterException, InvalidKeyException {

		/* Build up the Elements of the current shape childs */
		ArrayList<BPMNElement> childElements = new ArrayList<BPMNElement>();

		/* Create BPMN elements from shapes */
		for (GenericShape childShape : shape.getChildShapesReadOnly()) {
			BPMNElement childEl = this.createBpmnElementsRecursively(childShape);
			if(childEl != null) {
				childElements.add(childEl);
			}
		}

		if (shape.equals(this.diagram)) {
			this.diagramChilds = childElements;
			return null;
		}

		/* Get the appropriate factory and create the element */
		AbstractBpmnFactory factory = this.getFactoryForStencilId(shape
				.getStencilId());

		BPMNElement bpmnElement = null;
		if (this.configuration == null) {
			bpmnElement = factory.createBpmnElement(shape, new BPMNElement(
					null, null, null));
		} else {
			bpmnElement = factory.createBpmnElement(shape, this.configuration);
		}
		
		if(bpmnElement != null) {
			/* Add element to flat list of all elements of the diagram */
			this.addBpmnElement(bpmnElement);
	
			/* Add childs to current BPMN element */
			for (BPMNElement child : childElements) {
				bpmnElement.addChild(child);
			}
		}
		
		return bpmnElement;

	}

	/**
	 * Finds catching intermediate event that are attached to an activities
	 * boundary.
	 */
	private void detectBoundaryEvents() {
		for (GenericShape<?,?> shape : this.diagram.getAllShapesReadOnly()) {
			if (edgeIds.contains(shape.getStencilId())) {
				continue;
			}

			for (GenericShape outShape : shape.getOutgoingsReadOnly()) {
				if (edgeIds.contains(outShape.getStencilId()))
					continue;
				IntermediateCatchEventFactory.changeToBoundaryEvent(
						this.bpmnElements.get(shape.getResourceId()),
						this.bpmnElements.get(outShape.getResourceId()));
			}
		}
	}

	/**
	 * Retrieves the edges and updates the source and target references.
	 */
	private void detectConnectors() {
		for (GenericEdge<?,?> edge : DiagramHelper.getAllEdges(this.diagram)) {
			//TODO do we still need this?
			if (!edgeIds.contains(edge.getStencilId())) {
				continue;
			}

			/* Retrieve connector element */
			BPMNElement bpmnConnector = this.bpmnElements.get(edge
					.getResourceId());

			BPMNElement source = null;

			/*
			 * Find source of connector. It is assumed that the first none edge
			 * element is the source element.
			 */
			for (GenericShape incomingShape : edge.getIncomingsReadOnly()) {
				if (edgeIds.contains(incomingShape.getStencilId())) {
					((Edge) bpmnConnector.getNode()).getIncoming().add(
							(Edge) this.bpmnElements.get(
									incomingShape.getResourceId()).getNode());

					/*
					 * Avoids that a sequence/message flow or conversation link
					 * has an edge element as source
					 */
					if (bpmnConnector.getNode() instanceof SequenceFlow
							|| bpmnConnector.getNode() instanceof MessageFlow
							|| bpmnConnector.getNode() instanceof ConversationLink) {
						continue;
					}
				}
				source = this.bpmnElements.get(incomingShape.getResourceId());
				break;
			}

			/* Update outgoing references */
			for (GenericShape outgoingShape : edge.getOutgoingsReadOnly()) {
				if (!edgeIds.contains(outgoingShape.getStencilId()))
					continue;
				((Edge) bpmnConnector.getNode()).getOutgoing().add(
						(Edge) this.bpmnElements.get(
								outgoingShape.getResourceId()).getNode());

			}

			BPMNElement target = (edge.getTarget() != null) ? this.bpmnElements
					.get(edge.getTarget().getResourceId())
					: null;

			/* Update source references */
			if (source != null) {
				Edge edgeElement = (Edge) bpmnConnector.getNode();

				FlowElement sourceNode = (FlowElement) source.getNode();
				sourceNode.getOutgoing().add((Edge) bpmnConnector.getNode());

				edgeElement.setSourceRef(sourceNode);

			}

			/* Update target references */
			if (target != null) {
				Edge edgeElement = (Edge) bpmnConnector.getNode();

				FlowElement targetNode = (FlowElement) target.getNode();
				targetNode.getIncoming().add((Edge) bpmnConnector.getNode());

				edgeElement.setTargetRef(targetNode);

			}
		}
	}

	/**
	 * An undirected association that connects a sequence flow and a data object
	 * is split up into a data input and output association.
	 */
	private void updateUndirectedDataAssociationsRefs() {
		for (GenericShape shape : this.diagram.getAllShapesReadOnly()) {
			if (shape.getStencilId() == null
					|| !shape.getStencilId().equalsIgnoreCase("sequenceflow"))
				continue;

			/* Retrieve sequence flow connector element */
			BPMNElement seqFlowEle = this.bpmnElements.get(shape
					.getResourceId());
			if (seqFlowEle.getNode() instanceof SequenceFlow)
				((SequenceFlow) seqFlowEle.getNode())
						.processUndirectedDataAssociations();
		}
	}

	/**
	 * A {@link DataAssociation} is a child element of an {@link Activity}. This
	 * method updates the references between activities and their data
	 * associations.
	 */
	private void updateDataAssociationsRefs() {
		/* Define edge ids */
		String[] associationIdsArray = { /* "Association_Undirected", */
		"Association_Unidirectional", "Association_Bidirectional" };

		HashSet<String> associationIds = new HashSet<String>(Arrays
				.asList(associationIdsArray));

		for (GenericShape shape : this.diagram.getAllShapesReadOnly()) {
			if (!associationIds.contains(shape.getStencilId())) {
				continue;
			}

			/* Retrieve connector element */
			BPMNElement bpmnConnector = this.bpmnElements.get(shape
					.getResourceId());

			/* Get related activity */
			Edge dataAssociation = (Edge) bpmnConnector.getNode();
			Activity relatedActivity = null;
			if (dataAssociation instanceof DataInputAssociation) {
				relatedActivity = (dataAssociation.getTargetRef() instanceof Activity ? (Activity) dataAssociation
						.getTargetRef()
						: null);
				if (relatedActivity != null) {
					relatedActivity.getDataInputAssociation().add(
							(DataInputAssociation) dataAssociation);
				} else {
					bpmnConnector.setNode(new Association((DataInputAssociation)dataAssociation));
				}

			} else if (dataAssociation instanceof DataOutputAssociation) {
				relatedActivity = (dataAssociation.getSourceRef() instanceof Activity ? (Activity) dataAssociation
						.getSourceRef()
						: null);
				if (relatedActivity != null) {
					relatedActivity.getDataOutputAssociation().add(
							(DataOutputAssociation) dataAssociation);
				} else {
					bpmnConnector.setNode(new Association((DataOutputAssociation)dataAssociation));
				}
			}
		}

		/* Update undirected data associations references */
		this.updateUndirectedDataAssociationsRefs();
	}

	/**
	 * Identifies the default sequence flows after all sequence flows are set
	 * correctly.
	 */
	private void setDefaultSequenceFlowOfExclusiveGateway() {
		for (BPMNElement element : this.bpmnElements.values()) {
			BaseElement base = element.getNode();
			if (base instanceof GatewayWithDefaultFlow) {
				((GatewayWithDefaultFlow) base).findDefaultSequenceFlow();
			}
		}
	}

	/**
	 * Method to handle sub processes
	 * 
	 * @param subProcess
	 */
	private void handleSubProcess(SubProcess subProcess) {

		List<BPMNElement> childs = this.getChildElements(this.bpmnElements
				.get(subProcess.getId()));
		for (BPMNElement ele : childs) {
			// process.getFlowElement().add((FlowElement) ele.getNode());
			// subProcess.getFlowElement().add((FlowElement) ele.getNode());
			if (ele.getNode() instanceof SubProcess) {
				this.handleSubProcess((SubProcess) ele.getNode());
			} else if(ele.getNode() instanceof CallActivity) {
				this.handleCallActivities((CallActivity) ele.getNode());
			}
		}
	}
	
	/**
	 * In case the {@link CallActivity} calls an expanded subprocess, a new 
	 * process is created that includes all referenced {@link FlowElement} by 
	 * the {@link CallActivity}
	 * 
	 * @param callAct
	 */
	private void handleCallActivities(CallActivity callAct) {
		Process p = new Process();
		p.setId("");
		
		for(FlowElement fe : callAct._getFlowElementsOfTheGlobalProcess()) {
			p.getFlowElement().add(fe);
			if(fe instanceof CallActivity) {
				handleCallActivities((CallActivity) fe);
			} else if(fe instanceof SubProcess) {
				handleSubProcess((SubProcess) fe);
			}
		}
		
		// Apply id for BPMN 2.0 XML round trip if applicable 
		if(!p.hasId() && callAct.hasValidRoundTripProcessId()) {
			p.setId(callAct.getProcessid());
		} else if (!p.hasId()) {
			p.setId(SignavioUUID.generate());
		}
		
		if(p.getFlowElement().size() > 0) {
			callAct.setCalledElement(p);
			this.processes.add(p);
			this.tmpProcesses.add(p);
		}
	}

	/**
	 * Identifies {@link Artifact} elements and puts them into the appropriate
	 * {@link Process} element.
	 */
	private void handleArtifacts() {
		for (Artifact artifact : this.getAllArtifacts()) {

			/*
			 * Prefer the process by connecting object over process by pool
			 * containment. Use case: task in Pool1 text annotation in Pool2
			 */
			Process containmentProcess = artifact.getProcess();
			SubProcess subProcess = artifact.getSubProcess();
			SubChoreography subChoreography = artifact.getSubChoreography();

			artifact.findRelatedProcess();

			/*
			 * If no process was found, check it the artifact is part of a
			 * conversation.
			 */
			if (artifact.getProcess() == null && containmentProcess == null
					&& subProcess == null && subChoreography == null
					&& artifact.isConverstionRelated()) {
				getCollaboration().getArtifact().add(artifact);
				continue;
			}

			/*
			 * If a new process was assigned, delete the artifact as a child
			 * element from the old process
			 */
			if (containmentProcess != null
					&& artifact.getProcess() != null
					&& !containmentProcess.getId().equals(
							artifact.getProcess().getId())) {
				containmentProcess.removeChild(artifact);
			}

			/* Remove from subprocess if e.g. a Textannotation was missplaced */
			if (subProcess != null && artifact.getProcess() != null) {
				subProcess.removeChild(artifact);
			}

			/*
			 * If no related process was found, add assign to the default
			 * process.
			 */
			if (subProcess == null && artifact.getProcess() == null
					&& this.processes.size() > 0) {
				artifact.setProcess(this.processes
						.get(this.processes.size() - 1));
				this.processes.get(this.processes.size() - 1)
						.addChild(artifact);
			} else if (subProcess == null && artifact.getProcess() == null) {
				Process process = new Process();
				this.processes.add(process);
				process.setId(SignavioUUID.generate());
				process.addChild(artifact);

				artifact.setProcess(process);
			}
		}
	}

	/**
	 * Assigns the DataObjectes to the appropriate {@link Process}.
	 */
	private void handleDataObjects() {
		ArrayList<AbstractDataObject> dataObjects = new ArrayList<AbstractDataObject>();
		this.getAllDataObjects(this.diagramChilds, dataObjects);

		for (AbstractDataObject dataObject : dataObjects) {
			if (dataObject.getProcess() != null)
				continue;
			dataObject.findRelatedProcess();

			/* Add a DataStore as a global element */
			if (dataObject instanceof DataStoreReference
					&& ((DataStoreReference) dataObject).getDataStoreRef() != null) {
				this.definitions.getRootElement().add(
						((DataStoreReference) dataObject).getDataStoreRef());
			}

			/*
			 * If no related process was found, add assign to the default
			 * process.
			 */
			if (dataObject.getProcess() == null && this.processes.size() > 0) {
				dataObject.setProcess(this.processes
						.get(this.processes.size() - 1));
				this.processes.get(this.processes.size() - 1).addChild(
						dataObject);
			} else if (dataObject.getProcess() == null) {
				Process process = new Process();
				this.processes.add(process);
				process.setId(SignavioUUID.generate());
				process.addChild(dataObject);
				dataObject.setProcess(process);
			}

		}
	}

	/**
	 * Retrieves all data related elements.
	 * 
	 * @param elements
	 *            The list of {@link BPMNElement}.
	 * 
	 * @param dataObjects
	 *            The resulting list of {@link AbstractDataObject}
	 */
	private void getAllDataObjects(List<BPMNElement> elements,
			List<AbstractDataObject> dataObjects) {
		for (BPMNElement element : elements) {
			if (element.getNode() instanceof Lane
					|| element.getNode() instanceof SubProcess) {
				getAllDataObjects(this.getChildElements(element), dataObjects);
				continue;
			}

			if (element.getNode() instanceof AbstractDataObject) {
				dataObjects.add((AbstractDataObject) element.getNode());
			}
		}
	}

	/**
	 * 
	 * @return All {@link Artifact} contained in the diagram.
	 */
	private List<Artifact> getAllArtifacts() {
		List<Artifact> artifacts = new ArrayList<Artifact>();

		for (BPMNElement element : this.bpmnElements.values()) {
			if (element.getNode() instanceof Artifact) {
				artifacts.add((Artifact) element.getNode());
			}
		}

		return artifacts;
	}

	/**
	 * @return All {@link Task} contained in the diagram.
	 */
	private List<Task> getAllTasks() {
		ArrayList<Task> activities = new ArrayList<Task>();
		for (BPMNElement element : this.bpmnElements.values()) {
			if (element.getNode() instanceof Task)
				activities.add((Task) element.getNode());
		}

		return activities;
	}

	/**
	 * Identifies sets of nodes, connected through SequenceFlows.
	 */
	private void identifyProcesses() {
		this.processes = new ArrayList<Process>();
		this.tmpProcesses = new ArrayList<Process>();

		List<FlowNode> allNodes = new ArrayList<FlowNode>();
		this.getAllNodesRecursively(this.diagramChilds, allNodes);

		// handle subprocesses => trivial
		for (FlowNode flowNode : allNodes) {
			if (flowNode instanceof SubProcess) {
				handleSubProcess((SubProcess) flowNode);
			} else if(flowNode instanceof CallActivity) {
				handleCallActivities((CallActivity) flowNode);
			}
		}

		/* Handle pools, current solution: only one process per pool */
		for (BPMNElement element : this.diagramChilds) {
			if (element.getNode() instanceof Participant
					&& ((Participant) element.getNode()).getLaneSet() != null) {
				Participant participant = (Participant) element.getNode();
				LaneSet laneSet = participant.getLaneSet();

				Process process = new Process();
				process.setId("");
				
				// Retrieve process id from pool element to keep the process id
				// in a BPMN 2.0 XML round trip scenario.
				if(participant.hasValidRoundTripProcessId()) {
					process.setId(participant.getProcessid()); 
				} else {
					process.setId(SignavioUUID.generate());
				}

				/* Process attributes derived from lane set */
				/* isCloased */
				if (participant._isClosed != null
						&& participant._isClosed.equalsIgnoreCase("true"))
					process.setIsClosed(true);
				else
					process.setIsClosed(false);

				/* Process Type */
				if (participant._processType != null) {
					process.setProcessType(ProcessType
							.fromValue(participant._processType));
				}

				/* isExecutable */
				if (participant._isExecutable != null
						&& participant._isExecutable.equalsIgnoreCase("true"))
					process.setExecutable(true);
				else
					process.setExecutable(false);

				process.getLaneSet().add(laneSet);
				participant.setProcessRef(process);
				
				/* Copy name attribute of participant */
				process.setName(participant.getName());

				process.getFlowElement().addAll(laneSet.getChildFlowElements());

				this.processes.add(process);
			}

		}

		/* Identify components within allNodes */
		while (allNodes.size() > 0) {
			Process currentProcess = new Process();
			currentProcess.setId("");
			
			if(this.definitions.getName() != null 
					&& this.definitions.getName().length() > 0) {
				currentProcess.setName(this.definitions.getName());
			}
			this.processes.add(currentProcess);

			
			
			addNode(currentProcess,
					this.getBpmnElementForNode(allNodes.get(0)), allNodes);
			
			// Generate Process Id if necessary
			if(!currentProcess.hasId()) {
				currentProcess.setId(SignavioUUID.generate());
			}
		}

		this.addSequenceFlowsToProcess();

		/* Set processRefs */
		for (Process p : this.processes) {
			for (FlowElement el : p.getFlowElement()) {
				el.setProcess(p);
			}
		}
		
		/* Remove temporary processes from the list */
		this.processes.removeAll(this.tmpProcesses);
//		this.tmpProcesses = null;
	}

	/**
	 * Adds {@link Edge} to the related process.
	 */
	private void addSequenceFlowsToProcess() {
		for (BPMNElement element : this.diagramChilds) {
			if (!(element.getNode() instanceof SequenceFlow))
				continue;

			Edge edge = (Edge) element.getNode();
			List<FlowElement> flowElements = findProcessFlowElementListForEdge(edge);
			if (flowElements != null) {
				flowElements.add(edge);
			}
		}
	}

	/**
	 * Finds the process for an {@link Edge}
	 * 
	 * @param edge
	 */
	private List<FlowElement> findProcessFlowElementListForEdge(Edge edge) {
		/* Find process for edge */
		for (Process process : this.processes) {
			List<FlowElement> flowElements;

			flowElements = process.getFlowElement();
			List<Artifact> artifacts = process.getArtifact();

			if (flowElements.contains(edge.getSourceRef())
					|| flowElements.contains(edge.getTargetRef())
					|| artifacts.contains(edge.getSourceRef()) 
					|| artifacts.contains(edge.getTargetRef())) {
				edge.setProcess(process);
				return flowElements;
			}

			/* Look up in subprocesses */

			for (SubProcess subProcess : process.getSubprocessList()) {
				flowElements = subProcess.getFlowElement();

				if (flowElements.contains(edge.getSourceRef())
						|| flowElements.contains(edge.getTargetRef())) {
					edge.setSubProcess(subProcess);
					return flowElements;
				}
			}

			/* Look up in subchoreographies */
			for (SubChoreography subChoreography : process
					.getSubChoreographyList()) {
				flowElements = subChoreography.getFlowElement();

				if (flowElements.contains(edge.getSourceRef())
						|| flowElements.contains(edge.getTargetRef())) {
					edge.setSubChoreography(subChoreography);
					return flowElements;
				}
			}
		}

		return null;
	}

	/**
	 * Assigns {@link Association} to the appropriate process element.
	 */
	private void addAssociationsToProcess() {
		for (BPMNElement element : this.diagramChilds) {
			if (!(element.getNode() instanceof Association))
				continue;

			Edge edge = (Edge) element.getNode();
			List<FlowElement> flowElements = findProcessFlowElementListForEdge(edge);
			if (flowElements != null) {
				((Association) edge)._containedInProcess = true;
				flowElements.add(edge);
			}
		}
	}

	/**
	 * Assigns {@link Association} to the collaboration element in case of a
	 * conversation.
	 */
	private void addAssociationsToConversation() {
		for (BPMNElement element : this.diagramChilds) {
			if (!(element.getNode() instanceof Association))
				continue;

			Association edge = (Association) element.getNode();

			if (edge._containedInProcess) {
				continue;
			}

			/* Check source */
			if (edge.getSourceRef() != null
					&& edge.getSourceRef() instanceof ConversationElement
					&& !(edge.getSourceRef() instanceof Participant && ((Participant) edge
							.getSourceRef())._isChoreographyParticipant)
					&& (edge.getSourceRef().getProcess() == null || (edge
							.getSourceRef().getProcess() != null && !edge
							.getSourceRef().getProcess()
							.isChoreographyProcess()))) {
				this.getCollaboration().getAssociation().add(edge);
				continue;
			}

			/* Check target */
			if (edge.getTargetRef() != null
					&& edge.getTargetRef() instanceof ConversationElement) {
				this.getCollaboration().getAssociation().add(edge);
				continue;
			}
		}
	}

	/**
	 * Helper method to get the {@link BPMNElement} for the given
	 * {@link FlowNode} from the list of BPMN elements.
	 * 
	 * @param node
	 *            The concerning {@link FlowNode}
	 * @return The related {@link BPMNElement}
	 */
	private BPMNElement getBpmnElementForNode(FlowNode node) {
		return this.bpmnElements.get(node.getId());
	}

	/**
	 * Adds the node to the connected set of nodes.
	 * 
	 * @param process
	 * @param element
	 * @param allNodes
	 */
	private void addNode(Process process, BPMNElement element,
			List<FlowNode> allNodes) {
		if (!(element.getNode() instanceof FlowNode)
				|| !allNodes.contains(element.getNode())) {
			return;
		}
		
		
		FlowNode node = (FlowNode) element.getNode();

		// Set id of process if there is one applied for round-tripping scenarios
		if(!process.hasId() && node.hasValidRoundTripProcessId()) {
			process.setId(node.getProcessid());
		}

		allNodes.remove(node);

		node.setProcess(process);
		process.addChild(node);

		/* Handle sequence flows */
		/* Attention: navigate into both directions! */
		for (SequenceFlow seqFlow : node.getIncomingSequenceFlows()) {
			if (seqFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) seqFlow
						.getSourceRef()), allNodes);
			}
		}

		for (SequenceFlow seqFlow : node.getOutgoingSequenceFlows()) {
			if (seqFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) seqFlow
						.getTargetRef()), allNodes);
			}
		}

		/* Handle compensation flow */
		/* Attention: navigate into both directions! */
		for (Association compFlow : node.getIncomingCompensationFlows()) {
			if (compFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) compFlow
						.getSourceRef()), allNodes);
			}
		}

		for (Association compFlow : node.getOutgoingCompensationFlows()) {
			if (compFlow.sourceAndTargetContainedInSamePool()) {
				addNode(process, this.getBpmnElementForNode((FlowNode) compFlow
						.getTargetRef()), allNodes);
			}
		}

		/* Handle boundary events */
		/* Attention: navigate into both directions! */
		if (node instanceof BoundaryEvent) {
			if (((BoundaryEvent) node).getAttachedToRef() != null) {
				addNode(process, this
						.getBpmnElementForNode(((BoundaryEvent) node)
								.getAttachedToRef()), allNodes);
			}
		} else if (node instanceof Activity) {
			for (BoundaryEvent event : ((Activity) node).getBoundaryEventRefs()) {
				addNode(process, this.getBpmnElementForNode(event), allNodes);
			}
		}
	}

	/**
	 * Retrieves all nodes included into the diagram and stop recursion at
	 * subprocesses.
	 * 
	 * @param elements
	 *            The child elements of a parent BPMN element
	 * @param allNodes
	 *            The list to store every element
	 */
	private void getAllNodesRecursively(List<BPMNElement> elements,
			List<FlowNode> allNodes) {
		for (BPMNElement element : elements) {
			if (element.getNode() instanceof Lane) {
				getAllNodesRecursively(this.getChildElements(element), allNodes);
				continue;
			}
			if (!(element.getNode() instanceof FlowNode)) {
				continue;
			}

			FlowNode node = (FlowNode) element.getNode();

			if (node instanceof Activity || node instanceof Event
					|| node instanceof Gateway) {
				allNodes.add(node);
			}
		}
	}

	/**
	 * Retrieve the child elements of a BPMN element from within all BPMN
	 * elements in the diagram.
	 * 
	 * @param element
	 *            The parent BPMN Element
	 * @return
	 */
	private List<BPMNElement> getChildElements(BPMNElement element) {
		List<BPMNElement> childElements = new ArrayList<BPMNElement>();
		for (GenericShape<?,?> shape : this.diagram.getAllShapesReadOnly()) {
			if (!shape.getResourceId().equals(element.getId())) {
				continue;
			}
			for (GenericShape child : shape.getChildShapesReadOnly()) {
				childElements.add(this.bpmnElements.get(child.getResourceId()));
			}
		}

		return childElements;
	}

	/**
	 * Inserts the shapes of nodes as children of the {@link BPMNPlane}.
	 * 
	 * @param subProcess
	 */
	private void insertSubprocessShapes(SubProcess subProcess) {
		for (FlowElement flowEle : subProcess.getFlowElement()) {
			if (flowEle instanceof FlowNode
					&& this.bpmnElements.get(flowEle.getId()) != null) {
				this.definitions.getFirstPlane().getDiagramElement().add(
						this.bpmnElements.get(flowEle.getId()).getShape());
			}

			/* Subprocess elements */
			if (flowEle instanceof SubProcess) {
				insertSubprocessShapes((SubProcess) flowEle);
			}
		}
		
		for (Artifact a : subProcess.getArtifact()) {
			if(this.bpmnElements.get(a.getId()) != null) {
				this.definitions.getFirstPlane().getDiagramElement().add(
						this.bpmnElements.get(a.getId()).getShape());				
			}
		}
	}
	
	/**
	 * Places the shapes of the elements referenced by the process at diagram's
	 * shape list.
	 * 
	 * @param process
	 */
	private boolean insertProcessShapes(Process process) {
		if (process.isChoreographyProcess())
			return false;

		/* First insert LaneSets */
		for (Lane lane : process.getAllLanes()) {
			this.definitions.getFirstPlane().getDiagramElement().add(
					this.bpmnElements.get(lane.getId()).getShape());
		}

		/* Second process elements like tasks */

		for (FlowElement flowEle : process.getFlowElement()) {
			if (!(flowEle instanceof Edge)) {
				this.definitions.getFirstPlane().getDiagramElement().add(
						this.bpmnElements.get(flowEle.getId()).getShape());
			}

			/* Subprocess elements */
			if (flowEle instanceof SubProcess) {
				insertSubprocessShapes((SubProcess) flowEle);
			}
		}

		/* Insert Artifacts */
		for (Artifact a : process.getArtifact()) {
			this.definitions.getFirstPlane().getDiagramElement().add(
					this.bpmnElements.get(a.getId()).getShape());
		}
		
		/* Insert Data Objects form IOSpecifications */
		if(process.getIoSpecification() != null) {
			for(DataInput dataInput : process.getIoSpecification().getDataInput()) {
				this.definitions.getFirstPlane().getDiagramElement().add(
						this.bpmnElements.get(dataInput.getId()).getShape());
			}
			
			for(DataOutput dataOutput : process.getIoSpecification().getDataOutput()) {
				this.definitions.getFirstPlane().getDiagramElement().add(
						this.bpmnElements.get(dataOutput.getId()).getShape());
			}
		}
		
		return true;
	}

	/**
	 * Creates a process diagram for each identified process.
	 */
	private void insertProcessesIntoDefinitions() {
		for (Process process : this.processes) {
//			if (process.isChoreographyProcess())
//				continue;
//
//			/* First insert LaneSets */
//			for (Lane lane : process.getAllLanes()) {
//				this.definitions.getFirstPlane().getDiagramElement().add(
//						this.bpmnElements.get(lane.getId()).getShape());
//			}
//
//			/* Second process elements like tasks */
//
//			for (FlowElement flowEle : process.getFlowElement()) {
//				if (!(flowEle instanceof Edge)) {
//					this.definitions.getFirstPlane().getDiagramElement().add(
//							this.bpmnElements.get(flowEle.getId()).getShape());
//				}
//
//				/* Subprocess elements */
//				if (flowEle instanceof SubProcess) {
//					insertSubprocessShapes((SubProcess) flowEle);
//				}
//			}
//
//			/* Insert Artifacts */
//			for (Artifact a : process.getArtifact()) {
//				this.definitions.getFirstPlane().getDiagramElement().add(
//						this.bpmnElements.get(a.getId()).getShape());
//			}
//			
//			/* Insert Data Objects form IOSpecifications */
//			if(process.getIoSpecification() != null) {
//				for(DataInput dataInput : process.getIoSpecification().getDataInput()) {
//					this.definitions.getFirstPlane().getDiagramElement().add(
//							this.bpmnElements.get(dataInput.getId()).getShape());
//				}
//				
//				for(DataOutput dataOutput : process.getIoSpecification().getDataOutput()) {
//					this.definitions.getFirstPlane().getDiagramElement().add(
//							this.bpmnElements.get(dataOutput.getId()).getShape());
//				}
//			}
		
			if(!this.insertProcessShapes(process)) {
				continue;
			}
			
			/* Insert process into document */
			this.definitions.getRootElement().add(process);

			/*
			 * Set diagram shape reference for the process. This reference will
			 * be overwritten, if a collaboration is contained.
			 */
			if(!(this.definitions.getFirstPlane().getBpmnElement() instanceof Collaboration)
					&& !(this.definitions.getFirstPlane().getBpmnElement() instanceof Choreography)) {
				this.definitions.getFirstPlane().setBpmnElement(process);
			}
			
			/*
			 * Insert just the shapes of the temporary processes, because
			 * their process elements are already connected to and so inserted 
			 * in the definitions 
			 */
			for(Process p : this.tmpProcesses) {
				this.insertProcessShapes(p);
			}
		}
	}

	/**
	 * Set the reference to the activity related to the compensation.
	 */
	private void setCompensationEventActivityRef() {
		for (BPMNElement element : this.bpmnElements.values()) {
			/*
			 * Processing only necessary for events with compensation event
			 * definition
			 */
			if (!(element.getNode() instanceof Event))
				return;
			if (((Event) element.getNode())
					.getEventDefinitionOfType(CompensateEventDefinition.class) == null)

				if (element.getNode() instanceof BoundaryEvent
						&& ((BoundaryEvent) element.getNode())
								.getEventDefinitionOfType(CompensateEventDefinition.class) != null) {
					BoundaryEvent bEvent = (BoundaryEvent) element.getNode();
					((CompensateEventDefinition) bEvent
							.getEventDefinitionOfType(CompensateEventDefinition.class))
							.setActivityRef(bEvent.getAttachedToRef());
				}
		}
	}

	/**
	 * Set the initiating participant of a choreography activity.
	 */
	private void setInitiatingParticipant() {
		for (BPMNElement element : this.bpmnElements.values()) {
			if (element.getNode() instanceof ChoreographyActivity) {
				ChoreographyActivity activity = (ChoreographyActivity) element
						.getNode();
				for (Participant partici : activity.getParticipantRef()) {
					if (partici.isInitiating()) {
						activity.setInitiatingParticipantRef(partici);
						break;
					}
				}
			}
		}
	}

	/**
	 * Inserts {@link Participant} to the {@link Collaboration} element and
	 * assigns the collaboration to the appropriate {@link BPMNPlane} element.
	 */
	private void insertCollaborationElements() {
		for (BPMNElement bpmnElement : this.bpmnElements.values()) {
			/* Insert participants */
			if (bpmnElement.getNode() instanceof Participant
					&& !((Participant) bpmnElement.getNode())._isChoreographyParticipant) {
				/* Insert semantics element */
				getCollaboration().getParticipant().add(
						(Participant) bpmnElement.getNode());

				/* Insert diagram element */
				this.definitions.getFirstPlane().getDiagramElement().add(
						bpmnElement.getShape());
			}

			/* Conversation elements */
			else if (this.insertConversationElements(bpmnElement)) {

			}

			/* Message Flows */
			else if (bpmnElement.getNode() instanceof MessageFlow) {
				getCollaboration().getMessageFlow().add(
						(MessageFlow) bpmnElement.getNode());
			}

		}

		/* Set collaboration references */
		if (this.collaboration != null) {

			/* Insert shapes of artifacts */
			for (Artifact a : getCollaboration().getArtifact()) {
				this.definitions.getFirstPlane().getDiagramElement().add(
						bpmnElements.get(a.getId()).getShape());
			}

			/* Insert association shapes */
			for (Association a : getCollaboration().getAssociation()) {
				this.definitions.getFirstPlane().getDiagramElement().add(
						bpmnElements.get(a.getId()).getShape());
			}

			this.definitions.getRootElement().add(this.collaboration);
			this.definitions.getFirstPlane().setBpmnElement(this.collaboration);
		}

	}

	/**
	 * Checks if the element is an conversation elements and appends it to the
	 * appropriate list.
	 */
	private boolean insertConversationElements(BPMNElement element) {
		// this.identifyConversation();

		/* Conversation nodes */
		if (element.getNode() instanceof ConversationNode) {
			/* Semantics element */
			getCollaboration().getConversationNode().add(
					(ConversationNode) element.getNode());

			/* Insert diagram element */
			this.definitions.getFirstPlane().getDiagramElement().add(
					element.getShape());

			return true;
		}

		/* Conversation link */
		else if (element.getNode() instanceof ConversationLink) {
			getCollaboration().getConversationLink().add(
					(ConversationLink) element.getNode());
			return true;
		}

		return false;
	}

	/**
	 * Sets the message is visible flag to the first participant
	 * 
	 * @param association
	 */
	public void handleMessageAssociationOnChoreographyActivity(
			Association association) {
		/* Retrieve choreography acitivity */
		ChoreographyActivity choreoActivity = null;
		Message message = null;
		if (association.getSourceRef() instanceof ChoreographyActivity
			&& association.getTargetRef() instanceof Message) {
			choreoActivity = (ChoreographyActivity) association.getSourceRef();
			message = (Message) association.getTargetRef();
		} else if (association.getTargetRef() instanceof ChoreographyActivity
				&& association.getSourceRef() instanceof Message) {
			choreoActivity = (ChoreographyActivity) association.getTargetRef();
			message = (Message) association.getSourceRef();
		}

		if (choreoActivity != null 
				&& message != null
				&& choreoActivity.getParticipantRef().size() > 0) {
			/* Take first participant and set the flag on its shape element */
			Participant p = retrieveParticipantForMessage(choreoActivity, message);
			if(p != null) {
				setMessageVisibleForParticipant(p, message);
			}

			/*
			 * Remove association edge from bpmnElement list, because the
			 * occurrence in the exported XML is not allowed.
			 */

			removeBpmnElement(association);
			return;
		}

		Participant p = null;
		if (association.getSourceRef() instanceof Participant
				&& association.getTargetRef() instanceof Message) {
			p = (Participant) association.getSourceRef();
			message = (Message) association.getTargetRef();
		} else if (association.getTargetRef() instanceof Participant
				&& association.getSourceRef() instanceof Message) {
			p = (Participant) association.getTargetRef();
			message = (Message) association.getSourceRef();
		} else {
			return;
		}

		setMessageVisibleForParticipant(p, message);

		/*
		 * Remove association edge from bpmnElement list, because the occurrence
		 * in the exported XML is not allowed.
		 */

		removeBpmnElement(association);
		return;
	}
	
	/**
	 * 
	 * 
	 * @param choreoActivity
	 * @return
	 */
	private Participant retrieveParticipantForMessage(ChoreographyActivity choreoActivity, Message m) {
		if(choreoActivity.getParticipantRef().isEmpty()) {
			return null;
		}
		
		BPMNShape messageShape = (BPMNShape) bpmnElements.get(m.getId()).getShape();
		double minDistance = -1.0;
		Participant returnParticipant = null;
		
		for(Participant p : choreoActivity.getParticipantRef()) {
			BPMNShape participantShape = (BPMNShape) bpmnElements.get(p.getId()).getShape();
			double distance = DiagramHelper.calculateCenterDistance(messageShape.getBounds(), participantShape.getBounds());
			if(minDistance == -1.0 || distance < minDistance) {
				minDistance = distance;
				returnParticipant = p;
			}
		}
		
		return returnParticipant;
	}
	
	/**
	 * Makes the participant's message visible and stores the name attribute of
	 * a message as a {@link SignavioMessageName} extension element.
	 * 
	 * @param p
	 * @param m
	 */
	private void setMessageVisibleForParticipant(Participant p, Message m) {
		BPMNShape pShape = (BPMNShape) bpmnElements.get(p.getId())
				.getShape();
		pShape.setIsMessageVisible(Boolean.TRUE);
		
		/* Store name of message element */
		if(m.getName() != null && m.getName().length() > 0) {
			ExtensionElements extEle = p.getOrCreateExtensionElements();
			extEle.add(new SignavioMessageName(m.getName()));
		}
		
		/* Set ref to message for later processing */
		p._msgRef = m;
	}

	/**
	 * If a process contains choreography elements the process will be inserted
	 * into a choreography element.
	 */
	private void insertChoreographyProcessesIntoDefinitions() {
		for (Process p : this.processes) {
			if (!p.isChoreographyProcess())
				continue;

			Choreography choreo = new Choreography();
			this.getChoreography().add(choreo);
			choreo.setId(p.getId());
			choreo.setName(p.getName());
			choreo.setIsClosed(p.isIsClosed());

			/*
			 * Insert shapes of the choreograhy elements except edge shape, they
			 * will be appended later to appear on most top layer of the
			 * diagram.
			 */
			for (FlowElement flowEle : p.getFlowElement()) {
				/* Move association into other list */
				if (flowEle instanceof Association) {
					Association association = (Association) flowEle;

					/*
					 * Check whether the association depicts a message to the
					 * choreography activity
					 */
					if ((association.getSourceRef() instanceof ChoreographyActivity && association
							.getTargetRef() instanceof Message)
							|| (association.getSourceRef() instanceof Message && association
									.getTargetRef() instanceof ChoreographyActivity)
//					 || (association.getSourceRef() instanceof Participant
//					 && association.getTargetRef() instanceof Message)
//					 || (association.getSourceRef() instanceof Message
//					 && association.getTargetRef() instanceof Participant)
					) {
						handleMessageAssociationOnChoreographyActivity(association);

						continue;
					}

					choreo.getAssociation().add((Association) flowEle);
					continue;
				}

				choreo.getFlowElement().add(flowEle);
				if (!(flowEle instanceof Edge) && !(flowEle instanceof Message)) {
					this.definitions.getFirstPlane().getDiagramElement().add(
							this.bpmnElements.get(flowEle.getId()).getShape());

					/* Insert participant band elements */
					if (flowEle instanceof ChoreographyActivity) {
						for (Participant participant : ((ChoreographyActivity) flowEle)
								.getParticipantRef()) {
							this.definitions.getFirstPlane()
									.getDiagramElement().add(
											this.bpmnElements.get(
													participant.getId())
													.getShape());
						}
					}
				}

				/* Insert participants of activities into choreography */
				if (flowEle instanceof ChoreographyTask) {
					ChoreographyTask choreoAct = (ChoreographyTask) flowEle;

					/* Insert a message flow from first to last participant */
					choreoAct.createMessageFlows(choreo);
					
					/* Insert the message objects */
					insertMessageElements(choreoAct);

					choreo.getParticipant().addAll(
							((ChoreographyActivity) flowEle)
									.getParticipantRef());
				}

				/* Handle subchoreographies recursively */
				if (flowEle instanceof SubChoreography) {
					choreo.getParticipant().addAll(
							((ChoreographyActivity) flowEle)
									.getParticipantRef());

					((SubChoreography) flowEle).setParticipantsAndMessageFlows(
							choreo, this.bpmnElements, this);

					/* Insert child shape element of a sub choreography */
					List<String> idList = ((SubChoreography) flowEle)
							.getIdsOfDiagramElements();
					
					for (String id : idList) {
						BPMNElement bpmnEl = this.bpmnElements.get(id);
						
						if(bpmnEl != null) {
							this.definitions.getFirstPlane().getDiagramElement()
								.add(bpmnEl.getShape());
						}
					}
				}

			}
			/*
			 * Remove Message elements, because they are not represented by a
			 * shape element
			 */
			for (FlowElement flowEle : p.getFlowElement()) {
				if (flowEle instanceof Message) {
					this.bpmnElements.remove(this.bpmnElements.get(flowEle
							.getId()));
					Association msgAssociation = ((Message) flowEle)
							.getDataConnectingAssociation();
					if (msgAssociation != null) {
						this.bpmnElements.remove(this.bpmnElements
								.get(msgAssociation.getId()));
					}
				}
			}

			/* Insert Artifact elements */
			for (Artifact artifact : p.getArtifact()) {
				if (artifact.getSubChoreography() != null)
					continue;

				/* Semantic element */
				choreo.getArtifact().add(artifact);

				/* DI element */
				this.definitions.getFirstPlane().getDiagramElement().add(
						bpmnElements.get(artifact.getId()).getShape());
			}

			/*
			 * Set bpmn plane reference, maybe overwritten by another
			 * collaboration later
			 */
			this.definitions.getFirstPlane().setBpmnElement(choreo);
		}

		if (this.choreography != null) {
			/* Insert into definitions */
			this.definitions.getRootElement().addAll(this.choreography);
		}
	}
	
	/**
	 * Inserts the {@link Message} object related to that choreography task
	 * @param ct
	 */
	private void insertMessageElements(ChoreographyTask ct) {
		for(MessageFlow mf : ct.getMessageFlows()) {
			if(mf.getMessageRef() != null) {
				this.definitions.getRootElement().add(mf.getMessageRef());
			}
		}
	}

	/**
	 * Sets attributes of the {@link Definitions} element.
	 */
	private void setDefinitionsAttributes() {
		/* Set targetnamespace */
		String targetnamespace = diagram.getProperty("targetnamespace");
		if (targetnamespace == null)
			targetnamespace = "http://www.signavio.com/bpmn20";
		this.definitions.setTargetNamespace(targetnamespace);
		
		/* Set Name attribute */
		String name = diagram.getProperty("name");
		if(name != null && name.length() > 0) {
			this.definitions.setName(name);
		}

		/* Export Tool Information */
		this.definitions
				.setExporter("Signavio Process Editor, http://www.signavio.com");
		this.definitions
				.setExporterVersion((editorVersion != null ? editorVersion : ""));

		/* Additional namespace definitions */
		try {
			String namespacesProperty = this.diagram.getProperty("namespaces");
			JSONObject namespaces = new JSONObject(namespacesProperty);
			JSONArray namespaceItems = namespaces.getJSONArray("items");

			/*
			 * Retrieve namespace declarations and put them to namespaces
			 * attribute.
			 */
			for (int i = 0; i < namespaceItems.length(); i++) {
				JSONObject namespace = namespaceItems.getJSONObject(i);
				this.definitions.getNamespaces().put(
						namespace.getString("prefix"),
						namespace.getString("url"));
			}
		} catch (JSONException e) {
			// ignore namespace property
		} catch (NullPointerException np) {

		}
		
		/* Expression Language */
		String exprLanguage = diagram.getProperty("expressionlanguage");
		if (exprLanguage != null && !(exprLanguage.length() == 0))
			this.definitions.setExpressionLanguage(exprLanguage);

		/* Type Language */
		String typeLanguage = diagram.getProperty("typelanguage");
		if (typeLanguage != null && !(typeLanguage.length() == 0))
			this.definitions.setTypeLanguage(typeLanguage);
		
		/* Other attributes */
		String otherAttrStr = diagram.getProperty("otherattributes");
		if(otherAttrStr == null || otherAttrStr.length() == 0) {
			return;
		}
		
		// process as json array containing json objects
		try {
			JSONArray a = new JSONArray(otherAttrStr);
			for(int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				String localpart = o.optString("localpart");
				String ns = o.optString("ns");
				String prefix = o.optString("prefix");
				String value = o.optString("value");
				
				if((localpart != null || ns != null || prefix != null)
						&& value != null) {
					this.definitions.getOtherAttributes().put(new QName((ns != null ? ns : ""), (localpart != null ? localpart : ""), (prefix != null ? prefix : "")), value);
				}
			}
			
		} catch (JSONException e) {
		}
	}

	/**
	 * Method to create input output specification based on data inputs and
	 * outputs.
	 */
	private void setIOSpecification() {
		for (Task t : this.getAllTasks())
			t.determineIoSpecification();
	}

	/**
	 * Retrieves all elements like Signals and puts them in the appropriate
	 * field of the {@link Definitions} element.
	 */
	private void putGlobalElementsIntoDefinitions() {
		
		for (BPMNElement element : this.bpmnElements.values()) {
			/* Signals */
			if (element.getNode() instanceof Event
					&& ((Event) element.getNode()).isSignalEvent()) {
				SignalEventDefinition sigEvDev = (SignalEventDefinition) ((Event) element
						.getNode())
						.getEventDefinitionOfType(SignalEventDefinition.class);
				sigEvDev.insertSignalIntoDefinitions(definitions);
			}
		}
		
		
		/* Messages */
		this.retrieveAndInsertMessages();
	}

	/**
	 * Inserts all edge diagram element of the BPMN diagram.
	 */
	private void insertEdgeDiagramElements() {
		Process unmappedElementsProcess = new Process();
		unmappedElementsProcess.setId(SignavioUUID.generate());

		for (BPMNElement element : this.bpmnElements.values()) {
			if (element.getNode() instanceof Edge) {

				/* Associations */
				if (element.getNode() instanceof Association
						&& ((Association) element.getNode())._containedInProcess) {
					definitions.getFirstPlane().getDiagramElement().add(
							element.getShape());
				}
				
				/* Data Associations */
				if (element.getNode() instanceof DataAssociation) {
					DataAssociation dataAsso = (DataAssociation) element.getNode();
					if(dataAsso.getSourceRef() != null && dataAsso.getTargetRef() != null 
						&& (dataAsso.getSourceRef() instanceof Activity 
							|| dataAsso.getTargetRef() instanceof Activity)) {
						definitions.getFirstPlane().getDiagramElement().add(
								element.getShape());
					}
				}

				/* Sequence flows */
				if (element.getNode() instanceof SequenceFlow) {
					/*
					 * Check if the sequence flow element is assigned to a
					 * process element.
					 */
					if (((SequenceFlow) element.getNode()).getProcess() == null
							&& ((SequenceFlow) element.getNode())
									.getSubChoreography() == null
							&& ((SequenceFlow) element.getNode())
									.getSubProcess() == null) {
						unmappedElementsProcess.getFlowElement().add(
								(FlowElement) element.getNode());
					}
					definitions.getFirstPlane().getDiagramElement().add(
							element.getShape());
				}

				/* Message flows */
				if (element.getNode() instanceof MessageFlow) {
					definitions.getFirstPlane().getDiagramElement().add(
							element.getShape());
				}

				/* Conversation Links */
				if (element.getNode() instanceof ConversationLink) {
					definitions.getFirstPlane().getDiagramElement().add(
							element.getShape());
				}
			}
		}

		if (unmappedElementsProcess.getFlowElement().size() > 0) {
			this.definitions.getRootElement().add(unmappedElementsProcess);
		}
	}

	/**
	 * Retrieves a BPMN 2.0 diagram and transforms it into the BPMN 2.0 model.
	 * 
	 * @param diagram
	 *            The BPMN 2.0 {@link GenericDiagram} based on the ORYX JSON.
	 * @return The definitions root element of the BPMN 2.0 model.
	 * @throws BpmnConverterException
	 */
	public Definitions getDefinitionsFromDiagram()
			throws BpmnConverterException {

		/* Build-up the definitions as root element of the document */
		this.setDefinitionsAttributes();

		/* Convert shapes to BPMN 2.0 elements */

		try {
			createBpmnElementsRecursively(diagram);
		} catch (Exception e) {
			/* Pack exceptions in a BPMN converter exception */
			throw new BpmnConverterException(
					"Error while converting to BPMN model", e);
		}

		this.detectBoundaryEvents();
		this.detectConnectors();
		this.setInitiatingParticipant();

		/* Section to handle data concerning aspects */
		this.updateDataAssociationsRefs();
		this.setIOSpecification();

		this.setDefaultSequenceFlowOfExclusiveGateway();
		this.setCompensationEventActivityRef();
		// this.setConversationParticipants();

		this.identifyProcesses();

		this.handleDataObjects();
		this.handleArtifacts();
		this.addAssociationsToProcess();
		this.addAssociationsToConversation();
		this.createProcessIOSpec();
		this.processChoreographies();
		
		/* Globally defined elements */
		this.putGlobalElementsIntoDefinitions();
		this.putCalledElementsIntoDefinitions();
		
		/* Insert elements into diagram */

		this.insertCollaborationElements();
		this.insertChoreographyProcessesIntoDefinitions();
		this.insertProcessesIntoDefinitions();

		/*
		 * Insert diagram element of edge last, because they are on top of all
		 * other elements
		 */
		this.insertEdgeDiagramElements();

		this.insertDiagramsForLinkedSubprocesses();

		this.determineUnusedNamespaceDeclarations();
		
		this.collectExternalNamespaces();
		
		this.ensureSignavioUUIDStyle();
		
		this.checkUniquenessOfProcessIDs();
		
		return definitions;
	}
	
	/**
	 * Encapsulates methods for process task related to the export of
	 * choreography elements.
	 */
	private void processChoreographies() {
		for (Process p : this.processes) {
			if (!p.isChoreographyProcess())
				continue;

			for (FlowElement flowEle : p.getFlowElement()) {
				/* Handle messages connected to a choreography task */
				if (flowEle instanceof Association) {
					Association association = (Association) flowEle;

					/*
					 * Check whether the association depicts a message to the
					 * choreography activity
					 */
					if ((association.getSourceRef() instanceof ChoreographyActivity && association
							.getTargetRef() instanceof Message)
							|| (association.getSourceRef() instanceof Message && association
									.getTargetRef() instanceof ChoreographyActivity)
					) {
						handleMessageAssociationOnChoreographyActivity(association);
					}
				}
			}
		}
		
	}

	/**
	 * Ensures all element id have the same Signavio defined style
	 */
	private void ensureSignavioUUIDStyle() {
//		if(this.configuration != null && this.configuration.ensureSignavioStyle) {
			for(BPMNElement bpmnEl : bpmnElements.values()) {
				if(!SignavioIDChecker.isValidID(bpmnEl.getId(), this.configuration)) {
					/*
					 * Generate and set new ID
					 */
					String newId = SignavioUUID.generate();
					
					bpmnEl.setId(newId);
					bpmnEl.getNode().setId(newId);
					bpmnEl.getShape().setId(newId + "_gui");
				}
			}
//		}
	}
	
	/**
	 * Retrieves the called elements and puts them into the root elements section 
	 * as global defined elements.
	 */
	private void putCalledElementsIntoDefinitions() {
		for(BPMNElement bpmnEl : bpmnElements.values()) {
			if(bpmnEl.getNode() instanceof CallingElement) {
				this.definitions.getRootElement().addAll(((CallingElement) bpmnEl.getNode()).getCalledElements());
			}
		}
	}
	
	/**
	 * Creates the {@link InputOutputSpecification} for each process element.
	 * Every data input and output element is moved to the {@link InputOutputSpecification}
	 * and an additional data input/output reference is created.
	 */
	private void createProcessIOSpec() {
		
		for(Process p : this.processes) {
			InputOutputSpecification ioSpec = new InputOutputSpecification();
			OutputSet outputSet = new OutputSet();
			InputSet inputSet = new InputSet();
			ioSpec.getOutputSet().add(outputSet);
			ioSpec.getInputSet().add(inputSet);
			List<FlowElement> elementsToBeRemoved = new ArrayList<FlowElement>();
			
			for(FlowElement flowEl : p.getFlowElement()) {
				if (flowEl instanceof DataInput) {
					ioSpec.getDataInput().add((DataInput) flowEl);
					inputSet.getDataInputRefs().add((DataInput) flowEl);
					elementsToBeRemoved.add(flowEl);
				}
				
				else if (flowEl instanceof DataOutput) {
					ioSpec.getDataOutput().add((DataOutput) flowEl);
					outputSet.getDataOutputRefs().add((DataOutput) flowEl);
					elementsToBeRemoved.add(flowEl);
				}
			}
			
			p.getFlowElement().removeAll(elementsToBeRemoved);
			
			/* Insert specification */
			if(ioSpec.getDataInput().size() > 0 || ioSpec.getDataOutput().size() > 0) {
				p.setIoSpecification(ioSpec);
			}
		}
		
		
	}

	/**
	 * Collects all {@link Message}s referenced by {@link MessageFlow}s and 
	 * inserts them as global defined elements. 
	 */
	private void retrieveAndInsertMessages() {
		for (BPMNElement bpmnElement : bpmnElements.values()) {
			/* Message attached to a message flow */
			if(bpmnElement.getNode() instanceof MessageFlow) {
				MessageFlow msgFlow = (MessageFlow) bpmnElement.getNode();
				if(msgFlow.getMessageRef() != null) {
					this.definitions.getRootElement().add(msgFlow.getMessageRef());
				}
			} 
			
			/* Ignore messages on the canvas */
			else if(bpmnElement.getNode() instanceof Message) {
				Message m = (Message) bpmnElement.getNode();
				if(m.getProcess() != null && !m.getProcess().isChoreographyProcess()) {
					m.getProcess().removeChild(m);
//					this.definitions.getRootElement().add(m);
//					this.definitions.getFirstPlane().getDiagramElement().add(this.bpmnElements.get(m.getId()).getShape());
				}
			}
		}
	}

	private void insertDiagramsForLinkedSubprocesses() {
		for (BPMNElement bpmnel : this.bpmnElements.values()) {
			/*
			 * Basic subprocesses, sub and call choreographies
			 */
			if (bpmnel.getNode() instanceof ContainerElement) {
				List<BPMNDiagram> diagramList = createBPMNDiagramForContainerElement((ContainerElement) bpmnel.getNode());
				this.definitions.getDiagram().addAll(diagramList);
			} 
			
			/*
			 * Insert choreography element referenced by call choreography
			 */
			if(bpmnel.getNode() instanceof CallChoreography) {
				CallChoreography callChoreo = (CallChoreography) bpmnel.getNode();
				
				if(callChoreo.getCalledChoreographyRef() != null
					&& !(callChoreo.getCalledChoreographyRef() instanceof GlobalChoreographyTask)) {
					this.definitions.getRootElement().add(callChoreo.getCalledChoreographyRef());
				}
			}
			
			/*
			 * Insert elements referenced by a collapsed call activity sub process
			 */
			if(bpmnel.getNode() instanceof CallActivity 
					&& ((CallActivity) bpmnel.getNode())._diagramElement != null) {
				this.definitions.getDiagram().add(((CallActivity) bpmnel.getNode())._diagramElement);
			}
		}
	}
	
	

	/**
	 * Creates the {@link BPMNDiagram}s for a collapsed sub process and 
	 * choreography .
	 * 
	 * @param container
	 * 
	 * @return
	 */
	private List<BPMNDiagram> createBPMNDiagramForContainerElement(
			ContainerElement container) {
		List<BPMNDiagram> diagramList = new ArrayList<BPMNDiagram>();

		if (container._getDiagramElements().size() != 0) {
			BPMNDiagram diagram = new BPMNDiagram();
			diagram.getBPMNPlane().setBpmnElement((BaseElement) container);
			diagram.getBPMNPlane().getDiagramElement().addAll(
					container._getDiagramElements());
			diagramList.add(diagram);
		}

		/* Child subprocesses */
		for (FlowElement flowEl : container.getFlowElement()) {
			if (flowEl instanceof ContainerElement) {
				List<BPMNDiagram> subDiagramList = createBPMNDiagramForContainerElement((ContainerElement) flowEl);
				diagramList.addAll(subDiagramList);
			}
		}

		return diagramList;
	}

	/* Getter & Setter */

	/**
	 * @return The list of BPMN 2.0 's stencil set edgeIds
	 */
	public static HashSet<String> getEdgeIds() {
		return edgeIds;
	}

	/**
	 * @return the collaboration
	 */
	private Collaboration getCollaboration() {
		if (this.collaboration == null) {
			this.collaboration = new Collaboration();
			this.collaboration.setId(SignavioUUID.generate());
		}
		return this.collaboration;
	}

	/**
	 * @return the choreography element. If an collaboration already exists it
	 *         it converts the collaboration to an choreography element.
	 */
	private List<Choreography> getChoreography() {
		// /* Neither collaboration nor choreography existing */
		// if (this.collaboration == null) {
		// this.collaboration = new Choreography();
		// }
		// /* Collaboration exiting */
		// else if(this.collaboration instanceof Collaboration)
		// return (Choreography) this.collaboration;

		if (this.choreography == null) {
			this.choreography = new ArrayList<Choreography>();
		}

		return this.choreography;
	}

	/**
	 * Removes the {@link BPMNElement} identified by the contained
	 * {@link BaseElement} from the list of all BPMNElements.
	 * 
	 * @param node
	 */
	private void removeBpmnElement(BaseElement node) {
		BPMNElement bpmnElement = null;
		for (BPMNElement element : bpmnElements.values()) {
			if (element.getNode().equals(node)) {
				bpmnElement = element;
				break;
			}
		}

		if (bpmnElement != null) {
			bpmnElements.remove(bpmnElement.getId());
		}
	}

	/**
	 * Checks if a vendor specific namespace declaration is not in use and marks
	 * it for removal.
	 */
	private void determineUnusedNamespaceDeclarations() {
		List<String> unusedNamespacePrefixes = new ArrayList<String>(
				BPMNPrefixMapper.getCustomExtensions().values());

		for (BPMNElement element : this.bpmnElements.values()) {
			if (element.getCustomNamespaces() != null) {
				for (String prefix : Arrays.asList(element
						.getCustomNamespaces())) {
					unusedNamespacePrefixes.remove(prefix);
				}
			}
		}

		this.definitions.unusedNamespaceDeclarations = unusedNamespacePrefixes;
	}
	
	/**
	 * Collects the namespace definitions provided by xml elements from
	 * external sources like Activiti
	 */
	private void collectExternalNamespaces() {
		Map<String, String> nsDefs = new HashMap<String, String>();
		
		for(BPMNElement el : this.bpmnElements.values()) {
			nsDefs.putAll(el.getExternalNamespaceDefinitions());
			nsDefs.putAll(el.getNode().getExternalNamespaceDefinitions());
		}
		
		this.definitions.externalNSDefs = nsDefs;
	}
	
	/**
	 * Checks if all process IDs are valid and generates new ones in case of 
	 * a negative check.
	 */
	private void checkUniquenessOfProcessIDs() {
		Set<String> pIDs = new HashSet<String>();
		for(Process p : this.processes) {
			if(!p.hasId() || pIDs.contains(p.getId())) {
				p.setId(SignavioUUID.generate());
				pIDs.add(p.getId());
			}
			
			pIDs.add(p.getId());
		}
	}

	public static Constants getConstants() {
		return constants;
	}

	public static void setConstants(Constants constants) {
		Diagram2BpmnConverter.constants = constants;
	}
}
