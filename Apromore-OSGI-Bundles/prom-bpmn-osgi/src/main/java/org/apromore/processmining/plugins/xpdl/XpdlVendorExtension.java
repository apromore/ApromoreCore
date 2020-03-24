package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="VendorExtension"> <xsd:complexType> <xsd:sequence>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="ToolId"
 *         type="xsd:string" use="required"/> <xsd:attribute
 *         name="schemaLocation" type="xsd:anyURI" use="required"/>
 *         <xsd:attribute name="extensionDescription" type="xsd:anyURI"
 *         use="optional"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlVendorExtension extends XpdlElement {

	/*
	 * Attributes
	 */
	private String toolId;
	private String schemaLocation;
	private String extensionDescription;

	public XpdlVendorExtension(String tag) {
		super(tag);

		toolId = null;
		schemaLocation = null;
		extensionDescription = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ToolId");
		if (value != null) {
			toolId = value;
		}
		value = xpp.getAttributeValue(null, "schemaLocation");
		if (value != null) {
			schemaLocation = value;
		}
		value = xpp.getAttributeValue(null, "extensionDescription");
		if (value != null) {
			extensionDescription = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (toolId != null) {
			s += exportAttribute("ToolId", toolId);
		}
		if (schemaLocation != null) {
			s += exportAttribute("schemaLocation", schemaLocation);
		}
		if (extensionDescription != null) {
			s += exportAttribute("extensionDescription", extensionDescription);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "ToolId", toolId);
		checkURI(xpdl, "schemaLocation", schemaLocation, true);
		checkURI(xpdl, "extensionDescription", extensionDescription, false);
	}

	public String getToolId() {
		return toolId;
	}

	public void setToolId(String toolId) {
		this.toolId = toolId;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public String getExtensionDescription() {
		return extensionDescription;
	}

	public void setExtensionDescription(String extensionDescription) {
		this.extensionDescription = extensionDescription;
	}
}
