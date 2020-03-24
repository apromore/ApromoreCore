package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TypeDeclaration"> <xsd:complexType> <xsd:sequence>
 *         <xsd:group ref="xpdl:DataTypes"/> <xsd:element ref="xpdl:Description"
 *         minOccurs="0"/> <xsd:element ref="xpdl:ExtendedAttributes"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:ID" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTypeDeclaration extends XpdlDataTypes {

	/*
	 * Attributes
	 */
	private String id;
	private String name;

	/*
	 * Elements
	 */
	private XpdlDescription description;
	private XpdlExtendedAttributes extendedAttributes;

	public XpdlTypeDeclaration(String tag) {
		super(tag);

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
		String value = xpp.getAttributeValue(null, "Id");
		if (value != null) {
			id = value;
		}
		value = xpp.getAttributeValue(null, "Name");
		if (value != null) {
			name = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (id != null) {
			s += exportAttribute("Id", id);
		}
		if (name != null) {
			s += exportAttribute("Name", name);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		checkRequired(xpdl, "Id", id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
