package org.apromore.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.shapes.Decorated;
import org.apromore.processmining.models.shapes.Rectangle;

public class Swimlane extends BPMNNode implements Decorated, ContainingDirectedGraphNode {
	//com.jgraph.layout.hierarchical.JGraphHierarchicalLayout, interHierarchySpacing is decreased from 60 to 5. Thus the space between swimlanes decreawsed.
	//org.processmining.models.jgraph.renderers.ProMGroupShapeRenderer, Rectangle handle dimensions changed from 0,0,20,20 to 0,0,10,10.
	protected final static int COLLAPSED_WIDTH = 80;
	protected final static int COLLAPSED_HEIGHT = 40;

	protected final static int EXPANDED_WIDTH = 1000;
	protected final static int EXPANDED_HEIGHT = 250;

	public static final int PADDINGFROMBOXTOTEXT = 5;
	public static final int TEXTWIDTH = 20;
	private final Set<ContainableDirectedGraphElement> children;
	private SwimlaneType type;
	
	// A reference to a resource
	private String partitionElement;
	
	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label) {
		super(bpmndiagram);
		children = new HashSet<ContainableDirectedGraphElement>();
		initAttributeMap(label);
	}
	
	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, Swimlane parentSwimlane) {
		super(bpmndiagram, parentSwimlane);
		children = new HashSet<ContainableDirectedGraphElement>();
		initAttributeMap(label);
	}
	
	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, SubProcess parentSubProcess) {
		super(bpmndiagram, parentSubProcess);
		children = new HashSet<ContainableDirectedGraphElement>();
		initAttributeMap(label);
	}

	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, Swimlane parentSwimlane, SwimlaneType type) {
		this(bpmndiagram,label, parentSwimlane);
		this.type = type;
	}
	
	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, SubProcess parentSubProcess, SwimlaneType type) {
		this(bpmndiagram,label, parentSubProcess);
		this.type = type;
	}
	
	public Swimlane(AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,
			String label, SwimlaneType type) {
		this(bpmndiagram,label);
		this.type = type;
	}

	public Set<ContainableDirectedGraphElement> getChildren() {
		return children;
	}

	public void addChild(ContainableDirectedGraphElement child) {
		children.add(child);
	}

	public Dimension getCollapsedSize() {
		return new Dimension(COLLAPSED_WIDTH, COLLAPSED_HEIGHT);
	}
	
	public SwimlaneType getSwimlaneType() {
		return type;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
//		final int labelX = (int) Math.round(x);
//		final int labelY = (int) Math.round(y);
//		final int labelW = TEXTWIDTH;
//		final int labelH = (int) Math.round(height);
//
//		JLabel label = new JLabel(this.getLabel());
//		label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
//		label.setToolTipText(getLabel());
//		label.validate();
//		label.setForeground(Color.BLACK);
//		label.setSize(new Dimension(labelW, labelH));
//		label.setPreferredSize(new Dimension(labelW, labelH));
//		label.setBorder(new LineBorder(Color.BLACK, 2));
//		label.setVerticalAlignment(SwingConstants.CENTER);
//		label.setUI(new VerticalLabelUI(false));
//		label.paint(g2d.create(labelX, labelY, labelW, labelH));

	}
	
	public void setPartitionElement(String partitionElement) {
		this.partitionElement = partitionElement;
	}
	
	public String getPartitionElement() {
		return partitionElement;
	}
	
	private void initAttributeMap (String label) {
		getAttributeMap().put(AttributeMap.LABEL, label);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SHAPE, new Rectangle(false));
		getAttributeMap().put(AttributeMap.SQUAREBB, false);
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.TOP);
		getAttributeMap().put(AttributeMap.LABELHORIZONTALALIGNMENT, SwingConstants.CENTER);
		getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}
}


