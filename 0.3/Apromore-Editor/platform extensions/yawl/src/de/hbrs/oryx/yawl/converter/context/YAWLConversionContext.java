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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YFlow;

import de.hbrs.oryx.yawl.converter.layout.FlowLayout;
import de.hbrs.oryx.yawl.converter.layout.NetLayout;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout;

/**
 * YAWLConversionContext is the "glue" for the various handlers during an
 * conversion YAWL -> Oryx. Information about the conversion and its results are
 * stored here.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class YAWLConversionContext extends ConversionContext {

	/**
	 * Oryx Diagram with properties of the Specification. This Diagram will
	 * contain the RootNet.
	 */
	private BasicDiagram specificationDiagram;

	/**
	 * Contains the Oryx shapes of all YAWL nets
	 */
	private final HashMap<String, BasicShape> netMap;

	/**
	 * Layout information for each YAWL (sub)-net. (Net-ID -> Layout)
	 */
	private final Map<String, NetLayout> layoutMap;

	/**
	 * Map of all created Shapes during the conversion (Element-ID -> Shape)
	 */
	private final Map<String, BasicShape> shapeMap;

	/**
	 * Used to retrieve StencilSet to create new Shapes
	 */
	private final String rootDir;

	/**
	 * Identifier of the Root Net
	 */
	private String rootNetId;
	
	/**
	 * @param rootDir used to retrieve StencilSet to create new Shapes
	 */
	public YAWLConversionContext(String rootDir) {
		super();
		this.rootDir = rootDir;
		this.layoutMap = new HashMap<String, NetLayout>();
		this.shapeMap = new HashMap<String, BasicShape>();
		this.netMap = new HashMap<String, BasicShape>();
	}

	public void setSpecificationDiagram(BasicDiagram specificationDiagram) {
		this.specificationDiagram = specificationDiagram;
	}

	public BasicDiagram getSpecificationDiagram() {
		return specificationDiagram;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void addNet(String id, BasicShape shape) {
		netMap.put(id, shape);
	}

	public BasicShape getNet(String netId) {
		return netMap.get(netId);
	}

	public Set<Entry<String, BasicShape>> getNetSet() {
		return netMap.entrySet();
	}

	public void setRootNetId(String rootNetId) {
		this.rootNetId = rootNetId;
	}

	public String getRootNetId() {
		return rootNetId;
	}

	public BasicShape getRootNet() {
		return netMap.get(getRootNetId());
	}

	public NetLayout getNetLayout(String id) {
		return layoutMap.get(id);
	}

	public NetElementLayout getVertexLayout(String netId, String id) {
		return layoutMap.get(netId).getVertexLayout(id);
	}

	public void putNetLayout(String yawlId, NetLayout netLayout) {
		layoutMap.put(yawlId, netLayout);
	}

	public BasicShape getShape(String id) {
		return shapeMap.get(id);
	}

	public void putShape(String netId, String shapeId, BasicShape shape) {
		getNet(netId).addChildShape(shape);
		shapeMap.put(shapeId, shape);
	}

	public FlowLayout getFlowLayout(String netId, String priorElementID,
			String nextElementID) {
		return layoutMap.get(netId).getFlowLayout(
				priorElementID + "|" + nextElementID);
	}

	public void addPostsetFlows(String netId, Set<YFlow> postsetFlows) {
		layoutMap.get(netId).addFlows(postsetFlows);
	}
	

}