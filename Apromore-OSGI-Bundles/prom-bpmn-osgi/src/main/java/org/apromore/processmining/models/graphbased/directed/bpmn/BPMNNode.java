package org.apromore.processmining.models.graphbased.directed.bpmn;

import java.awt.Color;

import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;

public abstract class BPMNNode extends AbstractDirectedGraphNode implements ContainableDirectedGraphElement {
	
	public static final Color ABSTRACTBACKGROUNDCOLOR = new Color(120, 140, 248);
	public static final Color ABSTRACTBORDERCOLOR = new Color(20, 20, 20);
	public static final Color ABSTRACTTEXTCOLOR = new Color(10, 10, 10, 240);

	public static final Color ADJACENTBACKGROUNDCOLOR = new Color(255, 255, 255);

	public static final Color CLUSTERBACKGROUNDCOLOR = new Color(120, 140, 248);
	public static final Color CLUSTERBORDERCOLOR = new Color(20, 20, 20);
	public static final Color CLUSTERTEXTCOLOR = new Color(10, 10, 10, 240);

	public static final Color EDGECOLOR = new Color(150, 150, 150);
	public static final Color EDGECORRELATEDCOLOR = new Color(20, 20, 20);
	public static final Color EDGEUNCORRELATEDCOLOR = new Color(200, 200, 200);

	public static final Color LABELCOLOR = new Color(120, 120, 120);

	public static final Color PRIMITIVEBACKGROUNDCOLOR = new Color(240, 230, 200);
	public static final Color PRIMITIVEBORDERCOLOR = new Color(20, 20, 20);
	public static final Color PRIMITIVETEXTCOLOR = new Color(0, 0, 0, 230);
	
	
	private final AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> graph;
	private SubProcess parentSubProcess;
	private Swimlane parentSwimlane;

	public BPMNNode(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram) {
		super();
		graph = bpmndiagram;
	}

	public BPMNNode(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			Swimlane parentSwimlane) {
		this(bpmndiagram);
		this.parentSwimlane = parentSwimlane;
		if (parentSwimlane != null) {
			parentSwimlane.addChild(this);
		}
	}

	public BPMNNode(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			SubProcess parentSubProcess) {
		this(bpmndiagram);
		this.parentSubProcess = parentSubProcess;
		if (parentSubProcess != null) {
			parentSubProcess.addChild(this);
		}
	}

	public AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> getGraph() {
		return graph;
	}

	public ContainingDirectedGraphNode getParent() {
		if ((parentSubProcess != null) && (parentSwimlane != null)) {
			if(parentSwimlane.equals(parentSubProcess.getParentSwimlane())) {
				return parentSubProcess;
			} else{
				return parentSwimlane;
			}
		}
		else if (parentSubProcess != null)
			return parentSubProcess;
		else if (parentSwimlane != null)
			return parentSwimlane;
		else
			return null;
	}

	public Swimlane getParentSwimlane() {
		if (getParent() != null) {
			if (getParent() instanceof Swimlane)
				return (Swimlane) getParent();
			else
				return null;
		}
		return null;
	}
	
	public Swimlane getParentLane() {
		ContainingDirectedGraphNode parent = getParent();
		if (parent != null) {
			if ((parent instanceof Swimlane) 
					&& ((Swimlane)parent).getSwimlaneType().equals(SwimlaneType.LANE))
				return (Swimlane) parent;
			else
				return null;
		}
		return null;
	}
	
	public Swimlane getParentPool() {
		ContainingDirectedGraphNode parent = getParent();
		while (parent != null) {
			if ((parent instanceof Swimlane) 
					&& ((Swimlane) parent).getSwimlaneType().equals(SwimlaneType.POOL)) {
				return (Swimlane) parent;
			} else {
				if (parent instanceof ContainableDirectedGraphElement) {
					parent = ((ContainableDirectedGraphElement) parent).getParent();
				} else {
					return null;
				}
			}
		}
		return null;
	}
	
	public SubProcess getParentSubProcess() {
		if (getParent() != null) {
			if (getParent() instanceof SubProcess)
				return (SubProcess) getParent();
			else
				return null;
		}
		return null;
	}
	
	public SubProcess getAncestorSubProcess() {
		if (getParent() != null) {
			if (getParent() instanceof SubProcess) {
				return (SubProcess) getParent();
			} else if (getParent() instanceof Swimlane) {
				return ((Swimlane)getParent()).getParentSubProcess();
			} else {
				return null;
			}
		}
		return null;
	}
	
	public void setParentSwimlane(Swimlane swimlane) {
		if(this.parentSwimlane != null) {
			this.parentSwimlane.getChildren().remove(this);
		}
		this.parentSwimlane = swimlane;
		if(swimlane != null) {
			swimlane.addChild(this);
		}
	}
	public void setParentSubprocess(SubProcess subprocess) {
		if(this.parentSubProcess != null) {
			this.parentSubProcess.getChildren().remove(this);
		}
		this.parentSubProcess = subprocess;
		if(subprocess != null) {
			subprocess.addChild(this);
		}
	}
}
