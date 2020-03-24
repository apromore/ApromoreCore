package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.collections.XpdlPerformers;
import org.apromore.processmining.plugins.xpdl.idname.XpdlMessageType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TaskUser"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:Performers"
 *         minOccurs="0"/> <xsd:element name="MessageIn" type="xpdl:MessageType"
 *         minOccurs="0"/> <xsd:element name="MessageOut"
 *         type="xpdl:MessageType" minOccurs="0"/> <xsd:element
 *         ref="xpdl:WebServiceOperation" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Implementation" use="optional" default="WebService">
 *         <xsd:annotation> <xsd:documentation>Required if the Task is
 *         User</xsd:documentation> </xsd:annotation> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration
 *         value="WebService"/> <xsd:enumeration value="Other"/>
 *         <xsd:enumeration value="Unspecified"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTaskUser extends XpdlElement {

	/*
	 * Attributes
	 */
	private String implementation;
	/*
	 * Elements
	 */
	private XpdlPerformers performers;
	private XpdlMessageType messageIn;
	private XpdlMessageType messageOut;
	private XpdlWebServiceOperation webServiceOperation;

	public XpdlTaskUser(String tag) {
		super(tag);

		implementation = null;

		performers = null;
		messageIn = null;
		messageOut = null;
		webServiceOperation = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Performers")) {
			performers = new XpdlPerformers("Performers");
			performers.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("MessageIn")) {
			messageIn = new XpdlMessageType("MessageIn");
			messageIn.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("MessageOut")) {
			messageOut = new XpdlMessageType("MessageOut");
			messageOut.importElement(xpp, xpdl);
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
		if (performers != null) {
			s += performers.exportElement();
		}
		if (messageIn != null) {
			s += messageIn.exportElement();
		}
		if (messageOut != null) {
			s += messageOut.exportElement();
		}
		if (webServiceOperation != null) {
			s += webServiceOperation.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Implementation");
		if (value != null) {
			implementation = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (implementation != null) {
			s += exportAttribute("Implementation", implementation);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Implementation", implementation, Arrays.asList("WebService", "Other", "Unspecified"),
				false);
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public XpdlPerformers getPerformers() {
		return performers;
	}

	public void setPerformers(XpdlPerformers performers) {
		this.performers = performers;
	}

	public XpdlMessageType getMessageIn() {
		return messageIn;
	}

	public void setMessageIn(XpdlMessageType messageIn) {
		this.messageIn = messageIn;
	}

	public XpdlMessageType getMessageOut() {
		return messageOut;
	}

	public void setMessageOut(XpdlMessageType messageOut) {
		this.messageOut = messageOut;
	}

	public XpdlWebServiceOperation getWebServiceOperation() {
		return webServiceOperation;
	}

	public void setWebServiceOperation(XpdlWebServiceOperation webServiceOperation) {
		this.webServiceOperation = webServiceOperation;
	}
}
