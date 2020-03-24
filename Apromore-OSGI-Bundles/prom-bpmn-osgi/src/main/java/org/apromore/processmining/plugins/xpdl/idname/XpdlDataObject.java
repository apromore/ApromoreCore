package org.apromore.processmining.plugins.xpdl.idname;

import java.util.ArrayList;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="DataObject"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="DataField"
 *         minOccurs="0" maxOccurs="unbounded"> <xsd:complexType> <xsd:attribute
 *         name="Id" type="xpdl:Id" use="required"/> </xsd:complexType>
 *         </xsd:element> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Name" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="State" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="RequiredForStart" type="xsd:boolean" use="optional">
 *         <xsd:annotation> <xsd:documentation>Deprecated in
 *         BPMN1.1</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="ProducedAtCompletion" type="xsd:boolean"
 *         use="optional"> <xsd:annotation> <xsd:documentation>Deprecated in
 *         BPMN1.1</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlDataObject extends XpdlIdName {

	private class XpdlDataFieldId extends XpdlElement {
		/*
		 * Attributes
		 */
		private String id;

		public XpdlDataFieldId(String tag) {
			super(tag);

			id = null;
		}

		protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
			super.importAttributes(xpp, xpdl);
			String value = xpp.getAttributeValue(null, "Id");
			if (value != null) {
				id = value;
			}
		}

		/**
		 * Exports all attributes.
		 */
		protected String exportAttributes() {
			String s = super.exportAttributes();
			if (id != null) {
				s += exportAttribute("Id", id);
			}
			return s;
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkRequired(xpdl, "Id", id);
		}
	}

	/*
	 * Attributes
	 */
	private String state;
	private String requiredForStart;
	private String producedAtCompletion;

	/*
	 * Elements
	 */
	private final List<XpdlDataFieldId> dataFieldList;

	public XpdlDataObject(String tag) {
		super(tag);

		state = null;
		requiredForStart = null;
		producedAtCompletion = null;

		dataFieldList = new ArrayList<XpdlDataFieldId>();
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("DataFieldId")) {
			XpdlDataFieldId dataField = new XpdlDataFieldId("DataFieldId");
			dataField.importElement(xpp, xpdl);
			dataFieldList.add(dataField);
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
		for (XpdlDataFieldId dataField : dataFieldList) {
			s += dataField.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "State");
		if (value != null) {
			state = value;
		}
		value = xpp.getAttributeValue(null, "RequiredForStart");
		if (value != null) {
			requiredForStart = value;
		}
		value = xpp.getAttributeValue(null, "ProducedAtCompletion");
		if (value != null) {
			producedAtCompletion = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (state != null) {
			s += exportAttribute("State", state);
		}
		if (requiredForStart != null) {
			s += exportAttribute("RequiredForStart", requiredForStart);
		}
		if (producedAtCompletion != null) {
			s += exportAttribute("ProducedAtCompletion", producedAtCompletion);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		for (XpdlDataFieldId dataField : dataFieldList) {
			dataField.checkValidity(xpdl);
		}
		checkBoolean(xpdl, "RequiredForStart", requiredForStart, false);
		checkBoolean(xpdl, "ProducedAtCompletion", producedAtCompletion, false);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRequiredForStart() {
		return requiredForStart;
	}

	public void setRequiredForStart(String requiredForStart) {
		this.requiredForStart = requiredForStart;
	}

	public String getProducedAtCompletion() {
		return producedAtCompletion;
	}

	public void setProducedAtCompletion(String producedAtCompletion) {
		this.producedAtCompletion = producedAtCompletion;
	}

	public List<XpdlDataFieldId> getDataFieldList() {
		return dataFieldList;
	}
}
