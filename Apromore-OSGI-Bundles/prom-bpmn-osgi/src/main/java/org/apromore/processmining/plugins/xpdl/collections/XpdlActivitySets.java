package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlActivitySet;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ActivitySets"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:ActivitySet" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlActivitySets extends XpdlCollections<XpdlActivitySet> {

	public XpdlActivitySets(String tag) {
		super(tag);
	}

	public XpdlActivitySet create() {
		return new XpdlActivitySet("ActivitySet");
	}

	public XpdlActivitySet get(String id) {
		for (XpdlActivitySet activitySet : list) {
			if (activitySet.hasId(id)) {
				return activitySet;
			}
		}
		return null;
	}
}
