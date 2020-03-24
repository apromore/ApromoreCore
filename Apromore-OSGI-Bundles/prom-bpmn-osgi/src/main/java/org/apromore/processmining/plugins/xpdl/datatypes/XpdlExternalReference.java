package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ExternalReference"> <xsd:complexType>
 *         <xsd:sequence> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="xref" type="xsd:NMTOKEN" use="optional"/> <xsd:attribute
 *         name="location" type="xsd:anyURI" use="required"/> <xsd:attribute
 *         name="namespace" type="xsd:anyURI" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlExternalReference extends XpdlElement {

	/*
	 * Attributes
	 */
	private String xref;
	private String location;
	private String namespace;

	public XpdlExternalReference(String tag) {
		super(tag);

		xref = null;
		location = null;
		namespace = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "xref");
		if (value != null) {
			xref = value;
		}
		value = xpp.getAttributeValue(null, "location");
		if (value != null) {
			location = value;
		}
		value = xpp.getAttributeValue(null, "namespace");
		if (value != null) {
			namespace = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (xref != null) {
			s += exportAttribute("xref", xref);
		}
		if (location != null) {
			s += exportAttribute("location", location);
		}
		if (namespace != null) {
			s += exportAttribute("namespace", namespace);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkURI(xpdl, "location", location, true);
		checkURI(xpdl, "namespace", namespace, false);
	}

	public String getXref() {
		return xref;
	}

	public void setXref(String xref) {
		this.xref = xref;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}
