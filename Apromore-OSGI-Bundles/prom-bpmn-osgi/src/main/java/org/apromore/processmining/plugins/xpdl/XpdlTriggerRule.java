package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerRule"> <xsd:annotation> <xsd:documentation>
 *         BPMN: if the TriggerType is Rule then this must be present.
 *         Deprecated in BPMN1.1 </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="RuleName" type="xsd:string"
 *         use="required"> <xsd:annotation> <xsd:documentation>This is the
 *         nameof a Rule element.</xsd:documentation>
 *         <xsd:documentation>Deprecated in BPMN1.1</xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTriggerRule extends XpdlElement {

	/*
	 * Attributes
	 */
	private String ruleName;

	public XpdlTriggerRule(String tag) {
		super(tag);

		ruleName = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "RuleName");
		if (value != null) {
			ruleName = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (ruleName != null) {
			s += exportAttribute("RuleName", ruleName);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "RuleName", ruleName);
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
}
