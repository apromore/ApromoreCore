package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ResultError"> <xsd:annotation> <xsd:documentation>
 *         BPMN: Must be present if Trigger or ResultType is error.
 *         </xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="ErrorCode" type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlResultError extends XpdlElement {

	/*
	 * Attributes
	 */
	private String errorCode;

	public XpdlResultError(String tag) {
		super(tag);

		errorCode = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ErrorCode");
		if (value != null) {
			errorCode = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (errorCode != null) {
			s += exportAttribute("ErrorCode", errorCode);
		}
		return s;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
