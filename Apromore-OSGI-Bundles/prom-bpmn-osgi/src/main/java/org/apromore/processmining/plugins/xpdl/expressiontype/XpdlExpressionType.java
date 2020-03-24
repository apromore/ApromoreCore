package org.apromore.processmining.plugins.xpdl.expressiontype;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:complexType name="ExpressionType" mixed="true"> <xsd:choice
 *         minOccurs="0" maxOccurs="unbounded"> <xsd:any processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:choice> <xsd:attribute
 *         name="ScriptType" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="ScriptVersion" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="ScriptGrammar" type="xsd:anyURI"
 *         use="optional"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType>
 */
public class XpdlExpressionType extends XpdlElement {

	/*
	 * Attributes
	 */
	private String scriptType;
	private String scriptVersion;
	private String scriptGrammar;

	public XpdlExpressionType(String tag) {
		super(tag);

		scriptType = null;
		scriptVersion = null;
		scriptGrammar = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ScriptType");
		if (value != null) {
			scriptType = value;
		}
		value = xpp.getAttributeValue(null, "ScriptVersion");
		if (value != null) {
			scriptVersion = value;
		}
		value = xpp.getAttributeValue(null, "ScriptGrammar");
		if (value != null) {
			scriptGrammar = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (scriptType != null) {
			s += exportAttribute("ScriptType", scriptType);
		}
		if (scriptVersion != null) {
			s += exportAttribute("ScriptVersion", scriptVersion);
		}
		if (scriptGrammar != null) {
			s += exportAttribute("ScriptGrammar", scriptGrammar);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkURI(xpdl, "ScriptGrammar", scriptGrammar, false);
	}

	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public String getScriptVersion() {
		return scriptVersion;
	}

	public void setScriptVersion(String scriptVersion) {
		this.scriptVersion = scriptVersion;
	}

	public String getScriptGrammar() {
		return scriptGrammar;
	}

	public void setScriptGrammar(String scriptGrammar) {
		this.scriptGrammar = scriptGrammar;
	}
}
