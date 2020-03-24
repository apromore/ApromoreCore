package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TaskReference"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in XPDL 2.2</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="TaskRef"
 *         type="xpdl:IdRef" use="required"> <xsd:annotation>
 *         <xsd:documentation> BPMN: Pointer to Activity/@Id that defines the
 *         task. </xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTaskReference extends XpdlElement {

	/*
	 * Attributes
	 */
	private String taskRef;

	public XpdlTaskReference(String tag) {
		super(tag);

		taskRef = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "TaskRef");
		if (value != null) {
			taskRef = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (taskRef != null) {
			s += exportAttribute("TaskRef", taskRef);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "TaskRef", taskRef);
	}

	public String getTaskRef() {
		return taskRef;
	}

	public void setTaskRef(String taskRef) {
		this.taskRef = taskRef;
	}
}
