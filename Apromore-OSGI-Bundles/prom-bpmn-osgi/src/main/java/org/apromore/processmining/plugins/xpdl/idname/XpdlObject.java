package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.collections.XpdlCategories;
import org.apromore.processmining.plugins.xpdl.text.XpdlDocumentation;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Object"> <xsd:annotation> <xsd:documentation>BPMN:
 *         This is used to identify the Activity in an EndEvent
 *         Compensation???Also used to associate categories and ocumentation
 *         with a variety of elements</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence minOccurs="0"> <xsd:element
 *         ref="xpdl:Categories" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Documentation" minOccurs="0"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="Id" type="xsd:NMTOKEN"
 *         use="required"> <xsd:annotation> <xsd:documentation>This identifies
 *         any Object in the BPMN diagram.</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="Name" type="xsd:string"
 *         use="optional"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlObject extends XpdlIdName {

	/*
	 * Elements
	 */
	private XpdlCategories categories;
	private XpdlDocumentation documentation;

	public XpdlObject(String tag) {
		super(tag);

		categories = null;
		documentation = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Categories")) {
			categories = new XpdlCategories("Categories");
			categories.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Documentation")) {
			documentation = new XpdlDocumentation("Documentation");
			documentation.importElement(xpp, xpdl);
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
		if (categories != null) {
			s += categories.exportElement();
		}
		if (documentation != null) {
			s += documentation.exportElement();
		}
		return s;
	}

	public XpdlCategories getCategories() {
		return categories;
	}

	public void setCategories(XpdlCategories categories) {
		this.categories = categories;
	}

	public XpdlDocumentation getDocumentation() {
		return documentation;
	}

	public void setDocumentation(XpdlDocumentation documentation) {
		this.documentation = documentation;
	}
}
