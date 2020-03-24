package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlDataMapping;
import org.apromore.processmining.plugins.xpdl.XpdlEndPoint;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActualParameters;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="SubFlow"> <xsd:complexType> <xsd:sequence>
 *         <xsd:choice minOccurs="0"> <xsd:element ref="xpdl:ActualParameters"/>
 *         <xsd:element ref="xpdl:DataMappings"/> </xsd:choice> <xsd:element
 *         ref="xpdl:EndPoint" minOccurs="0"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="Id" type="xpdl:IdRef"
 *         use="required"> <xsd:annotation> <xsd:documentation>BPMN: Corresponds
 *         to BPMN attribute ProcessRef, pointer to WorkflowProcess/@Id in BPD
 *         referenced by PackageRef. [Suggest name change to ProcessRef; this is
 *         IDREF not ID].</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="Name" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="Execution" use="optional" default="SYNCHR">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="ASYNCHR"/> <xsd:enumeration value="SYNCHR"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="View" use="optional" default="COLLAPSED"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Detrmines rendering of subprocess as
 *         Collapsed or Expended. Default is Collapsed.</xsd:documentation>
 *         </xsd:annotation> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="COLLAPSED"/>
 *         <xsd:enumeration value="EXPANDED"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute name="PackageRef"
 *         type="xpdl:IdRef" use="optional"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Corresponds to BPMN attribute DiagramRef,
 *         pointer to a BPD identified by Package/@Id. [Maybe IDREF doesn't work
 *         here since ID is in a different document.]</xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:attribute
 *         name="InstanceDataField" type="xsd:string" use="optional">
 *         <xsd:annotation> <xsd:documentation> Used to store the instance id of
 *         the subflow instantiated by the activity. This is then available
 *         later on (e.g. for correlation, messaging etc.) especially in the
 *         case of asynchronous invocation.</xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:attribute
 *         name="StartActivitySetId" type="xpdl:IdRef" use="optional"/>
 *         <xsd:attribute name="StartActivityId" type="xpdl:IdRef"
 *         use="optional"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlSubFlow extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String execution;
	private String view;
	private String packageRef;
	private String instanceDataField;
	private String startActivitySetId;
	private String startActivityId;

	/*
	 * Elements
	 */
	private XpdlActualParameters actualParameters;
	private XpdlDataMapping dataMapping;
	private XpdlEndPoint endPoint;

	public XpdlSubFlow(String tag) {
		super(tag);

		execution = null;
		view = null;
		packageRef = null;
		instanceDataField = null;
		startActivitySetId = null;
		startActivityId = null;

		actualParameters = null;
		dataMapping = null;
		endPoint = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("ActualParameters")) {
			actualParameters = new XpdlActualParameters("ActualParameters");
			actualParameters.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("DataMapping")) {
			dataMapping = new XpdlDataMapping("DataMapping");
			dataMapping.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("EndPoint")) {
			endPoint = new XpdlEndPoint("EndPoint");
			endPoint.importElement(xpp, xpdl);
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
		if (actualParameters != null) {
			s += actualParameters.exportElement();
		}
		if (dataMapping != null) {
			s += dataMapping.exportElement();
		}
		if (endPoint != null) {
			s += endPoint.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Execution");
		if (value != null) {
			execution = value;
		}
		value = xpp.getAttributeValue(null, "View");
		if (value != null) {
			view = value;
		}
		value = xpp.getAttributeValue(null, "PackageRef");
		if (value != null) {
			packageRef = value;
		}
		value = xpp.getAttributeValue(null, "InstanceDataField");
		if (value != null) {
			instanceDataField = value;
		}
		value = xpp.getAttributeValue(null, "StartActivitySetId");
		if (value != null) {
			startActivitySetId = value;
		}
		value = xpp.getAttributeValue(null, "StartActivityId");
		if (value != null) {
			startActivityId = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (execution != null) {
			s += exportAttribute("Execution", execution);
		}
		if (view != null) {
			s += exportAttribute("View", view);
		}
		if (packageRef != null) {
			s += exportAttribute("PackageRef", packageRef);
		}
		if (instanceDataField != null) {
			s += exportAttribute("InstanceDataField", instanceDataField);
		}
		if (startActivitySetId != null) {
			s += exportAttribute("StartActivitySetId", startActivitySetId);
		}
		if (startActivityId != null) {
			s += exportAttribute("StartActivityId", startActivityId);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Execution", execution, Arrays.asList("ASYNCHR", "SYNCHR"), false);
		checkRestriction(xpdl, "View", view, Arrays.asList("COLLAPSED", "EXPANDED"), false);
	}

	public String getExecution() {
		return execution;
	}

	public void setExecution(String execution) {
		this.execution = execution;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getPackageRef() {
		return packageRef;
	}

	public void setPackageRef(String packageRef) {
		this.packageRef = packageRef;
	}

	public String getInstanceDataField() {
		return instanceDataField;
	}

	public void setInstanceDataField(String instanceDataField) {
		this.instanceDataField = instanceDataField;
	}

	public String getStartActivitySetId() {
		return startActivitySetId;
	}

	public void setStartActivitySetId(String startActivitySetId) {
		this.startActivitySetId = startActivitySetId;
	}

	public String getStartActivityId() {
		return startActivityId;
	}

	public void setStartActivityId(String startActivityId) {
		this.startActivityId = startActivityId;
	}

	public XpdlActualParameters getActualParameters() {
		return actualParameters;
	}

	public void setActualParameters(XpdlActualParameters actualParameters) {
		this.actualParameters = actualParameters;
	}

	public XpdlDataMapping getDataMapping() {
		return dataMapping;
	}

	public void setDataMapping(XpdlDataMapping dataMapping) {
		this.dataMapping = dataMapping;
	}

	public XpdlEndPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(XpdlEndPoint endPoint) {
		this.endPoint = endPoint;
	}
}
