package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.collections.XpdlResponsibles;
import org.apromore.processmining.plugins.xpdl.text.XpdlVersion;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="RedefinableHeader"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:Author" minOccurs="0"/>
 *         <xsd:element ref="xpdl:Version" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Codepage" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Countrykey" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Responsibles" minOccurs="0"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="PublicationStatus">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="UNDER_REVISION"/> <xsd:enumeration
 *         value="RELEASED"/> <xsd:enumeration value="UNDER_TEST"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlRedefinableHeader extends XpdlElement {

	/*
	 * Attributes
	 */
	private String publicationStatus;

	/*
	 * Elements
	 */
	private XpdlAuthor author;
	private XpdlVersion version;
	private XpdlCodepage codepage;
	private XpdlCountrykey countrykey;
	private XpdlResponsibles responsibles;

	public XpdlRedefinableHeader(String tag) {
		super(tag);

		publicationStatus = null;

		author = null;
		version = null;
		codepage = null;
		countrykey = null;
		responsibles = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Author")) {
			author = new XpdlAuthor("Author");
			author.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Version")) {
			version = new XpdlVersion("Version");
			version.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Codepage")) {
			codepage = new XpdlCodepage("Codepage");
			codepage.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Countrykey")) {
			countrykey = new XpdlCountrykey("Countrykey");
			countrykey.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Responsibles")) {
			responsibles = new XpdlResponsibles("Responsibles");
			responsibles.importElement(xpp, xpdl);
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
		if (author != null) {
			s += author.exportElement();
		}
		if (version != null) {
			s += version.exportElement();
		}
		if (codepage != null) {
			s += codepage.exportElement();
		}
		if (countrykey != null) {
			s += countrykey.exportElement();
		}
		if (responsibles != null) {
			s += responsibles.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "PublicationStatus");
		if (value != null) {
			publicationStatus = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (publicationStatus != null) {
			s += exportAttribute("PublicationStatus", publicationStatus);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "PublicationStatus", publicationStatus, Arrays.asList("UNDER_REVISION", "RELEASED",
				"UNDER_TEST"), false);
	}

	public String getPublicationStatus() {
		return publicationStatus;
	}

	public void setPublicationStatus(String publicationStatus) {
		this.publicationStatus = publicationStatus;
	}

	public XpdlAuthor getAuthor() {
		return author;
	}

	public void setAuthor(XpdlAuthor author) {
		this.author = author;
	}

	public XpdlVersion getVersion() {
		return version;
	}

	public void setVersion(XpdlVersion version) {
		this.version = version;
	}

	public XpdlCodepage getCodepage() {
		return codepage;
	}

	public void setCodepage(XpdlCodepage codepage) {
		this.codepage = codepage;
	}

	public XpdlCountrykey getCountrykey() {
		return countrykey;
	}

	public void setCountrykey(XpdlCountrykey countrykey) {
		this.countrykey = countrykey;
	}

	public XpdlResponsibles getResponsibles() {
		return responsibles;
	}

	public void setResponsibles(XpdlResponsibles responsibles) {
		this.responsibles = responsibles;
	}
}
