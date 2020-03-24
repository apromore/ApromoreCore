package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActualParameters;
import org.apromore.processmining.plugins.xpdl.collections.XpdlDataMappings;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TaskApplication"> <xsd:complexType> <xsd:sequence>
 *         <xsd:choice minOccurs="0"> <xsd:element ref="xpdl:ActualParameters"/>
 *         <xsd:element ref="xpdl:DataMappings"/> </xsd:choice> <xsd:element
 *         ref="xpdl:Description" minOccurs="0"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="Id" type="xsd:NMTOKEN"
 *         use="required"/> <xsd:attribute name="Name" type="xsd:string"
 *         use="optional"/> <xsd:attribute name="PackageRef" type="xsd:NMTOKEN"
 *         use="optional"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTaskApplication extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String packageRef;

	/*
	 * Elements
	 */
	private XpdlActualParameters actualParameters;
	private XpdlDataMappings dataMappings;
	private XpdlDescription description;

	public XpdlTaskApplication(String tag) {
		super(tag);

		packageRef = null;

		actualParameters = null;
		dataMappings = null;
		description = null;
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
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
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
		if (description != null) {
			s += description.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "PackageRef");
		if (value != null) {
			packageRef = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (packageRef != null) {
			s += exportAttribute("PackageRef", packageRef);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		int nr = (actualParameters != null ? 1 : 0) + (dataMappings != null ? 1 : 0);
		if (nr > 1) {
			xpdl.log(tag, lineNumber, "Expected ActualParamters or DataMappings, not both");
		}
	}

	public String getPackageRef() {
		return packageRef;
	}

	public void setPackageRef(String packageRef) {
		this.packageRef = packageRef;
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

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}
}
