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
	private final HashMap<String, BasicDiagram> netMap;

	/**
	 * Layout information for each YAWL (sub)-net. (Net-ID -> Layout)
	 */
	private final Map<String, NetLayout> layoutMap;

	/**
	 * Map of all created Shapes during the conversion (Element-ID -> Shape)
	 */
	private final Map<String, BasicShape> shapeMap;

	/**
	 * Identifier of the Root Net
	 */
	private String rootNetId;

	/**
	 * @param rootDir
	 *            used to retrieve StencilSet to create new Shapes
	 */
	public YAWLConversionContext() {
		super();
		this.layoutMap = new HashMap<String, NetLayout>();
		this.shapeMap = new HashMap<String, BasicShape>();
		this.netMap = new HashMap<String, BasicDiagram>();
	}

	public void setSpecificationDiagram(BasicDiagram specificationDiagram) {
		this.specificationDiagram = specificationDiagram;
	}

	public BasicDiagram getSpecificationDiagram() {
		return specificationDiagram;
	}

	public void addNet(String id, BasicDiagram shape) {
		netMap.put(id, shape);
	}

	public BasicDiagram getNet(String netId) {
		return netMap.get(netId);
	}

	public Set<Entry<String, BasicDiagram>> getNetSet() {
		return netMap.entrySet();
	}

	public void setRootNetId(String rootNetId) {
		this.rootNetId = rootNetId;
	}

	public String getRootNetId() {
		return rootNetId;
	}

	public BasicDiagram getRootNet() {
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

	public FlowLayout getFlowLayout(String netId, String priorElementID, String nextElementID) {
		return layoutMap.get(netId).getFlowLayout(priorElementID + "|" + nextElementID);
	}

	public void addPostsetFlows(String netId, Set<YFlow> postsetFlows) {
		layoutMap.get(netId).addFlows(postsetFlows);
	}

}