package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.datatypes.XpdlDataType;
import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.apromore.processmining.plugins.xpdl.text.XpdlLength;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="FormalParameter"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:DataType"/> <xsd:element name="InitialValue"
 *         type="xpdl:ExpressionType" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Description" minOccurs="0"/> <xsd:element ref="xpdl:Length"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Mode" default="IN"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="IN"/> <xsd:enumeration
 *         value="OUT"/> <xsd:enumeration value="INOUT"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:attribute name="ReadOnly"
 *         type="xsd:boolean" use="optional" default="false"/> <xsd:attribute
 *         name="Required" type="xsd:boolean" use="optional" default="false"/>
 *         <xsd:attribute name="IsArray" type="xsd:boolean" use="optional"
 *         default="false"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlFormalParameter extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String mode;
	private String readOnly;
	private String required;
	private String isArray;

	/*
	 * Elements
	 */
	private XpdlDataType dataType;
	private XpdlExpressionType initialValue;
	private XpdlDescription description;
	private XpdlLength length;

	public XpdlFormalParameter(String tag) {
		super(tag);

		mode = null;
		readOnly = null;
		required = null;
		isArray = null;

		dataType = null;
		initialValue = null;
		description = null;
		length = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("DataType")) {
			dataType = new XpdlDataType("DataType");
			dataType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("InitialValue")) {
			initialValue = new XpdlExpressionType("InitialValue");
			initialValue.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Length")) {
			length = new XpdlLength("Length");
			length.importElement(xpp, xpdl);
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
		if (dataType != null) {
			s += dataType.exportElement();
		}
		if (initialValue != null) {
			s += initialValue.exportElement();
		}
		if (description != null) {
			s += description.exportElement();
		}
		if (length != null) {
			s += length.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Mode");
		if (value != null) {
			mode = value;
		}
		value = xpp.getAttributeValue(null, "ReadOnly");
		if (value != null) {
			readOnly = value;
		}
		value = xpp.getAttributeValue(null, "Required");
		if (value != null) {
			required = value;
		}
		value = xpp.getAttributeValue(null, "IsArray");
		if (value != null) {
			isArray = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (mode != null) {
			s += exportAttribute("Mode", mode);
		}
		if (readOnly != null) {
			s += exportAttribute("ReadOnly", readOnly);
		}
		if (required != null) {
			s += exportAttribute("Required", required);
		}
		if (isArray != null) {
			s += exportAttribute("IsArray", isArray);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Mode", mode, Arrays.asList("IN", "OUT", "INOUT"), false);
		checkBoolean(xpdl, "ReadOnly", readOnly, false);
		checkBoolean(xpdl, "Required", required, false);
		checkBoolean(xpdl, "IsArray", isArray, false);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getIsArray() {
		return isArray;
	}

	public void setIsArray(String isArray) {
		this.isArray = isArray;
	}

	public XpdlDataType getDataType() {
		return dataType;
	}

	public void setDataType(XpdlDataType dataType) {
		this.dataType = dataType;
	}

	public XpdlExpressionType getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(XpdlExpressionType initialValue) {
		this.initialValue = initialValue;
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}

	public XpdlLength getLength() {
		return length;
	}

	public void setLength(XpdlLength length) {
		this.length = length;
	}
}
