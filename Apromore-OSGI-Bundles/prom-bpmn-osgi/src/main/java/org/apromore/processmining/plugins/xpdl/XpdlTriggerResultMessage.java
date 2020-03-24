package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.idname.XpdlMessageType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerResultMessage"> <xsd:annotation>
 *         <xsd:documentation> BPMN: If the Trigger or Result Type is Message
 *         then this must be present </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="Message"
 *         type="xpdl:MessageType" minOccurs="0"/> <xsd:element
 *         ref="xpdl:WebServiceOperation" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="CatchThrow" use="optional" default="CATCH"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration value="CATCH"/>
 *         <xsd:enumeration value="THROW"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTriggerResultMessage extends XpdlElement {

	/*
	 * Attributes
	 */
	private String catchThrow;

	/*
	 * Elements
	 */
	private XpdlMessageType message;
	private XpdlWebServiceOperation webServiceOperation;

	public XpdlTriggerResultMessage(String tag) {
		super(tag);

		catchThrow = "CATCH";

		message = null;
		webServiceOperation = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Message")) {
			message = new XpdlMessageType("Message");
			message.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("WebServiceOperation")) {
			webServiceOperation = new XpdlWebServiceOperation("WebServiceOperation");
			webServiceOperation.importElement(xpp, xpdl);
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
		if (message != null) {
			s += message.exportElement();
		}
		if (webServiceOperation != null) {
			s += webServiceOperation.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "CatchThrow");
		if (value != null) {
			catchThrow = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (catchThrow != null) {
			s += exportAttribute("CatchThrow", catchThrow);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "CatchThrow", catchThrow, Arrays.asList("CATCH", "THROW"), false);
	}

	public String getCatchThrow() {
		return catchThrow;
	}

	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}

	public XpdlMessageType getMessage() {
		return message;
	}

	public void setMessage(XpdlMessageType message) {
		this.message = message;
	}

	public XpdlWebServiceOperation getWebServiceOperation() {
		return webServiceOperation;
	}

	public void setWebServiceOperation(XpdlWebServiceOperation webServiceOperation) {
		this.webServiceOperation = webServiceOperation;
	}
}
