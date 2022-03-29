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
package org.apromore.processmining.plugins.bpmn;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apromore.jgraph.graph.AbstractCellView;
import org.apromore.jgraph.graph.DefaultGraphCell;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Association;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.MessageFlow;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.TextAnnotation;
import org.apromore.processmining.models.jgraph.ProMGraphModel;
import org.apromore.processmining.models.jgraph.ProMJGraph;
import org.apromore.processmining.models.jgraph.ProMJGraphVisualizer;
import org.apromore.processmining.models.jgraph.elements.ProMGraphCell;
import org.apromore.processmining.models.jgraph.elements.ProMGraphEdge;
import org.apromore.processmining.models.jgraph.elements.ProMGraphPort;
import org.apromore.processmining.models.jgraph.views.JGraphPortView;
import org.apromore.processmining.plugins.bpmn.diagram.BpmnDcBounds;
import org.apromore.processmining.plugins.bpmn.diagram.BpmnDiEdge;
import org.apromore.processmining.plugins.bpmn.diagram.BpmnDiPlane;
import org.apromore.processmining.plugins.bpmn.diagram.BpmnDiShape;
import org.apromore.processmining.plugins.bpmn.diagram.BpmnDiWaypoint;
import org.apromore.processmining.plugins.bpmn.diagram.BpmnDiagram;
import org.xmlpull.v1.XmlPullParser;

/**
 * <b>BpmnDefinitions</b> is the root class representing the top <definitions> tag in BPMN 2.0 files.<br>
 * All contained elements and sub-elements represent other hierarchical elements in the file.<br>
 * Altogether, they literally represent tags in BPMN 2.0 files after import, i.e. they are not object references<br>
 * For example, a sequence flow tag only contains attributes, no links to its source and target node objects.<br>
 *
 * <b>Key operations:</b><br>
 * <ul>
 *     <li>Importing is the process of importing a .bpmn file into a BpmnDefinitions object.</li>
 *     <li>Exporting is the process of exporting a BpmnDefinitions object to a .bpmn file representation (string)</li>
 *     <li>Marshalling is the process of converting a BpmnDefinitions to {@link BPMNDiagram} object. Attributes are read and
 *     objects are linked.</li>
 *     <li>Unmarshalling is the reverse process of converting from a BPMNDiagram object to BpmnDefinitions.</li>
 * </ul>
 *
 * @author Anna Kalenkova
 * @author Bruce Nguyen:
 * 	19 Oct 2021: Add class comments
 */
public class BpmnDefinitions extends BpmnElement {

	protected Collection<BpmnResource> resources;
	protected Collection<BpmnProcess> processes;
	protected Collection<BpmnCollaboration> collaborations;
	protected Collection<BpmnMessage> messages;	
	protected Collection<BpmnDiagram> diagrams;
	
	public BpmnDefinitions(String tag) {
		super(tag);
		resources = new HashSet<BpmnResource>();
		processes = new HashSet<BpmnProcess>();
		collaborations = new HashSet<BpmnCollaboration>();
		messages = new HashSet<BpmnMessage>();
		diagrams = new HashSet<BpmnDiagram>();
	}
	
	public BpmnDefinitions(String tag, BpmnDefinitionsBuilder builder) {
		super(tag);
		resources = builder.resources;
		processes = builder.processes;
		collaborations = builder.collaborations;
		messages = new HashSet<BpmnMessage>();		
		diagrams = builder.diagrams;
	}
	
	/**
	 * Builds a BPMN model (BpmnDefinitions) from BPMN diagram
	 *
	 * @author Anna Kalenkova
	 * Sep 22, 2013
	 * @modified: Bruce Nguyen
	 *     - Add ProMJGraph to constructor to reuse an existing layout
	 */
	public static class BpmnDefinitionsBuilder {

		protected Collection<BpmnResource> resources;
		protected Collection<BpmnProcess> processes;
		protected Collection<BpmnCollaboration> collaborations;	
		protected Collection<BpmnDiagram> diagrams;
		
		public BpmnDefinitionsBuilder(BPMNDiagram diagram) {
			resources = new HashSet<BpmnResource>();
			processes = new HashSet<BpmnProcess>();
			collaborations = new HashSet<BpmnCollaboration>();
			diagrams = new HashSet<BpmnDiagram>();

			buildFromDiagram(diagram, null);
		}
		
        public BpmnDefinitionsBuilder(BPMNDiagram diagram, ProMJGraph layoutInfo) {
            resources = new HashSet<BpmnResource>();
            processes = new HashSet<BpmnProcess>();
            collaborations = new HashSet<BpmnCollaboration>();
            diagrams = new HashSet<BpmnDiagram>();

            buildFromDiagram(diagram, layoutInfo);
        }		

		/**
		 * Build BpmnDefinitions from BPMNDiagram (BPMN picture)
		 * 
		 * @param context if null, then graphics info is not added
		 * @param diagram
		 */
		private void buildFromDiagram(BPMNDiagram diagram, ProMJGraph layoutInfo) {
			BpmnCollaboration bpmnCollaboration = new BpmnCollaboration("collaboration");
			bpmnCollaboration.setId("col_" + bpmnCollaboration.hashCode());

			// Build pools and participants
			for (Swimlane pool : diagram.getPools()) {
				BpmnParticipant bpmnParticipant = new BpmnParticipant("participant");
				bpmnParticipant.id = pool.getId().toString().replace(' ', '_');
				bpmnParticipant.name = pool.getLabel();
				// If pool is not a "black box", create a process
				if (!pool.getChildren().isEmpty()) {
					BpmnProcess bpmnProcess = new BpmnProcess("process");
					bpmnProcess.marshall(diagram, pool);
					bpmnProcess.setId("proc_" + bpmnProcess.hashCode());
					processes.add(bpmnProcess);
					bpmnParticipant.setProcessRef(bpmnProcess.getId());
				}
				bpmnCollaboration.addParticipant(bpmnParticipant);
			}

			// Discover "internal" process
			BpmnProcess intBpmnProcess = new BpmnProcess("process");
			intBpmnProcess.setId("proc_" + intBpmnProcess.hashCode());
			// If there are elements without parent pool, add process
			if (intBpmnProcess.marshall(diagram, null)) {
				processes.add(intBpmnProcess);
			}

			// Build message flows
			for (MessageFlow messageFlow : diagram.getMessageFlows()) {
				BpmnMessageFlow bpmnMessageFlow = new BpmnMessageFlow("messageFlow");
				bpmnMessageFlow.marshall(messageFlow);
				bpmnCollaboration.addMessageFlow(bpmnMessageFlow);
			}
			
			// Build text annotations
			for (TextAnnotation textAnnotation : diagram.getTextAnnotations(null)) {
				BpmnTextAnnotation bpmnTextAnnotation = new BpmnTextAnnotation("textAnnotation");
				bpmnTextAnnotation.marshall(textAnnotation);
				bpmnCollaboration.addTextAnnotation(bpmnTextAnnotation);;
			}
			
			// Build associations
			for (Association association : diagram.getAssociations(null)) {
				BpmnAssociation bpmnAssociation = new BpmnAssociation("association");
				bpmnAssociation.marshall(association);
				bpmnCollaboration.addAssociation(bpmnAssociation);
			}
			
			// Build resources
			for(Swimlane swimlane : diagram.getSwimlanes()) {
				if(swimlane.getPartitionElement() != null) {
					BpmnResource resource = new BpmnResource("resource");
					resource.marshall(swimlane);
					resources.add(resource);
				}
			}
			
			// Build graphics info
			BpmnDiagram bpmnDiagram = new BpmnDiagram("bpmndi:BPMNDiagram");
			bpmnDiagram.setId("id_" + diagram.hashCode());
			BpmnDiPlane plane = new BpmnDiPlane("bpmndi:BPMNPlane");
			if (diagram.getPools().size() > 0) {
				collaborations.add(bpmnCollaboration);
				plane.setBpmnElement(bpmnCollaboration.id);
			}
			else {
				plane.setBpmnElement(intBpmnProcess.id);
			}
			bpmnDiagram.addPlane(plane);
			if (layoutInfo == null) {
			    fillGraphicsInfo(diagram, bpmnDiagram, plane);
			}
			else {
			    fillGraphicsInfo(diagram, bpmnDiagram, plane, layoutInfo);
			}
//			if(context != null) {
//				fillGraphicsInfo(context, diagram, bpmnDiagram, plane);
//			}

			diagrams.add(bpmnDiagram);
		}

		/**
		 * Fill graphics info
		 * 
		 * @param context
		 * @param diagram
		 * @param bpmnDiagram
		 * @param plane
		 */
		private synchronized static void fillGraphicsInfo(BPMNDiagram diagram,
				BpmnDiagram bpmnDiagram, BpmnDiPlane plane) {

			// Construct graph info
			//ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(context, diagram);
			ProMJGraph layoutInfo = ProMJGraphVisualizer.instance().visualizeGraph(diagram);
			fillGraphicsInfo(diagram, bpmnDiagram, plane, layoutInfo);
		}
		
		// Bruce added 30 Mar 2020
		private synchronized static void fillGraphicsInfo(BPMNDiagram diagram,
                BpmnDiagram bpmnDiagram, BpmnDiPlane plane, ProMJGraph layoutInfo) {
            ProMGraphModel graphModel = layoutInfo.getModel();
            for (Object o : graphModel.getRoots()) {
                if (o instanceof ProMGraphCell) {
                    ProMGraphCell graphCell = (ProMGraphCell) o;
                    addCellGraphicsInfo(graphCell, plane);
                }
                if (o instanceof ProMGraphPort) {
                    ProMGraphPort graphPort = (ProMGraphPort) o;
                    if(graphPort.getBoundingNode() != null) {
                        addCellGraphicsInfo(graphPort, plane);
                    }
                }
                if (o instanceof ProMGraphEdge) {
                    ProMGraphEdge graphEdge = (ProMGraphEdge) o;
                    addEdgeGraphInfo(graphEdge, plane);
                }
            }
        }
		
		/**
		 * Retrieve graphics info from graphCell
		 * 
		 * @param graphCell
		 * @param plane
		 */
		private static void addCellGraphicsInfo(DefaultGraphCell graphCell, BpmnDiPlane plane) {
			DirectedGraphNode graphNode = null;
			if(graphCell instanceof ProMGraphCell) {
				graphNode = ((ProMGraphCell)graphCell).getNode();
			} else if (graphCell instanceof ProMGraphPort) {
				graphNode = ((ProMGraphPort)graphCell).getBoundingNode();
			}
			// Create BPMNShape
			String bpmnElement = graphNode.getId().toString().replace(' ', '_');
			boolean isExpanded = false;
			boolean isHorizontal = false;
			if(graphNode instanceof SubProcess) {
				SubProcess subProcess = (SubProcess)graphNode;
				if(!subProcess.isBCollapsed()) {
					isExpanded = true;
				}
			}
			if(graphNode instanceof Swimlane) {				
				isExpanded = true;
				isHorizontal = true;
			}
			AbstractCellView view = null;
			if(graphCell instanceof ProMGraphCell) {
				view = ((ProMGraphCell)graphCell).getView();
			} else if(graphCell instanceof ProMGraphPort) {
				view = ((ProMGraphPort)graphCell).getView();
			}
			Rectangle2D rectangle = view.getBounds();
			
			double x = rectangle.getX();
			double y = rectangle.getY();
			double width = rectangle.getWidth();
			double height = rectangle.getHeight();
			
			BpmnDcBounds bounds = new BpmnDcBounds("dc:Bounds", x, y, width, height);
			BpmnDiShape shape = new BpmnDiShape("bpmndi:BPMNShape", bpmnElement, bounds, isExpanded, isHorizontal);
			plane.addShape(shape);
			addChildGrapInfo(graphCell, plane);
		}
		
		/**
		 * Retrieve graphics info from graphEdge
		 * 
		 * @param graphEdge
		 * @param plane
		 */
		private static void addEdgeGraphInfo(ProMGraphEdge graphEdge, BpmnDiPlane plane) {
			@SuppressWarnings("rawtypes")
			BPMNEdge bpmnEdge = (BPMNEdge)graphEdge.getEdge();
			// Create BPMNEdge
			String bpmnElement = bpmnEdge.getEdgeID().toString().replace(' ', '_');

			BpmnDiEdge edge = new BpmnDiEdge("bpmndi:BPMNEdge", bpmnElement);
			for (Object point : graphEdge.getView().getPoints()) {
				Point2D point2D;
				if(point instanceof JGraphPortView) {
					JGraphPortView portView = (JGraphPortView) point;
					point2D = portView.getLocation();
				} else if(point instanceof Point2D) {
					point2D = (Point2D)point;
				} else {
					continue;
				}
				double x = point2D.getX();
				double y = point2D.getY();
				BpmnDiWaypoint waypoint = new BpmnDiWaypoint("di:waypoint", x, y);
				edge.addWaypoint(waypoint);
			}
			plane.addEdge(edge);
		}
		
		/**
		 * Retrieve graphics info for child elements
		 * 
		 * @param graphCell
		 * @param plane
		 */
		private static void addChildGrapInfo(DefaultGraphCell graphCell, BpmnDiPlane plane){
			for (Object o : graphCell.getChildren()) {
				if (o instanceof ProMGraphCell) {
					ProMGraphCell childGraphCell = (ProMGraphCell) o;
					addCellGraphicsInfo(childGraphCell, plane);
				}
				if (o instanceof ProMGraphPort) {
					ProMGraphPort childGraphPort = (ProMGraphPort) o;
					if(childGraphPort.getBoundingNode() != null) {
						addCellGraphicsInfo(childGraphPort, plane);
					}
				}
				if (o instanceof ProMGraphEdge) {
					ProMGraphEdge childGraphEdge = (ProMGraphEdge) o;
					addEdgeGraphInfo(childGraphEdge, plane);
				}
			}
		}
	}
	
	@Override
    protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("process")) {
			BpmnProcess process = new BpmnProcess("process");
			process.importElement(xpp, bpmn);
			processes.add(process);
			return true;
		} else if (xpp.getName().equals("collaboration")) {
			BpmnCollaboration collaboration = new BpmnCollaboration("collaboration");
			collaboration.importElement(xpp, bpmn);
			collaborations.add(collaboration);
			return true;
		} else if (xpp.getName().equals("message")) {
			BpmnMessage message = new BpmnMessage("message");
			message.importElement(xpp, bpmn);
			messages.add(message);
			return true; 
		} else if (xpp.getName().equals("resource")) {
			BpmnResource resource = new BpmnResource("resource");
			resource.importElement(xpp, bpmn);
			resources.add(resource);
			return true;
		} else if (xpp.getName().equals("BPMNDiagram")) {
			BpmnDiagram diagram = new BpmnDiagram("BPMNDiagram");
			diagram.importElement(xpp, bpmn);
			diagrams.add(diagram);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	@Override
    public String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
        for (BpmnCollaboration collaboration : collaborations) {
            s += collaboration.exportElement();
        }
		for (BpmnResource resource : resources) {
			s += resource.exportElement();
		}
		for (BpmnProcess process : processes) {
			s += process.exportElement();
		}
		for (BpmnMessage message : messages) {
			s += message.exportElement();
		}
		for (BpmnDiagram diagram : diagrams) {
			s += diagram.exportElement();
		}
		return s;
	}
	
	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallParticipants(diagram, id2node, id2lane);
		}
		for (BpmnProcess process : processes) {
			process.unmarshall(diagram, id2node, id2lane);
		}
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallMessageFlows(diagram, id2node);
			collaboration.unmarshallTextAnnotations(diagram, id2node);
			collaboration.unmarshallAssociations(diagram, id2node);
		}
		for (BpmnDiagram bpmnDiagram : diagrams) {
			bpmnDiagram.unmarshallIsExpanded(id2node);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane) {
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallParticipants(diagram, elements, id2node, id2lane);
		}
		for (BpmnProcess process : processes) {
			process.unmarshall(diagram, elements, id2node, id2lane);
		}
		for (BpmnCollaboration collaboration : collaborations) {
			collaboration.unmarshallMessageFlows(diagram, elements, id2node);
			collaboration.unmarshallTextAnnotations(diagram, elements, id2node);
			collaboration.unmarshallAssociations(diagram, elements, id2node);
		}
		for (BpmnDiagram bpmnDiagram : diagrams) {
			bpmnDiagram.unmarshallIsExpanded(id2node);
		}
	}
	
	public Collection<BpmnResource> getResources() {
		return resources;
	}
	
	public Collection<BpmnProcess> getProcesses() {
		return processes;
	}

	public Collection<BpmnCollaboration> getCollaborations() {
		return collaborations;
	}
	
	public Collection<BpmnMessage> getMessages() {
		return messages;
	}
	
	public Collection<BpmnDiagram> getDiagrams() {
		return diagrams;
	}
}
