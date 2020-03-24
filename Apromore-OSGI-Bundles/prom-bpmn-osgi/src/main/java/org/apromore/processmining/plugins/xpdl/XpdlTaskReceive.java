package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.idname.XpdlMessageType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TaskReceive"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="Message"
 *         type="xpdl:MessageType" minOccurs="0"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Implementation-related but required by
 *         spec.</xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:element ref="xpdl:WebServiceOperation" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Instantiate" type="xsd:boolean" use="required"/> <xsd:attribute
 *         name="Implementation" use="optional" default="WebService">
 *         <xsd:annotation> <xsd:documentation>BPMN: Implementation-related but
 *         required by spec.</xsd:documentation> </xsd:annotation>
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="WebService"/> <xsd:enumeration
 *         value="Other"/> <xsd:enumeration value="Unspecified"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTaskReceive extends XpdlElement {

	/*
	 * Attributes
	 */
	private String instantiate;
	private String implementation;

	/*
	 * Elements
	 */
	private XpdlMessageType message;
	private XpdlWebServiceOperation webServiceOperation;

	public XpdlTaskReceive(String tag) {
		super(tag);

		instantiate = null;
		implementation = null;

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
		String value = xpp.getAttributeValue(null, "Instantiate");
		if (value != null) {
			instantiate = value;
		}
		value = xpp.getAttributeValue(null, "Implementation");
		if (value != null) {
			implementation = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (instantiate != null) {
			s += exportAttribute("Instantiate", instantiate);
		}
		if (implementation != null) {
			s += exportAttribute("Implementation", implementation);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkBoolean(xpdl, "Instantiate", instantiate, true);
		checkRestriction(xpdl, "Implementation", implementation, Arrays.asList("WebService", "Other", "Unspecified"),
				false);
	}

	public String getInstantiate() {
		return instantiate;
	}

	public void setInstantiate(String instantiate) {
		this.instantiate = instantiate;
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
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
