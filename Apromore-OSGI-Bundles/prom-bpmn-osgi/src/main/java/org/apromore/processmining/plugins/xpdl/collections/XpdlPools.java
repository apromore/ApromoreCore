package org.apromore.processmining.plugins.xpdl.collections;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.plugins.xpdl.idname.XpdlPool;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Pools"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:Pool"
 *         minOccurs="0" maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlPools extends XpdlCollections<XpdlPool> {

	public XpdlPools(String tag) {
		super(tag);
	}

	public XpdlPool create() {
		return new XpdlPool("Pool");
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		//we do not convert Pools. We convert the swim lanes even if they are in other pools.
		if (!list.isEmpty()) {
			for (XpdlPool pool : list) {
				if(pool.getBoundaryVisible().equals("true")) {
					pool.convertToBpmn(bpmn, id2node);
				} else {
					XpdlLanes lanes = pool.getLanes();
					if (lanes != null) {
						lanes.convertToBpmn(bpmn, id2node);
					}
				}
			}
		}
	}
}
