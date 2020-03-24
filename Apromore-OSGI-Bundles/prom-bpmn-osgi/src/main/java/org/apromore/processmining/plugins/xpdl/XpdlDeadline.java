package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.apromore.processmining.plugins.xpdl.text.XpdlText;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Deadline"> <xsd:annotation> <xsd:documentation>
 *         BPMN provides a timer event to support this type of functionality and
 *         it is the preferred method for doing this. </xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:element
 *         name="DeadlineDuration" type="xpdl:ExpressionType" minOccurs="0"/>
 *         <xsd:element name="ExceptionName" minOccurs="0"> <xsd:annotation>
 *         <xsd:documentation> This name should match that specified in
 *         Transition/Condition/Expression </xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Execution"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="ASYNCHR"/>
 *         <xsd:enumeration value="SYNCHR"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlDeadline extends XpdlElement {

	/*
	 * Attributes
	 */
	private String execution;

	/*
	 * Elements
	 */
	private XpdlExpressionType deadlineDuration;
	private XpdlText exceptionName;

	public XpdlDeadline(String tag) {
		super(tag);

		execution = null;

		deadlineDuration = null;
		exceptionName = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("DeadlineDuration")) {
			deadlineDuration = new XpdlExpressionType("DeadlineDuration");
			deadlineDuration.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExceptionName")) {
			exceptionName = new XpdlText("ExceptionName");
			exceptionName.importElement(xpp, xpdl);
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
		if (deadlineDuration != null) {
			s += deadlineDuration.exportElement();
		}
		if (exceptionName != null) {
			s += exceptionName.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Execution");
		if (value != null) {
			execution = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (execution != null) {
			s += exportAttribute("Execution", execution);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Execution", execution, Arrays.asList("ASYNCHR", "SYNCHR"), false);
	}

	public String getExecution() {
		return execution;
	}

	public void setExecution(String execution) {
		this.execution = execution;
	}

	public XpdlExpressionType getDeadlineDuration() {
		return deadlineDuration;
	}

	public void setDeadlineDuration(XpdlExpressionType deadlineDuration) {
		this.deadlineDuration = deadlineDuration;
	}

	public XpdlText getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(XpdlText exceptionName) {
		this.exceptionName = exceptionName;
	}
}
