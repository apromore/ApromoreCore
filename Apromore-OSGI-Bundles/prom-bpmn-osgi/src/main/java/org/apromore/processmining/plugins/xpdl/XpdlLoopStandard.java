package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="LoopStandard"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="LoopCondition"
 *         type="xpdl:ExpressionType" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="LoopCondition" type="xsd:string" use="optional">
 *         <xsd:annotation> <xsd:documentation> Deprecated in XPDL2.2 use the
 *         LoopCondition element. </xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="LoopCounter"
 *         type="xsd:integer"> <xsd:annotation> <xsd:documentation> This is
 *         updated at run time to count the number of executions of the loop and
 *         is available as a property to be used in expressions. Does this
 *         belong in the XPDL? </xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="LoopMaximum" type="xsd:integer"
 *         use="optional"/> <xsd:attribute name="TestTime" use="optional"
 *         default="After"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="Before"/>
 *         <xsd:enumeration value="After"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlLoopStandard extends XpdlElement {

	/*
	 * Attributes
	 */
	private String loopCondition;
	private String loopCounter;
	private String loopMaximum;
	private String testTime;

	/*
	 * Elements
	 */
	private XpdlExpressionType loopConditionElt;

	public XpdlLoopStandard(String tag) {
		super(tag);

		loopCondition = null;
		loopCounter = null;
		loopMaximum = null;
		testTime = null;

		loopConditionElt = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("LoopCondition")) {
			loopConditionElt = new XpdlExpressionType("LoopCondition");
			loopConditionElt.importElement(xpp, xpdl);
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
		if (loopConditionElt != null) {
			s += loopConditionElt.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "LoopCondition");
		if (value != null) {
			loopCondition = value;
		}
		value = xpp.getAttributeValue(null, "LoopCounter");
		if (value != null) {
			loopCounter = value;
		}
		value = xpp.getAttributeValue(null, "LoopMaximum");
		if (value != null) {
			loopMaximum = value;
		}
		value = xpp.getAttributeValue(null, "TestTime");
		if (value != null) {
			testTime = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (loopCondition != null) {
			s += exportAttribute("LoopCondition", loopCondition);
		}
		if (loopCounter != null) {
			s += exportAttribute("LoopCounter", loopCounter);
		}
		if (loopMaximum != null) {
			s += exportAttribute("LoopMaximum", loopMaximum);
		}
		if (testTime != null) {
			s += exportAttribute("TestTime", testTime);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkInteger(xpdl, "LoopCounter", loopCounter, false);
		checkInteger(xpdl, "LoopMaximum", loopMaximum, false);
		checkRestriction(xpdl, "TestTime", testTime, Arrays.asList("Before", "After"), true);
	}

	public String getLoopCondition() {
		return loopCondition;
	}

	public void setLoopCondition(String loopCondition) {
		this.loopCondition = loopCondition;
	}

	public String getLoopCounter() {
		return loopCounter;
	}

	public void setLoopCounter(String loopCounter) {
		this.loopCounter = loopCounter;
	}

	public String getLoopMaximum() {
		return loopMaximum;
	}

	public void setLoopMaximum(String loopMaximum) {
		this.loopMaximum = loopMaximum;
	}

	public String getTestTime() {
		return testTime;
	}

	public void setTestTime(String testTime) {
		this.testTime = testTime;
	}

	public XpdlExpressionType getLoopConditionElt() {
		return loopConditionElt;
	}

	public void setLoopConditionElt(XpdlExpressionType loopConditionElt) {
		this.loopConditionElt = loopConditionElt;
	}
}
