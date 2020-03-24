package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="SimulationInformation"> <xsd:complexType>
 *         <xsd:sequence> <xsd:choice> <xsd:element ref="xpdl:Cost"/>
 *         <xsd:element ref="xpdl:CostStructure"/> </xsd:choice> <xsd:element
 *         ref="xpdl:TimeEstimation"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="Instantiation"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration value="ONCE"/>
 *         <xsd:enumeration value="MULTIPLE"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlSimulationInformation extends XpdlElement {

	/*
	 * Attributes
	 */
	private String instantiation;

	/*
	 * Elements
	 */
	private XpdlCost cost;
	private XpdlTimeEstimation timeEstimation;

	public XpdlSimulationInformation(String tag) {
		super(tag);

		cost = null;
		timeEstimation = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Cost")) {
			cost = new XpdlCost("Cost");
			cost.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TimeEstimation")) {
			timeEstimation = new XpdlTimeEstimation("TimeEstimation");
			timeEstimation.importElement(xpp, xpdl);
			return true;
		}
		/*
		 * Unknown tag
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if (cost != null) {
			s += cost.exportElement();
		}
		if (timeEstimation != null) {
			s += timeEstimation.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Instantiation");
		if (value != null) {
			instantiation = value;
		}
		value = xpp.getAttributeValue(null, "TextAnnotation");
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (instantiation != null) {
			s += exportAttribute("Instantiation", instantiation);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Instantiation", instantiation, Arrays.asList("ONCE", "MULTIPLE"), false);
	}

	public String getInstantiation() {
		return instantiation;
	}

	public void setInstantiation(String instantiation) {
		this.instantiation = instantiation;
	}

	public XpdlCost getCost() {
		return cost;
	}

	public void setCost(XpdlCost cost) {
		this.cost = cost;
	}

	public XpdlTimeEstimation getTimeEstimation() {
		return timeEstimation;
	}

	public void setTimeEstimation(XpdlTimeEstimation timeEstimation) {
		this.timeEstimation = timeEstimation;
	}
}
