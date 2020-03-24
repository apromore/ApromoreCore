package org.apromore.processmining.plugins.xpdl;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="PartnerLinkType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element name="Role" maxOccurs="2"> <xsd:complexType>
 *         <xsd:sequence> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="portType" type="xsd:string" use="required"/> <xsd:attribute
 *         name="Name" type="xsd:string" use="required"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="name" type="xsd:string" use="required"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlPartnerLinkType extends XpdlElement {

	private class XpdlRole extends XpdlElement {

		/*
		 * Attributes
		 */
		private String portType;
		private String name;

		public XpdlRole(String tag) {
			super(tag);

			portType = null;
			name = null;
		}

		protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
			super.importAttributes(xpp, xpdl);
			String value = xpp.getAttributeValue(null, "Name");
			if (value != null) {
				name = value;
			}
			value = xpp.getAttributeValue(null, "portType");
			if (value != null) {
				portType = value;
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
			if (portType != null) {
				s += exportAttribute("portType", portType);
			}
			return s;
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkRequired(xpdl, "Name", name);
			checkRequired(xpdl, "portType", portType);
		}
	}

	/*
	 * Attributes
	 */
	private String id;
	private String name;

	/*
	 * Elements
	 */
	private final List<XpdlRole> roleList;

	public XpdlPartnerLinkType(String tag) {
		super(tag);

		id = null;
		name = null;

		roleList = new ArrayList<XpdlRole>();
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Role")) {
			XpdlRole role = new XpdlRole("Role");
			role.importElement(xpp, xpdl);
			roleList.add(role);
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
		for (XpdlRole role : roleList) {
			s += role.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "name");
		if (value != null) {
			name = value;
		}
		value = xpp.getAttributeValue(null, "Id");
		if (value != null) {
			id = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (name != null) {
			s += exportAttribute("name", name);
		}
		if (id != null) {
			s += exportAttribute("Id", id);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "name", name);
		checkRequired(xpdl, "Id", id);
		if (roleList.size() > 2) {
			xpdl.log(tag, lineNumber, "Expected up to two roles");
		}
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

	public List<XpdlRole> getRoleList() {
		return roleList;
	}

}
