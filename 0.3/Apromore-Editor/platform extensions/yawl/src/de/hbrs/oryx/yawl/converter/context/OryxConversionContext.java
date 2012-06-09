/**
 * Copyright (c) 2011-2012 Felix Mannhardt
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
 * 
 * See: http://www.opensource.org/licenses/mit-license.php
 * 
 */
package de.hbrs.oryx.yawl.converter.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YSpecification;

/**
 * Context of a Conversion Oryx -> YAWL
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxConversionContext extends ConversionContext {

	private final String oryxBackendUrl;

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

	/**
	 * Create a new OryxConversionContext used to store information about the
	 * conversion.
	 * 
	 * @param oryxBackendUrl
	 */
	public OryxConversionContext(String oryxBackendUrl) {
		super();
		this.oryxBackendUrl = oryxBackendUrl;
		this.netMap = new HashMap<BasicShape, YNet>();
		this.flowMap = new HashMap<BasicShape, Set<BasicEdge>>();
		this.subnetDiagramMap = new HashMap<String, BasicDiagram>();
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
			return flowSet;
		}
	}

	public String getOryxBackendUrl() {
		return oryxBackendUrl;
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

}
