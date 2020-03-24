package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingConstants;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;


public class SubProcess extends Activity implements Decorated,ContainingDirectedGraphNode {

	private final Set<ContainableDirectedGraphElement> children;
	
	private boolean triggeredByEvent = false;

	/**
	 * Create a subprocess without parent
	 * @param bpmndiagram
	 * @param label
	 * @param looped
	 * @param adhoc
	 * @param compensation
	 * @param multiinstance
	 * @param collapsed
	 */
	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance, boolean collapsed) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed);
		children = new HashSet<ContainableDirectedGraphElement>();
		fillAttributes();
	}

	/**
	 * Create a subprocess with parent subprocess
	 * @param bpmndiagram
	 * @param label
	 * @param looped
	 * @param adhoc
	 * @param compensation
	 * @param multiinstance
	 * @param collapsed
	 * @param parentSubProcess
	 */
	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance,
			boolean collapsed, SubProcess parentSubProcess) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed, parentSubProcess);
		children = new HashSet<ContainableDirectedGraphElement>();
		fillAttributes();
	}

	/**
	 * Create a subprocess with parent swimlane
	 * @param bpmndiagram
	 * @param label
	 * @param looped
	 * @param adhoc
	 * @param compensation
	 * @param multiinstance
	 * @param collapsed
	 * @param parentSwimlane
	 */
	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance,
			boolean collapsed, Swimlane parentSwimlane) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed, parentSwimlane);
		children = new HashSet<ContainableDirectedGraphElement>();
		fillAttributes();
	}
	
	/**
	 * Create a subprocess without parent and with triggeredByEvent param
	 * @param bpmndiagram
	 * @param label
	 * @param looped
	 * @param adhoc
	 * @param compensation
	 * @param multiinstance
	 * @param collapsed
	 * @param triggeredByEvent
	 */
	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance, boolean collapsed,
			boolean triggeredByEvent) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed);
		children = new HashSet<ContainableDirectedGraphElement>();
		this.triggeredByEvent = triggeredByEvent;
		fillAttributes();
	}

	/**
	 * Create a subprocess with parent subprocess and with triggeredByEvent param
	 * @param bpmndiagram
	 * @param label
	 * @param looped
	 * @param adhoc
	 * @param compensation
	 * @param multiinstance
	 * @param collapsed
	 * @param triggeredByEvent
	 * @param parentSubProcess
	 */
	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance,
			boolean collapsed, boolean triggeredByEvent, SubProcess parentSubProcess) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed, parentSubProcess);
		children = new HashSet<ContainableDirectedGraphElement>();
		this.triggeredByEvent = triggeredByEvent;
		fillAttributes();
	}

	/**
	 * Create a subprocess with parent swimlane and with triggeredByEvent param
	 * @param bpmndiagram
	 * @param label
	 * @param looped
	 * @param adhoc
	 * @param compensation
	 * @param multiinstance
	 * @param collapsed
	 * @param triggeredByEvent
	 * @param parentSwimlane
	 */
	public SubProcess(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, boolean looped, boolean adhoc, boolean compensation, boolean multiinstance,
			boolean collapsed, boolean triggeredByEvent, Swimlane parentSwimlane) {
		super(bpmndiagram, label, looped, adhoc, compensation, multiinstance, collapsed, parentSwimlane);
		children = new HashSet<ContainableDirectedGraphElement>();
		this.triggeredByEvent = triggeredByEvent;
		fillAttributes();
	}

	/**
	 * 
	 */
	private void fillAttributes() {
		if(triggeredByEvent) {
			getAttributeMap().put(AttributeMap.DASHPATTERN, new float[] { (float)3.0, (float)10.0 });
		}
		getAttributeMap().put(AttributeMap.SIZE, null);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.FILLCOLOR, new Color(.95F, .95F, .95F, .95F));
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.TOP);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}

	public Set<ContainableDirectedGraphElement> getChildren() {
		return children;
	}

	public void addChild(ContainableDirectedGraphElement child) {
		children.add(child);
	}

	public Dimension getCollapsedSize() {
		return new Dimension(stdWidth, stdHeight);
	}
	
	public boolean getTriggeredByEvent() {
		return triggeredByEvent;
	}
	
	@Override
	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		super.decorate(g2d, x, y, width, height);
	}
}
