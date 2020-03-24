package org.apromore.processmining.plugins.xpdl.collections;

import java.util.ArrayList;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlArtifactInput;
import org.apromore.processmining.plugins.xpdl.XpdlInput;
import org.apromore.processmining.plugins.xpdl.XpdlPropertyInput;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="InputSet"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:Input"
 *         maxOccurs="unbounded"/> <xsd:element ref="xpdl:ArtifactInput"
 *         minOccurs="0" maxOccurs="unbounded"/> <xsd:element
 *         ref="xpdl:PropertyInput" minOccurs="0" maxOccurs="unbounded"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlInputSet extends XpdlCollections<XpdlInput> {

	/*
	 * Elements
	 */
	private final List<XpdlArtifactInput> artifactInputList;
	private final List<XpdlPropertyInput> propertyInputList;

	public XpdlInputSet(String tag) {
		super(tag);

		artifactInputList = new ArrayList<XpdlArtifactInput>();
		propertyInputList = new ArrayList<XpdlPropertyInput>();
	}

	public XpdlInput create() {
		return new XpdlInput("Input");
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("ArtifactInput")) {
			XpdlArtifactInput artifactInput = new XpdlArtifactInput("ArtifactInput");
			artifactInput.importElement(xpp, xpdl);
			artifactInputList.add(artifactInput);
			return true;
		}
		if (xpp.getName().equals("PropertyInput")) {
			XpdlPropertyInput propertyInput = new XpdlPropertyInput("PropertyInput");
			propertyInput.importElement(xpp, xpdl);
			propertyInputList.add(propertyInput);
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
		for (XpdlArtifactInput artifactInput : artifactInputList) {
			s += artifactInput.exportElement();
		}
		for (XpdlPropertyInput propertyInput : propertyInputList) {
			s += propertyInput.exportElement();
		}
		return s;
	}

	public List<XpdlArtifactInput> getArtifactInputList() {
		return artifactInputList;
	}

	public List<XpdlPropertyInput> getPropertyInputList() {
		return propertyInputList;
	}
}
