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
package de.hbrs.oryx.yawl.converter.layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.oryxeditor.server.diagram.Bounds;
import org.yawlfoundation.yawl.elements.YFlow;

/**
 * Layout information of a YAWL Net/Subnet and its elements
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class NetLayout {

	/**
	 * Map of VertexID <-> LayoutInformation for Vertex (e.g. Task or Condition)
	 */
	private Map<String, NetElementLayout> vertexLayoutMap;

	/**
	 * Map of "Prior Vertex ID"|"Next Vertex ID" <-> LayoutInformation for Flow
	 */
	private Map<String, FlowLayout> flowLayoutMap;

	/**
	 * Bounds of the Net itself
	 */
	private Bounds bounds;

	/**
	 * Set of all flows (edges) between the YAWL elements of this net. TODO:
	 * This is not a layout information, so maybe store it elsewhere!
	 */
	private final Set<YFlow> flowSet;

	public NetLayout(Bounds bounds) {
		super();
		this.bounds = bounds;
		this.vertexLayoutMap = new HashMap<String, NetElementLayout>();
		this.flowLayoutMap = new HashMap<String, FlowLayout>();
		this.flowSet = new HashSet<YFlow>();
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void putVertexLayout(String vertexID, NetElementLayout value) {
		vertexLayoutMap.put(vertexID, value);
	}

	public NetElementLayout getVertexLayout(String vertexID) {
		return vertexLayoutMap.get(vertexID);
	}

	public void putFlowLayout(String flowID, FlowLayout value) {
		flowLayoutMap.put(flowID, value);
	}

	public FlowLayout getFlowLayout(String flowID) {
		return flowLayoutMap.get(flowID);
	}

	public Set<YFlow> getFlowSet() {
		return flowSet;
	}

	public void addFlows(Set<YFlow> flows) {
		flowSet.addAll(flows);

	}

}
