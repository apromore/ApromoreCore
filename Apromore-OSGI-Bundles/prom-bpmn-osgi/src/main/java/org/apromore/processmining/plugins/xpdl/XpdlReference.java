package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Reference"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in BPMN 2.0</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="ActivityId" type="xpdl:IdRef" use="required"> <xsd:annotation>
 *         <xsd:documentation> BPMN: Reference to a BPMN task or subprocess
 *         definition elsewhere; should not be used for gateway or event.
 *         Pointer to Activity/@Id in XPDL. </xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlReference extends XpdlElement {

	/*
	 * Attributes
	 */
	private String activityId;

	public XpdlReference(String tag) {
		super(tag);

		activityId = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ActivityId");
		if (value != null) {
			activityId = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (activityId != null) {
			s += exportAttribute("ActivityId", activityId);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "ActivityId", activityId);
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
}
