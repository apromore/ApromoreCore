package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Script"> <xsd:complexType> <xsd:sequence> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Type"
 *         type="xsd:string" use="required"/> <xsd:attribute name="Version"
 *         type="xsd:string" use="optional"/> <xsd:attribute name="Grammar"
 *         type="xsd:anyURI" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlScript extends XpdlElement {

	/*
	 * Attributes
	 */
	private String type;
	private String version;
	private String grammar;

	public XpdlScript(String tag) {
		super(tag);

		type = null;
		version = null;
		grammar = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Type");
		if (value != null) {
			type = value;
		}
		value = xpp.getAttributeValue(null, "Version");
		if (value != null) {
			version = value;
		}
		value = xpp.getAttributeValue(null, "Grammar");
		if (value != null) {
			grammar = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (type != null) {
			s += exportAttribute("Type", type);
		}
		if (version != null) {
			s += exportAttribute("Version", version);
		}
		if (grammar != null) {
			s += exportAttribute("Grammar", grammar);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "Type", type);
		checkURI(xpdl, "Grammar", grammar, false);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGrammar() {
		return grammar;
	}

	public void setGrammar(String grammar) {
		this.grammar = grammar;
	}
}
