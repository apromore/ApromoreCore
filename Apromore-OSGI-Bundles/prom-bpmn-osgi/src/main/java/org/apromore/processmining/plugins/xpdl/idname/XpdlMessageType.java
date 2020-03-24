package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActualParameters;
import org.apromore.processmining.plugins.xpdl.collections.XpdlDataMappings;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:complexType name="MessageType"> <xsd:annotation>
 *         <xsd:documentation>Formal Parameters defined by WSDL. Must constraint
 *         the parameters to either all in or all out, because Message is in a
 *         single direction</xsd:documentation> </xsd:annotation> <xsd:sequence
 *         minOccurs="0"> <xsd:choice minOccurs="0"> <xsd:element
 *         ref="xpdl:ActualParameters"/> <xsd:element ref="xpdl:DataMappings"/>
 *         </xsd:choice> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Name" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="From" type="xsd:NMTOKEN" use="optional"> <xsd:annotation>
 *         <xsd:documentation>This must be the name of a
 *         Participant</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="To" type="xsd:NMTOKEN" use="optional">
 *         <xsd:annotation> <xsd:documentation>This must be the name of a
 *         participant</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="FaultName" type="xsd:NMTOKEN" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType>
 */
public class XpdlMessageType extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String from;
	private String to;
	private String faultName;

	/*
	 * Elements
	 */
	private XpdlActualParameters actualParameters;
	private XpdlDataMappings dataMappings;

	public XpdlMessageType(String tag) {
		super(tag);

		from = null;
		to = null;
		faultName = null;

		actualParameters = null;
		dataMappings = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("ActualParameters")) {
			actualParameters = new XpdlActualParameters("ActualParameters");
			actualParameters.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("DataMappings")) {
			dataMappings = new XpdlDataMappings("DataMappings");
			dataMappings.importElement(xpp, xpdl);
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
		if (actualParameters != null) {
			s += actualParameters.exportElement();
		}
		if (dataMappings != null) {
			s += dataMappings.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "From");
		if (value != null) {
			from = value;
		}
		value = xpp.getAttributeValue(null, "To");
		if (value != null) {
			to = value;
		}
		value = xpp.getAttributeValue(null, "FaultName");
		if (value != null) {
			faultName = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (from != null) {
			s += exportAttribute("From", from);
		}
		if (to != null) {
			s += exportAttribute("To", to);
		}
		if (faultName != null) {
			s += exportAttribute("FaultName", faultName);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFaultName() {
		return faultName;
	}

	public void setFaultName(String faultName) {
		this.faultName = faultName;
	}

	public XpdlActualParameters getActualParameters() {
		return actualParameters;
	}

	public void setActualParameters(XpdlActualParameters actualParameters) {
		this.actualParameters = actualParameters;
	}

	public XpdlDataMappings getDataMappings() {
		return dataMappings;
	}

	public void setDataMappings(XpdlDataMappings dataMappings) {
		this.dataMappings = dataMappings;
	}

}
