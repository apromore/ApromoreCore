package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Transaction"> <xsd:annotation> <xsd:documentation>
 *         BPMN: If SubProcess is a transaction then this is required.
 *         </xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="TransactionId" type="xsd:string" use="required"/>
 *         <xsd:attribute name="TransactionProtocol" type="xsd:string"
 *         use="required"/> <xsd:attribute name="TransactionMethod"
 *         use="required"> <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="Compensate"/> <xsd:enumeration
 *         value="Store"/> <xsd:enumeration value="Image"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTransaction extends XpdlElement {

	/*
	 * Attributes
	 */
	private String transactionId;
	private String transactionProtocol;
	private String transactionMethod;

	public XpdlTransaction(String tag) {
		super(tag);

		transactionId = null;
		transactionProtocol = null;
		transactionMethod = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "TransactionId");
		if (value != null) {
			transactionId = value;
		}
		value = xpp.getAttributeValue(null, "TransactionProtocol");
		if (value != null) {
			transactionProtocol = value;
		}
		value = xpp.getAttributeValue(null, "TransactionMethod");
		if (value != null) {
			transactionMethod = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (transactionId != null) {
			s += exportAttribute("TransactionId", transactionId);
		}
		if (transactionProtocol != null) {
			s += exportAttribute("TransactionProtocol", transactionProtocol);
		}
		if (transactionMethod != null) {
			s += exportAttribute("TransactionMethod", transactionMethod);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "TransactionId", transactionId);
		checkRequired(xpdl, "TransactionProtocol", transactionProtocol);
		checkRestriction(xpdl, "TransactionMethod", transactionMethod, Arrays.asList("Compensate", "Store", "Image"),
				true);
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionProtocol() {
		return transactionProtocol;
	}

	public void setTransactionProtocol(String transactionProtocol) {
		this.transactionProtocol = transactionProtocol;
	}

	public String getTransactionMethod() {
		return transactionMethod;
	}

	public void setTransactionMethod(String transactionMethod) {
		this.transactionMethod = transactionMethod;
	}
}
