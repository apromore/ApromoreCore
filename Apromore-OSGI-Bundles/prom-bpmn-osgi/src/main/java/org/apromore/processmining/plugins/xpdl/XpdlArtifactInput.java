package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ArtifactInput"> <xsd:complexType> <xsd:sequence>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="ArtifactId" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="RequiredForStart" type="xsd:boolean" use="optional"
 *         default="true"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlArtifactInput extends XpdlElement {

	/*
	 * Attributes
	 */
	private String artifactId;
	private String requiredForStart;

	public XpdlArtifactInput(String tag) {
		super(tag);

		artifactId = null;
		requiredForStart = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ArtifactId");
		if (value != null) {
			artifactId = value;
		}
		value = xpp.getAttributeValue(null, "RequiredForStart");
		if (value != null) {
			requiredForStart = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (artifactId != null) {
			s += exportAttribute("ArtifactId", artifactId);
		}
		if (requiredForStart != null) {
			s += exportAttribute("RequiredForStart", requiredForStart);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "ArtifactId", artifactId);
		checkBoolean(xpdl, "RequiredForStart", requiredForStart, false);
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getRequiredForStart() {
		return requiredForStart;
	}

	public void setRequiredForStart(String requiredForStart) {
		this.requiredForStart = requiredForStart;
	}
}
