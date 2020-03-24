package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlParticipantType;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.datatypes.XpdlExternalReference;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Participant"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:ParticipantType"/> <xsd:element
 *         ref="xpdl:Description" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ExternalReference" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ExtendedAttributes" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlParticipant extends XpdlIdName {

	/*
	 * Elements
	 */
	private XpdlParticipantType participantType;
	private XpdlDescription description;
	private XpdlExternalReference externalReference;
	private XpdlExtendedAttributes extendedAttributes;

	public XpdlParticipant(String tag) {
		super(tag);

		participantType = null;
		description = null;
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
		if (xpp.getName().equals("ParticipantType")) {
			participantType = new XpdlParticipantType("ParticipantType");
			participantType.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
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
		if (participantType != null) {
			s += participantType.exportElement();
		}
		if (description != null) {
			s += description.exportElement();
		}
		if (externalReference != null) {
			s += externalReference.exportElement();
		}
		if (extendedAttributes != null) {
			s += extendedAttributes.exportElement();
		}
		return s;
	}

	public XpdlParticipantType getParticipantType() {
		return participantType;
	}

	public void setParticipantType(XpdlParticipantType participantType) {
		this.participantType = participantType;
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
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
