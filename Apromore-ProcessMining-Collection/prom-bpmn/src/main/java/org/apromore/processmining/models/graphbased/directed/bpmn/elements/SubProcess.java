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

	@Override
	public SubProcess copy() {
		SubProcess copy = new SubProcess(getGraph(), getLabel(), isBLooped(),
			isBAdhoc(), isBCompensation(), isBMultiinstance(), isBCollapsed());
		copy.setId(getId().toString());
		return copy;
	}
}
