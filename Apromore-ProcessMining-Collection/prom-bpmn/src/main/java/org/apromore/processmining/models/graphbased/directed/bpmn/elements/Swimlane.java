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

	@Override
	public Swimlane copy() {
		Swimlane copy = new Swimlane(getGraph(), getLabel(), getSwimlaneType());
		copy.setId(getId().toString());
		return copy;
	}
}


