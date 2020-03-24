package org.apromore.processmining.plugins.xpdl.idname;

import java.util.ArrayList;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Group"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Category" minOccurs="0"/> <xsd:element
 *         name="Object" minOccurs="0" maxOccurs="unbounded"> <xsd:complexType>
 *         <xsd:attribute name="Id" type="xpdl:Id" use="required"/>
 *         </xsd:complexType> </xsd:element> <xsd:any namespace="##other"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Name" type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlGroup extends XpdlIdName {

	private class XpdlObjectId extends XpdlElement {

		/*
		 * Attributes
		 */
		private String id;

		public XpdlObjectId(String tag) {
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
	 * Elements
	 */
	private XpdlCategory category;
	private final List<XpdlObjectId> objectList;

	public XpdlGroup(String tag) {
		super(tag);

		category = null;
		objectList = new ArrayList<XpdlObjectId>();
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Category")) {
			category = new XpdlCategory("Category");
			category.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Object")) {
			XpdlObjectId object = new XpdlObjectId("Object");
			object.importElement(xpp, xpdl);
			objectList.add(object);
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
		if (category != null) {
			s += category.exportElement();
		}
		for (XpdlObjectId object : objectList) {
			s += object.exportElement();
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
	}

	public XpdlCategory getCategory() {
		return category;
	}

	public void setCategory(XpdlCategory category) {
		this.category = category;
	}

	public List<XpdlObjectId> getObjectList() {
		return objectList;
	}
}
