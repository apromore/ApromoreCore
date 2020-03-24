package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerTimer"> <xsd:annotation>
 *         <xsd:documentation> BPMN: If the Trigger Type is Timer then this must
 *         be present </xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:choice> <xsd:element name="TimeDate"
 *         type="xpdl:ExpressionType"/> <xsd:element name="TimeCycle"
 *         type="xpdl:ExpressionType"/> </xsd:choice> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="TimeDate" type="xsd:string" use="optional"> <xsd:annotation>
 *         <xsd:documentation>Deprecated</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="TimeCycle" type="xsd:string"
 *         use="optional"> <xsd:annotation>
 *         <xsd:documentation>Deprecated</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTriggerTimer extends XpdlElement {

	/*
	 * Attributes
	 */
	private String timeDateAttribute;
	private String timeCycleAttribute;

	/*
	 * Elements
	 */
	private XpdlExpressionType timeDateElement;
	private XpdlExpressionType timeCycleElement;

	public XpdlTriggerTimer(String tag) {
		super(tag);

		timeDateAttribute = null;
		timeCycleAttribute = null;

		timeDateElement = null;
		timeCycleElement = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("TimeDate")) {
			timeDateElement = new XpdlExpressionType("TimeDate");
			timeDateElement.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TimeCycle")) {
			timeCycleElement = new XpdlExpressionType("TimeCycle");
			timeCycleElement.importElement(xpp, xpdl);
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
		if (timeDateElement != null) {
			s += timeDateElement.exportElement();
		}
		if (timeCycleElement != null) {
			s += timeCycleElement.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "TimeDate");
		if (value != null) {
			timeDateAttribute = value;
		}
		value = xpp.getAttributeValue(null, "TimeCycle");
		if (value != null) {
			timeCycleAttribute = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (timeDateAttribute != null) {
			s += exportAttribute("TimeDate", timeDateAttribute);
		}
		if (timeCycleAttribute != null) {
			s += exportAttribute("TimeCycle", timeCycleAttribute);
		}
		return s;
	}

	public String getTimeDateAttribute() {
		return timeDateAttribute;
	}

	public void setTimeDateAttribute(String timeDateAttribute) {
		this.timeDateAttribute = timeDateAttribute;
	}

	public String getTimeCycleAttribute() {
		return timeCycleAttribute;
	}

	public void setTimeCycleAttribute(String timeCycleAttribute) {
		this.timeCycleAttribute = timeCycleAttribute;
	}

	public XpdlExpressionType getTimeDateElement() {
		return timeDateElement;
	}

	public void setTimeDateElement(XpdlExpressionType timeDateElement) {
		this.timeDateElement = timeDateElement;
	}

	public XpdlExpressionType getTimeCycleElement() {
		return timeCycleElement;
	}

	public void setTimeCycleElement(XpdlExpressionType timeCycleElement) {
		this.timeCycleElement = timeCycleElement;
	}
}
