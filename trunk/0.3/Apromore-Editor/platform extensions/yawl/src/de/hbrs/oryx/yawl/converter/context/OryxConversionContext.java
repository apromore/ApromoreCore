/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * See: http://www.gnu.org/licenses/lgpl-3.0
 * 
 */
package de.hbrs.oryx.yawl.converter.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom.Element;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.elements.YTask;

/**
 * Context of a Conversion Oryx -> YAWL
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxConversionContext extends ConversionContext {

	private YSpecification specification;

	/**
	 * Contains all (Sub-)Nets
	 */
	private Map<BasicShape, YNet> netMap;

	/**
	 * Contains all Shapes an their connected Edges
	 */
	private Map<BasicShape, Set<BasicEdge>> flowMap;

	private Map<String, BasicDiagram> subnetDiagramMap;

	private Map<YNet, Map<YTask, List<String>>> cancellationSetMap;

	private List<Element> netLayoutList;

	private Element specLayoutElement;

	/**
	 * Create a new OryxConversionContext used to store information about the
	 * conversion.
	 * 
	 * @param oryxBackendUrl
	 */
	public OryxConversionContext() {
		super();
		this.netMap = new HashMap<BasicShape, YNet>();
		this.flowMap = new HashMap<BasicShape, Set<BasicEdge>>();
		this.subnetDiagramMap = new HashMap<String, BasicDiagram>();
		this.cancellationSetMap = new HashMap<YNet, Map<YTask, List<String>>>();
		this.netLayoutList = new ArrayList<Element>();
	}

	public void setSpecification(YSpecification yawlSpec) {
		this.specification = yawlSpec;
	}

	public YSpecification getSpecification() {
		return specification;
	}

	public YNet addNet(BasicShape shape, YNet net) {
		return netMap.put(shape, net);
	}

	public YNet getNet(BasicShape shape) {
		return netMap.get(shape);
	}

	public void addFlow(BasicShape net, BasicEdge flow) {
		if (flowMap.get(net) != null) {
			flowMap.get(net).add(flow);
		} else {
			Set<BasicEdge> flowSet = new HashSet<BasicEdge>();
			flowSet.add(flow);
			flowMap.put(net, flowSet);
		}
	}

	public Set<BasicEdge> getFlowSet(BasicShape shape) {
		if (flowMap.get(shape) != null) {
			return flowMap.get(shape);
		} else {
			// Create empty one
			Set<BasicEdge> flowSet = new HashSet<BasicEdge>();
			flowMap.put(shape, flowSet);
			return Collections.unmodifiableSet(flowSet);
		}
	}

	/**
	 * Adds a Oryx Diagram of a YAWL Subnet, that will later be used to compile
	 * subnets for each composite task.
	 * 
	 * @param id
	 *            of the YAWL subnet
	 * @param subnetDiagram
	 *            of a YAWL subnet used in the specification to be converted
	 */
	public void addSubnetDiagram(String id, BasicDiagram subnetDiagram) {
		subnetDiagramMap.put(id, subnetDiagram);
	}

	public BasicDiagram getSubnetDiagram(String id) {
		return subnetDiagramMap.get(id);
	}

	/**
	 * Add the Element with ID to the Elements cancelled by task.
	 * 
	 * @param task
	 *            the Task that cancels the Element
	 * @param id
	 *            of the Element to be cancelled
	 */
	public void addToCancellationSet(YTask task, String id) {
		if (!cancellationSetMap.containsKey(task.getNet())) {
			cancellationSetMap.put(task.getNet(), new HashMap<YTask, List<String>>());
		}

		Map<YTask, List<String>> cancellationSetForNet = cancellationSetMap.get(task.getNet());

		if (!cancellationSetForNet.containsKey(task)) {
			cancellationSetForNet.put(task, new ArrayList<String>());
		}

		cancellationSetForNet.get(task).add(id);
	}

	/**
	 * Get a unmodifiable view on the Cancellation Set
	 * 
	 * @param net
	 * 
	 * @return
	 */
	public Set<Entry<YTask, List<String>>> getCancellationSets(YNet net) {
		if (cancellationSetMap.get(net) != null) {
			return Collections.unmodifiableSet(cancellationSetMap.get(net).entrySet());
		} else {
			return Collections.unmodifiableSet(new HashSet<Entry<YTask, List<String>>>());
		}
	}

	public List<String> getCancellationSet(YNet net, YTask task) {
		if (cancellationSetMap.get(net) != null) {
			return cancellationSetMap.get(net).get(task);
		} else {
			return new ArrayList<String>();
		}
	}

	public void addNetLayout(Element netLayoutElement) {
		netLayoutList.add(netLayoutElement);
	}

	public List<Element> getNetLayoutList() {
		return Collections.unmodifiableList(netLayoutList);
	}

	public void setSpecificationLayout(Element specLayoutElement) {
		this.specLayoutElement = specLayoutElement;
	}

	public Element getSpecificationLayoutElement() {
		return specLayoutElement;
	}

}
