package org.apromore.processmining.plugins.xpdl.datatypes;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.apromore.processmining.plugins.xpdl.text.XpdlLength;
import org.apromore.processmining.plugins.xpdl.text.XpdlPrecision;
import org.apromore.processmining.plugins.xpdl.text.XpdlScale;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="BasicType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Length" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Precision" minOccurs="0"/> <xsd:element ref="xpdl:Scale"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Type" use="required"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="STRING"/>
 *         <xsd:enumeration value="FLOAT"/> <xsd:enumeration value="INTEGER"/>
 *         <xsd:enumeration value="REFERENCE"/> <xsd:enumeration
 *         value="DATETIME"/> <xsd:enumeration value="DATE"/> <xsd:enumeration
 *         value="TIME"/> <xsd:enumeration value="BOOLEAN"/> <xsd:enumeration
 *         value="PERFORMER"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlBasicType extends XpdlElement {

	/*
	 * Attributes
	 */
	private String type;

	/*
	 * Elements
	 */
	private XpdlLength length;
	private XpdlPrecision precision;
	private XpdlScale scale;

	public XpdlBasicType(String tag) {
		super(tag);

		type = null;

		length = null;
		precision = null;
		scale = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Length")) {
			length = new XpdlLength("Length");
			length.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Precision")) {
			precision = new XpdlPrecision("Precision");
			precision.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Scale")) {
			scale = new XpdlScale("Scale");
			scale.importElement(xpp, xpdl);
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
		if (length != null) {
			s += length.exportElement();
		}
		if (precision != null) {
			s += precision.exportElement();
		}
		if (scale != null) {
			s += scale.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Type");
		if (value != null) {
			type = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (type != null) {
			s += exportAttribute("Type", type);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Type", type, Arrays.asList("STRING", "FLOAT", "INTEGER", "REFERENCE", "DATETIME",
				"DATE", "TIME", "BOOLEAN", "PERFORMER"), true);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public XpdlLength getLength() {
		return length;
	}

	public void setLength(XpdlLength length) {
		this.length = length;
	}

	public XpdlPrecision getPrecision() {
		return precision;
	}

	public void setPrecision(XpdlPrecision precision) {
		this.precision = precision;
	}

	public XpdlScale getScale() {
		return scale;
	}

	public void setScale(XpdlScale scale) {
		this.scale = scale;
	}
}
