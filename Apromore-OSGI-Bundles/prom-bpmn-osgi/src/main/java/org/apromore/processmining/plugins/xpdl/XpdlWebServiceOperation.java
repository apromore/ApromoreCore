package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="WebServiceOperation"> <xsd:annotation>
 *         <xsd:documentation> BPMN: If the Implementation is a WebService this
 *         is required. </xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:choice> <xsd:element name="Partner">
 *         <xsd:complexType> <xsd:sequence> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="PartnerLinkId"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="RoleType"
 *         use="required"> <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="MyRole"/> <xsd:enumeration
 *         value="PartnerRole"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 *         <xsd:element name="Service"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:EndPoint"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="ServiceName" type="xsd:string"
 *         use="required"/> <xsd:attribute name="PortName" type="xsd:string"
 *         use="required"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 *         </xsd:choice> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="OperationName" type="xsd:string" use="required"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlWebServiceOperation extends XpdlElement {

	private class XpdlPartner extends XpdlElement {

		/*
		 * Attributes
		 */
		private String partnerLinkId;
		private String roleType;

		public XpdlPartner(String tag) {
			super(tag);

			partnerLinkId = null;
			roleType = null;
		}

		protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
			super.importAttributes(xpp, xpdl);
			String value = xpp.getAttributeValue(null, "PartnerLinkId");
			if (value != null) {
				partnerLinkId = value;
			}
			value = xpp.getAttributeValue(null, "RoleType");
			if (value != null) {
				roleType = value;
			}
		}

		/**
		 * Exports all attributes.
		 */
		protected String exportAttributes() {
			String s = super.exportAttributes();
			if (partnerLinkId != null) {
				s += exportAttribute("PartnerLinkId", partnerLinkId);
			}
			if (roleType != null) {
				s += exportAttribute("RoleType", roleType);
			}
			return s;
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkRequired(xpdl, "PartnerLinkId", partnerLinkId);
			checkRestriction(xpdl, "RoleType", roleType, Arrays.asList("MyRole", "PartnerRole"), true);
		}
	}

	private class XpdlService extends XpdlElement {

		/*
		 * Attributes
		 */
		private String serviceName;
		private String portName;

		/*
		 * Elements
		 */
		private XpdlEndPoint endPoint;

		public XpdlService(String tag) {
			super(tag);

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
			String value = xpp.getAttributeValue(null, "ServiceName");
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
			checkRequired(xpdl, "ServiceName", serviceName);
			checkRequired(xpdl, "PortName", portName);
		}
	}

	/*
	 * Attributes
	 */
	private String operationName;

	/*
	 * Elements
	 */
	private XpdlPartner partner;
	private XpdlService service;

	public XpdlWebServiceOperation(String tag) {
		super(tag);

		operationName = null;

		partner = null;
		service = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Partner")) {
			partner = new XpdlPartner("Partner");
			partner.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Service")) {
			service = new XpdlService("Service");
			service.importElement(xpp, xpdl);
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
		if (partner != null) {
			s += partner.exportElement();
		}
		if (service != null) {
			s += service.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "OperationName");
		if (value != null) {
			operationName = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (operationName != null) {
			s += exportAttribute("OperationName", operationName);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "OperationName", operationName);
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public XpdlPartner getPartner() {
		return partner;
	}

	public void setPartner(XpdlPartner partner) {
		this.partner = partner;
	}

	public XpdlService getService() {
		return service;
	}

	public void setService(XpdlService service) {
		this.service = service;
	}
}
