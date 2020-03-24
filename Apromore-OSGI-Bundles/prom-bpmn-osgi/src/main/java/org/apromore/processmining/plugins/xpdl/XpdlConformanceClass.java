package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ConformanceClass"> <xsd:complexType>
 *         <xsd:sequence> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="GraphConformance" use="optional" default="NON_BLOCKED">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="FULL_BLOCKED"/> <xsd:enumeration
 *         value="LOOP_BLOCKED"/> <xsd:enumeration value="NON_BLOCKED"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="BPMNModelPortabilityConformance" use="optional" default="NONE">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="NONE"/> <xsd:enumeration value="SIMPLE"/>
 *         <xsd:enumeration value="STANDARD"/> <xsd:enumeration
 *         value="COMPLETE"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlConformanceClass extends XpdlElement {

	/*
	 * Attributes
	 */
	private String graphConformance;
	private String bpmnModelPortabilityConformance;

	public XpdlConformanceClass(String tag) {
		super(tag);

		graphConformance = null;
		bpmnModelPortabilityConformance = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "GraphConformance");
		if (value != null) {
			graphConformance = value;
		}
		value = xpp.getAttributeValue(null, "BPMNModelPortabilityConformance");
		if (value != null) {
			bpmnModelPortabilityConformance = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (graphConformance != null) {
			s += exportAttribute("GraphConformance", graphConformance);
		}
		if (bpmnModelPortabilityConformance != null) {
			s += exportAttribute("BPMNModelPortabilityConformance", bpmnModelPortabilityConformance);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		
		String[] graphConformances = new String[] {
				"FULL_BLOCKED", "LOOP_BLOCKED", "NON_BLOCKED",
				"FULL-BLOCKED", "LOOP-BLOCKED", "NON-BLOCKED"
		};
		
		String[] portableConformances = new String[] {
				"NONE", "SIMPLE", "STANDARD", "COMPLETE"
		};
		
		checkRestriction(xpdl, "GraphConformance", graphConformance, Arrays.asList(graphConformances), false);
		checkRestriction(xpdl, "BPMNModelPortabilityConformance", bpmnModelPortabilityConformance, Arrays.asList(portableConformances), false);
	}

	public String getGraphConformance() {
		return graphConformance;
	}

	public void setGraphConformance(String graphConformance) {
		this.graphConformance = graphConformance;
	}

	public String getBpmnModelPortabilityConformance() {
		return bpmnModelPortabilityConformance;
	}

	public void setBpmnModelPortabilityConformance(String bpmnModelPortabilityConformance) {
		this.bpmnModelPortabilityConformance = bpmnModelPortabilityConformance;
	}
}
