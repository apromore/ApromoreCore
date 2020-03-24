package org.apromore.processmining.plugins.xpdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerResultSignal"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="Properties"
 *         type="xpdl:ExpressionType" minOccurs="0" maxOccurs="unbounded"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"> <xsd:annotation>
 *         <xsd:documentation>Text description of the signal</xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:attribute name="CatchThrow"
 *         use="optional" default="CATCH"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="CATCH"/> <xsd:enumeration
 *         value="THROW"/> </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTriggerResultSignal extends XpdlElement {

	/*
	 * Attributes
	 */
	private String name;
	private String catchThrow;

	/*
	 * Elements
	 */
	private final List<XpdlExpressionType> propertiesList;

	public XpdlTriggerResultSignal(String tag) {
		super(tag);

		name = null;
		catchThrow = null;

		propertiesList = new ArrayList<XpdlExpressionType>();
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Properties")) {
			XpdlExpressionType properties = new XpdlExpressionType("Properties");
			properties.importElement(xpp, xpdl);
			propertiesList.add(properties);
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
		for (XpdlExpressionType properties : propertiesList) {
			s += properties.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Name");
		if (value != null) {
			name = value;
		}
		value = xpp.getAttributeValue(null, "CatchThrow");
		if (value != null) {
			catchThrow = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (name != null) {
			s += exportAttribute("Name", name);
		}
		if (catchThrow != null) {
			s += exportAttribute("CatchThrow", catchThrow);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "CatchThrow", catchThrow, Arrays.asList("CATCH", "THROW"), false);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCatchThrow() {
		return catchThrow;
	}

	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}

	public List<XpdlExpressionType> getPropertiesList() {
		return propertiesList;
	}
}
