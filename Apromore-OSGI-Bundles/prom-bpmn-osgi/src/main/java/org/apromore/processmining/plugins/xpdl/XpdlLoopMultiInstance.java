package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="LoopMultiInstance"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="MI_Condition"
 *         type="xpdl:ExpressionType" minOccurs="0"/> <xsd:element
 *         name="ComplexMI_FlowCondition" type="xpdl:ExpressionType"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="MI_Condition" type="xsd:string" use="optional">
 *         <xsd:annotation> <xsd:documentation>Deprecated in XPDL2.2. Use
 *         MI_Condition element.</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="LoopCounter"
 *         type="xsd:integer"> <xsd:annotation> <xsd:documentation> This is
 *         updated at run time to count the number of executions of the loop and
 *         is available as a property to be used in expressions. Does this
 *         belong in the XPDL? </xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="MI_Ordering" use="optional"
 *         default="Parallel"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="Sequential"/>
 *         <xsd:enumeration value="Parallel"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="MI_FlowCondition" use="optional" default="All">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="None"/> <xsd:enumeration value="One"/>
 *         <xsd:enumeration value="All"/> <xsd:enumeration value="Complex"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="ComplexMI_FlowCondition" type="xsd:string" use="optional">
 *         <xsd:annotation> <xsd:documentation>Deprecated in XPDL2.1. Use
 *         ComplexMI_FlowCondition</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlLoopMultiInstance extends XpdlElement {

	/*
	 * Attributes
	 */
	private String condition;
	private String loopCounter;
	private String ordering;
	private String flowCondition;
	private String complexFlowCondition;

	/*
	 * Elements
	 */
	private XpdlExpressionType miCondition;
	private XpdlExpressionType complexMIFlowCondition;

	public XpdlLoopMultiInstance(String tag) {
		super(tag);

		condition = null;
		loopCounter = null;
		ordering = null;
		flowCondition = null;
		complexFlowCondition = null;

		miCondition = null;
		complexMIFlowCondition = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("MI_Condition")) {
			miCondition = new XpdlExpressionType("MI_Condition");
			miCondition.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ComplexMI_FlowCondition")) {
			complexMIFlowCondition = new XpdlExpressionType("ComplexMI_FlowCondition");
			complexMIFlowCondition.importElement(xpp, xpdl);
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
		if (miCondition != null) {
			s += miCondition.exportElement();
		}
		if (complexMIFlowCondition != null) {
			s += complexMIFlowCondition.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "MI_Condition");
		if (value != null) {
			condition = value;
		}
		value = xpp.getAttributeValue(null, "LoopCounter");
		if (value != null) {
			loopCounter = value;
		}
		value = xpp.getAttributeValue(null, "MI_Ordering");
		if (value != null) {
			ordering = value;
		}
		value = xpp.getAttributeValue(null, "MI_FlowCondition");
		if (value != null) {
			flowCondition = value;
		}
		value = xpp.getAttributeValue(null, "ComplexMI_FlowCondition");
		if (value != null) {
			complexFlowCondition = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (condition != null) {
			s += exportAttribute("MI_Condition", condition);
		}
		if (loopCounter != null) {
			s += exportAttribute("LoopCounter", loopCounter);
		}
		if (ordering != null) {
			s += exportAttribute("MI_Ordering", ordering);
		}
		if (flowCondition != null) {
			s += exportAttribute("MI_FlowCondition", flowCondition);
		}
		if (complexFlowCondition != null) {
			s += exportAttribute("ComplexMI_FlowCondition", complexFlowCondition);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkInteger(xpdl, "LoopCounter", loopCounter, false);
		checkRestriction(xpdl, "MI_Ordering", ordering, Arrays.asList("Sequential", "Parallel"), true);
		checkRestriction(xpdl, "MI_FlowCondition", flowCondition, Arrays.asList("None", "One", "All", "Complex"), true);
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getLoopCounter() {
		return loopCounter;
	}

	public void setLoopCounter(String loopCounter) {
		this.loopCounter = loopCounter;
	}

	public String getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	public String getFlowCondition() {
		return flowCondition;
	}

	public void setFlowCondition(String flowCondition) {
		this.flowCondition = flowCondition;
	}

	public String getComplexFlowCondition() {
		return complexFlowCondition;
	}

	public void setComplexFlowCondition(String complexFlowCondition) {
		this.complexFlowCondition = complexFlowCondition;
	}

	public XpdlExpressionType getMiCondition() {
		return miCondition;
	}

	public void setMiCondition(XpdlExpressionType miCondition) {
		this.miCondition = miCondition;
	}

	public XpdlExpressionType getComplexMIFlowCondition() {
		return complexMIFlowCondition;
	}

	public void setComplexMIFlowCondition(XpdlExpressionType complexMIFlowCondition) {
		this.complexMIFlowCondition = complexMIFlowCondition;
	}
}
