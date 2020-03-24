package org.apromore.processmining.plugins.xpdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.idname.XpdlMessageType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         xsd:element name="TaskSend"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="Message"
 *         type="xpdl:MessageType" minOccurs="0"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Implementation-related but required by
 *         spec</xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:element ref="xpdl:WebServiceOperation" minOccurs="0"/>
 *         <xsd:element ref="xpdl:WebServiceFaultCatch" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="Implementation" use="optional"
 *         default="WebService"> <xsd:annotation> <xsd:documentation>Required if
 *         the Task is Send</xsd:documentation> </xsd:annotation>
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="WebService"/> <xsd:enumeration
 *         value="Other"/> <xsd:enumeration value="Unspecified"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTaskSend extends XpdlElement {

	/*
	 * Attributes
	 */
	private String implementation;
	/*
	 * Elements
	 */
	private XpdlMessageType message;
	private XpdlWebServiceOperation webServiceOperation;
	private final List<XpdlWebServiceFaultCatch> webServiceFaultCatchList;

	public XpdlTaskSend(String tag) {
		super(tag);

		implementation = null;

		message = null;
		webServiceOperation = null;
		webServiceFaultCatchList = new ArrayList<XpdlWebServiceFaultCatch>();
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
		if (xpp.getName().equals("WebServiceFaultCatch")) {
			XpdlWebServiceFaultCatch webServiceFaultCatch = new XpdlWebServiceFaultCatch("WebServiceFaultCatch");
			webServiceFaultCatch.importElement(xpp, xpdl);
			webServiceFaultCatchList.add(webServiceFaultCatch);
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
		for (XpdlWebServiceFaultCatch webServiceFaultCatch : webServiceFaultCatchList) {
			s += webServiceFaultCatch.exportElement();
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

	public List<XpdlWebServiceFaultCatch> getWebServiceFaultCatchList() {
		return webServiceFaultCatchList;
	}
}
