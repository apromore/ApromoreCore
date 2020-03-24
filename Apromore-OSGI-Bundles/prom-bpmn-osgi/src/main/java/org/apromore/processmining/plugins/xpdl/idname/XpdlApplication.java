package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlApplicationType;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.collections.XpdlFormalParameters;
import org.apromore.processmining.plugins.xpdl.datatypes.XpdlExternalReference;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Application"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Description" minOccurs="0"/> <xsd:element
 *         name="Type" type="xpdl:ApplicationType" minOccurs="0"/> <xsd:choice>
 *         <xsd:element ref="xpdl:FormalParameters"/> <xsd:element
 *         ref="xpdl:ExternalReference" minOccurs="0"/> </xsd:choice>
 *         <xsd:element ref="xpdl:ExtendedAttributes" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlApplication extends XpdlIdName {

	/*
	 * Elements
	 */
	private XpdlDescription description;
	private XpdlApplicationType applicationType;
	private XpdlFormalParameters formalParameters;
	private XpdlExternalReference externalReference;
	private XpdlExtendedAttributes extendedAttributes;

	public XpdlApplication(String tag) {
		super(tag);

		description = null;
		applicationType = null;
		formalParameters = null;
		externalReference = null;
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
		if (xpp.getName().equals("ApplicationType")) {
			applicationType = new XpdlApplicationType("ApplicationType");
			applicationType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("FormalParameters")) {
			formalParameters = new XpdlFormalParameters("FormalParameters");
			formalParameters.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExternalReference")) {
			externalReference = new XpdlExternalReference("ExternalReference");
			externalReference.importElement(xpp, xpdl);
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
		if (applicationType != null) {
			s += applicationType.exportElement();
		}
		if (formalParameters != null) {
			s += formalParameters.exportElement();
		}
		if (externalReference != null) {
			s += externalReference.exportElement();
		}
		if (extendedAttributes != null) {
			s += extendedAttributes.exportElement();
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		int n = (formalParameters != null ? 1 : 0) + (externalReference != null ? 1 : 0);
		if (n > 1) {
			xpdl.log(tag, lineNumber, "FormalParameters and ExternalReference are mutually exclusive");
		}
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}

	public XpdlApplicationType getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(XpdlApplicationType applicationType) {
		this.applicationType = applicationType;
	}

	public XpdlFormalParameters getFormalParameters() {
		return formalParameters;
	}

	public void setFormalParameters(XpdlFormalParameters formalParameters) {
		this.formalParameters = formalParameters;
	}

	public XpdlExternalReference getExternalReference() {
		return externalReference;
	}

	public void setExternalReference(XpdlExternalReference externalReference) {
		this.externalReference = externalReference;
	}

	public XpdlExtendedAttributes getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(XpdlExtendedAttributes extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}
}
