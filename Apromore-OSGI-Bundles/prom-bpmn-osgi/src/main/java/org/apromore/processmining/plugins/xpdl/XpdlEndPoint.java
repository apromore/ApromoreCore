package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.datatypes.XpdlExternalReference;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="EndPoint"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:ExternalReference"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="EndPointType" use="optional" default="WSDL"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration value="WSDL"/>
 *         <xsd:enumeration value="Service"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlEndPoint extends XpdlElement {

	/*
	 * Attributes
	 */
	private String endPointType;

	/*
	 * Elements
	 */
	private XpdlExternalReference externalReference;

	public XpdlEndPoint(String tag) {
		super(tag);

		endPointType = null;

		externalReference = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("ExternalReference")) {
			externalReference = new XpdlExternalReference("ExternalReference");
			externalReference.importElement(xpp, xpdl);
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
		if (externalReference != null) {
			s += externalReference.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "EndPointType");
		if (value != null) {
			endPointType = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (endPointType != null) {
			s += exportAttribute("EndPointType", endPointType);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "EndPointType", endPointType, Arrays.asList("WSDL", "Service"), false);
	}

	public String getEndPointType() {
		return endPointType;
	}

	public void setEndPointType(String endPointType) {
		this.endPointType = endPointType;
	}

	public XpdlExternalReference getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(XpdlExternalReference externalReference) {
		this.externalReference = externalReference;
	}
}
