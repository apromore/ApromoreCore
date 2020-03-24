package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.datatypes.XpdlDataType;
import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.apromore.processmining.plugins.xpdl.text.XpdlLength;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="DataField"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:DataType"/> <xsd:element name="InitialValue"
 *         type="xpdl:ExpressionType" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Length" minOccurs="0"/> <xsd:element ref="xpdl:Description"
 *         minOccurs="0"/> <xsd:element ref="xpdl:ExtendedAttributes"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Name" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="ReadOnly" type="xsd:boolean" use="optional" default="false"/>
 *         <xsd:attribute name="IsArray" type="xsd:boolean" use="optional"
 *         default="false"/> <xsd:attribute name="Correlation"
 *         type="xsd:boolean" use="optional" default="false"> <xsd:annotation>
 *         <xsd:documentation>Used in BPMN to support mapping to
 *         BPEL</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlDataField extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String readOnly;
	private String isArray;
	private String correlation;

	/*
	 * Elements
	 */
	private XpdlDataType dataType;
	private XpdlExpressionType initialValue;
	private XpdlLength length;
	private XpdlDescription description;
	private XpdlExtendedAttributes extendedAttributes;

	public XpdlDataField(String tag) {
		super(tag);

		readOnly = null;
		isArray = null;
		correlation = null;

		dataType = null;
		initialValue = null;
		length = null;
		description = null;
		extendedAttributes = null;
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
		if (xpp.getName().equals("Length")) {
			length = new XpdlLength("Length");
			length.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExtendedAttributes")) {
			extendedAttributes = new XpdlExtendedAttributes("ExtendedAttributes");
			extendedAttributes.importElement(xpp, xpdl);
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
		if (length != null) {
			s += length.exportElement();
		}
		if (description != null) {
			s += description.exportElement();
		}
		if (extendedAttributes != null) {
			s += extendedAttributes.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ReadOnly");
		if (value != null) {
			readOnly = value;
		}
		value = xpp.getAttributeValue(null, "IsArray");
		if (value != null) {
			isArray = value;
		}
		value = xpp.getAttributeValue(null, "Correlation");
		if (value != null) {
			correlation = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (readOnly != null) {
			s += exportAttribute("ReadOnly", readOnly);
		}
		if (isArray != null) {
			s += exportAttribute("IsArray", isArray);
		}
		if (correlation != null) {
			s += exportAttribute("Correlation", correlation);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkBoolean(xpdl, "ReadOnly", readOnly, false);
		checkBoolean(xpdl, "IsArray", isArray, false);
	}

	public String getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}

	public String getIsArray() {
		return isArray;
	}

	public void setIsArray(String isArray) {
		this.isArray = isArray;
	}

	public String getCorrelation() {
		return correlation;
	}

	public void setCorrelation(String correlation) {
		this.correlation = correlation;
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

	public XpdlLength getLength() {
		return length;
	}

	public void setLength(XpdlLength length) {
		this.length = length;
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}

	public XpdlExtendedAttributes getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(XpdlExtendedAttributes extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}
}
