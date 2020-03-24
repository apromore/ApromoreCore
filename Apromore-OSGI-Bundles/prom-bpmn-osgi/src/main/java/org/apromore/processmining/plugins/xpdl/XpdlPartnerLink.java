package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="PartnerLink"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element name="MyRole" minOccurs="0"> <xsd:complexType>
 *         <xsd:sequence> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="RoleName" type="xsd:string" use="required"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element> <xsd:element name="PartnerRole" minOccurs="0">
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:EndPoint"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="RoleName" type="xsd:string" use="required"/> <xsd:attribute
 *         name="ServiceName" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="PortName" type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="name" type="xsd:string" use="required"/> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="PartnerLinkTypeId" type="xsd:NMTOKEN" use="required"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlPartnerLink extends XpdlElement {

	private class XpdlMyRole extends XpdlElement {

		/*
		 * Attributes
		 */
		private String roleName;

		public XpdlMyRole(String tag) {
			super(tag);

			roleName = null;
		}

		protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
			super.importAttributes(xpp, xpdl);
			String value = xpp.getAttributeValue(null, "RoleName");
			if (value != null) {
				roleName = value;
			}
		}

		/**
		 * Exports all attributes.
		 */
		protected String exportAttributes() {
			String s = super.exportAttributes();
			if (roleName != null) {
				s += exportAttribute("RoleName", roleName);
			}
			return s;
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkRequired(xpdl, "RoleName", roleName);
		}
	}

	private class XpdlPartnerRole extends XpdlElement {

		/*
		 * Attributes
		 */
		private String roleName;
		private String serviceName;
		private String portName;

		/*
		 * Elements
		 */
		private XpdlEndPoint endPoint;

		public XpdlPartnerRole(String tag) {
			super(tag);

			roleName = null;
			serviceName = null;
			portName = null;

			endPoint = null;
		}

		protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
			if (super.importElements(xpp, xpdl)) {
				/*
				 * Start tag corresponds to a known child element of an XPDL
				 * node.
				 */
				return true;
			}
			if (xpp.getName().equals("EndPoint")) {
				endPoint = new XpdlEndPoint("EndPoint");
				endPoint.importElement(xpp, xpdl);
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
			if (endPoint != null) {
				s += endPoint.exportElement();
			}
			return s;
		}

		protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
			super.importAttributes(xpp, xpdl);
			String value = xpp.getAttributeValue(null, "RoleName");
			if (value != null) {
				roleName = value;
			}
			value = xpp.getAttributeValue(null, "ServiceName");
			if (value != null) {
				serviceName = value;
			}
			value = xpp.getAttributeValue(null, "PortName");
			if (value != null) {
				portName = value;
			}
		}

		/**
		 * Exports all attributes.
		 */
		protected String exportAttributes() {
			String s = super.exportAttributes();
			if (roleName != null) {
				s += exportAttribute("RoleName", roleName);
			}
			if (serviceName != null) {
				s += exportAttribute("ServiceName", serviceName);
			}
			if (portName != null) {
				s += exportAttribute("PortName", portName);
			}
			return s;
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkRequired(xpdl, "RoleName", roleName);
		}
	}

	/*
	 * Attributes
	 */
	private String name;
	private String id;
	private String partnerLinkTypeId;

	/*
	 * Elements
	 */
	private XpdlMyRole myRole;
	private XpdlPartnerRole partnerRole;

	public XpdlPartnerLink(String tag) {
		super(tag);

		name = null;
		id = null;
		partnerLinkTypeId = null;

		myRole = null;
		partnerRole = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("MyRole")) {
			myRole = new XpdlMyRole("MyRole");
			myRole.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("PartnerRole")) {
			partnerRole = new XpdlPartnerRole("PartnerRole");
			partnerRole.importElement(xpp, xpdl);
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
		if (myRole != null) {
			s += myRole.exportElement();
		}
		if (partnerRole != null) {
			s += partnerRole.exportElement();
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
		value = xpp.getAttributeValue(null, "PartnerLinkTypeId");
		if (value != null) {
			partnerLinkTypeId = value;
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
		if (partnerLinkTypeId != null) {
			s += exportAttribute("PartnerLinkTypeId", partnerLinkTypeId);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "name", name);
		checkRequired(xpdl, "Id", id);
		checkRequired(xpdl, "PartnerLinkTypeId", partnerLinkTypeId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartnerLinkTypeId() {
		return partnerLinkTypeId;
	}

	public void setPartnerLinkTypeId(String partnerLinkTypeId) {
		this.partnerLinkTypeId = partnerLinkTypeId;
	}

	public XpdlMyRole getMyRole() {
		return myRole;
	}

	public void setMyRole(XpdlMyRole myRole) {
		this.myRole = myRole;
	}

	public XpdlPartnerRole getPartnerRole() {
		return partnerRole;
	}

	public void setPartnerRole(XpdlPartnerRole partnerRole) {
		this.partnerRole = partnerRole;
	}
}
