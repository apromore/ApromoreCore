package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Arrays;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlExtensions;
import org.apromore.processmining.plugins.xpdl.XpdlProcessHeader;
import org.apromore.processmining.plugins.xpdl.XpdlRedefinableHeader;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActivities;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActivitySets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlApplications;
import org.apromore.processmining.plugins.xpdl.collections.XpdlAssignments;
import org.apromore.processmining.plugins.xpdl.collections.XpdlDataFields;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.collections.XpdlFormalParameters;
import org.apromore.processmining.plugins.xpdl.collections.XpdlInputSets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlOutputSets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlParticipants;
import org.apromore.processmining.plugins.xpdl.collections.XpdlPartnerLinks;
import org.apromore.processmining.plugins.xpdl.collections.XpdlPools;
import org.apromore.processmining.plugins.xpdl.collections.XpdlTransitions;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:complexType name="ProcessType"> <xsd:sequence> <xsd:element
 *         ref="xpdl:ProcessHeader"/> <xsd:element ref="xpdl:RedefinableHeader"
 *         minOccurs="0"/> <xsd:element ref="xpdl:FormalParameters"
 *         minOccurs="0"/> <xsd:element ref="xpdl:InputSets" minOccurs="0"/>
 *         <xsd:element ref="xpdl:OutputSets" minOccurs="0"/> <xsd:choice
 *         minOccurs="0"> <xsd:annotation> <xsd:documentation>changes xpdl 1.0
 *         order</xsd:documentation> </xsd:annotation> <xsd:sequence
 *         minOccurs="0"> <xsd:element ref="xpdl:Participants" minOccurs="0"/>
 *         <xsd:element ref="xpdl:Applications" minOccurs="0"/> <xsd:element
 *         ref="xpdl:DataFields" minOccurs="0"/> </xsd:sequence> <xsd:sequence
 *         minOccurs="0"> <xsd:element ref="deprecated:DataFields"
 *         minOccurs="0"/> <xsd:element ref="deprecated:Participants"
 *         minOccurs="0"/> <xsd:element ref="deprecated:Applications"
 *         minOccurs="0"/> </xsd:sequence> </xsd:choice> <xsd:element
 *         ref="xpdl:ActivitySets" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Activities" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Transitions" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ExtendedAttributes" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Assignments" minOccurs="0"/> <xsd:element
 *         ref="xpdl:PartnerLinks" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Object" minOccurs="0"/> <xsd:choice minOccurs="0">
 *         <xsd:sequence> <xsd:element name="Extensions"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> </xsd:choice> </xsd:sequence>
 *         <xsd:attribute name="Id" type="xpdl:Id" use="required">
 *         <xsd:annotation> <xsd:documentation>BPMN: unique identifier for the
 *         process, referenced by Pool</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="Name" type="xsd:string"
 *         use="optional"> <xsd:annotation> <xsd:documentation>BPMN: label of
 *         WorkflowProcess in diagram, should be same as for
 *         Pool</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="AccessLevel" use="optional" default="PUBLIC">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="PUBLIC"/> <xsd:enumeration value="PRIVATE"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="ProcessType" use="optional" default="None"> <xsd:annotation>
 *         <xsd:documentation>BPMN:</xsd:documentation> </xsd:annotation>
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="None"/> <xsd:enumeration value="Private"/>
 *         <xsd:enumeration value="Abstract"/> <xsd:enumeration
 *         value="Collaboration"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:attribute name="Status" use="optional"
 *         default="None"> <xsd:annotation> <xsd:documentation> BPMN: Status
 *         values are assigned during execution. Status can be treated as a
 *         property and used in expressions local to a Process. It is unclear
 *         that status belongs in the XPDL document.</xsd:documentation>
 *         </xsd:annotation> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="None"/> <xsd:enumeration
 *         value="Ready"/> <xsd:enumeration value="Active"/> <xsd:enumeration
 *         value="Cancelled"/> <xsd:enumeration value="Aborting"/>
 *         <xsd:enumeration value="Aborted"/> <xsd:enumeration
 *         value="Completing"/> <xsd:enumeration value="Completed"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="SuppressJoinFailure" type="xsd:boolean" use="optional"
 *         default="false"/> <xsd:attribute name="EnableInstanceCompensation"
 *         type="xsd:boolean" use="optional" default="false"/> <xsd:attribute
 *         name="AdHoc" type="xsd:boolean" use="optional" default="false">
 *         <xsd:annotation> <xsd:documentation>BPMN: for Embedded
 *         subprocess</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="AdHocOrdering" use="optional"
 *         default="Parallel"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="Sequential"/>
 *         <xsd:enumeration value="Parallel"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="AdHocCompletionCondition" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="DefaultStartActivitySetId" type="xpdl:IdRef"
 *         use="optional"/> <xsd:attribute name="DefaultStartActivityId"
 *         type="xpdl:IdRef" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 */
public class XpdlProcessType extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String accessLevel;
	private String processType;
	private String status;
	private String suppressJoinFailure;
	private String enableInstanceCompensation;
	private String adHoc;
	private String adHocOrdering;
	private String adHocCompletionCondition;
	private String defaultStartActivitySetId;
	private String defaultStartActivityId;

	/*
	 * Elements
	 */
	private XpdlProcessHeader processHeader;
	private XpdlRedefinableHeader redefinableHeader;
	private XpdlFormalParameters formalParameters;
	private XpdlInputSets inputSets;
	private XpdlOutputSets outputSets;
	private XpdlParticipants participants;
	private XpdlApplications applications;
	private XpdlDataFields dataFields;
	private XpdlActivitySets activitySets;
	private XpdlActivities activities;
	private XpdlTransitions transitions;
	private XpdlExtendedAttributes extendedAttributes;
	private XpdlAssignments assignments;
	private XpdlPartnerLinks partnerLinks;
	private XpdlObject object;
	private XpdlExtensions extensions;

	public XpdlProcessType(String tag) {
		super(tag);

		accessLevel = null;
		processType = null;
		status = null;
		suppressJoinFailure = null;
		enableInstanceCompensation = null;
		adHoc = null;
		adHocOrdering = null;
		adHocCompletionCondition = null;
		defaultStartActivitySetId = null;
		defaultStartActivityId = null;

		processHeader = null;
		redefinableHeader = null;
		formalParameters = null;
		inputSets = null;
		outputSets = null;
		participants = null;
		applications = null;
		dataFields = null;
		activitySets = null;
		activities = null;
		transitions = null;
		extendedAttributes = null;
		assignments = null;
		partnerLinks = null;
		object = null;
		extensions = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("ProcessHeader")) {
			processHeader = new XpdlProcessHeader("ProcessHeader");
			processHeader.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("RedefinableHeader")) {
			redefinableHeader = new XpdlRedefinableHeader("RedefinableHeader");
			redefinableHeader.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("FormalParameters")) {
			formalParameters = new XpdlFormalParameters("FormalParameters");
			formalParameters.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("InputSets")) {
			inputSets = new XpdlInputSets("InputSets");
			inputSets.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("OutputSets")) {
			outputSets = new XpdlOutputSets("OutputSets");
			outputSets.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Participants")) {
			participants = new XpdlParticipants("Participants");
			participants.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Applications")) {
			applications = new XpdlApplications("Applications");
			applications.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("DataFields")) {
			dataFields = new XpdlDataFields("DataFields");
			dataFields.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ActivitySets")) {
			activitySets = new XpdlActivitySets("ActivitySets");
			activitySets.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Activities")) {
			activities = new XpdlActivities("Activities");
			activities.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Transitions")) {
			transitions = new XpdlTransitions("Transitions");
			transitions.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExtendedAttributes")) {
			extendedAttributes = new XpdlExtendedAttributes("ExtendedAttributes");
			extendedAttributes.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Assignments")) {
			assignments = new XpdlAssignments("Assignments");
			assignments.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("PartnerLinks")) {
			partnerLinks = new XpdlPartnerLinks("PartnerLinks");
			partnerLinks.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Object")) {
			object = new XpdlObject("Object");
			object.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Extensions")) {
			extensions = new XpdlExtensions("Extensions");
			extensions.importElement(xpp, xpdl);
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
		if (processHeader != null) {
			s += processHeader.exportElement();
		}
		if (redefinableHeader != null) {
			s += redefinableHeader.exportElement();
		}
		if (formalParameters != null) {
			s += formalParameters.exportElement();
		}
		if (inputSets != null) {
			s += inputSets.exportElement();
		}
		if (outputSets != null) {
			s += outputSets.exportElement();
		}
		if (participants != null) {
			s += participants.exportElement();
		}
		if (applications != null) {
			s += applications.exportElement();
		}
		if (dataFields != null) {
			s += dataFields.exportElement();
		}
		if (activitySets != null) {
			s += activitySets.exportElement();
		}
		if (activities != null) {
			s += activities.exportElement();
		}
		if (transitions != null) {
			s += transitions.exportElement();
		}
		if (extendedAttributes != null) {
			s += extendedAttributes.exportElement();
		}
		if (assignments != null) {
			s += assignments.exportElement();
		}
		if (partnerLinks != null) {
			s += partnerLinks.exportElement();
		}
		if (object != null) {
			s += object.exportElement();
		}
		if (extensions != null) {
			s += extensions.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "AccessLevel");
		if (value != null) {
			accessLevel = value;
		}
		value = xpp.getAttributeValue(null, "ProcessType");
		if (value != null) {
			processType = value;
		}
		value = xpp.getAttributeValue(null, "Status");
		if (value != null) {
			status = value;
		}
		value = xpp.getAttributeValue(null, "SuppressJoinFailure");
		if (value != null) {
			suppressJoinFailure = value;
		}
		value = xpp.getAttributeValue(null, "EnableInstanceCompensation");
		if (value != null) {
			enableInstanceCompensation = value;
		}
		value = xpp.getAttributeValue(null, "AdHoc");
		if (value != null) {
			adHoc = value;
		}
		value = xpp.getAttributeValue(null, "AdHocOrdering");
		if (value != null) {
			adHocOrdering = value;
		}
		value = xpp.getAttributeValue(null, "AdHocCompletionCondition");
		if (value != null) {
			adHocCompletionCondition = value;
		}
		value = xpp.getAttributeValue(null, "DefaultStartActivitySetId");
		if (value != null) {
			defaultStartActivitySetId = value;
		}
		value = xpp.getAttributeValue(null, "DefaultStartActivityId");
		if (value != null) {
			defaultStartActivityId = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (accessLevel != null) {
			s += exportAttribute("AccessLevel", accessLevel);
		}
		if (processType != null) {
			s += exportAttribute("ProcessType", processType);
		}
		if (status != null) {
			s += exportAttribute("Status", status);
		}
		if (suppressJoinFailure != null) {
			s += exportAttribute("SuppressJoinFailure", suppressJoinFailure);
		}
		if (enableInstanceCompensation != null) {
			s += exportAttribute("EnableInstanceCompensation", enableInstanceCompensation);
		}
		if (adHoc != null) {
			s += exportAttribute("AdHoc", adHoc);
		}
		if (adHocOrdering != null) {
			s += exportAttribute("AdHocOrdering", adHocOrdering);
		}
		if (adHocCompletionCondition != null) {
			s += exportAttribute("AdHocCompletionCondition", adHocCompletionCondition);
		}
		if (defaultStartActivitySetId != null) {
			s += exportAttribute("DefaultStartActivitySetId", defaultStartActivitySetId);
		}
		if (defaultStartActivityId != null) {
			s += exportAttribute("DefaultStartActivityId", defaultStartActivityId);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "AccessLevel", accessLevel, Arrays.asList("PUBLIC", "PRIVATE"), false);
		checkRestriction(xpdl, "ProcessType", processType, Arrays
				.asList("None", "Private", "Abstract", "Collaboration"), false);
		checkRestriction(xpdl, "Status", status, Arrays.asList("None", "Ready", "Active", "Cancelled", "Aborting",
				"Aborted", "Completing", "Completed"), false);
		checkBoolean(xpdl, "SuppressJoinFailure", suppressJoinFailure, false);
		checkBoolean(xpdl, "EnableInstanceCompensation", enableInstanceCompensation, false);
		checkBoolean(xpdl, "AdHoc", adHoc, false);
		checkRestriction(xpdl, "AdHocOrdering", adHocOrdering, Arrays.asList("Sequential", "Parallel"), false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node, XpdlPools pools) {
		
		Swimlane parentPool = retrieveParentPool(bpmn, id2node, pools);
		
		if (activities != null) {
			activities.convertToBpmn(bpmn, parentPool, activitySets, id2node);
		}
		if (transitions != null) {
			transitions.convertToBpmn(bpmn, parentPool, activitySets, id2node);
		}
	}

	public String getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(String accessLevel) {
		this.accessLevel = accessLevel;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSuppressJoinFailure() {
		return suppressJoinFailure;
	}

	public void setSuppressJoinFailure(String suppressJoinFailure) {
		this.suppressJoinFailure = suppressJoinFailure;
	}

	public String getEnableInstanceCompensation() {
		return enableInstanceCompensation;
	}

	public void setEnableInstanceCompensation(String enableInstanceCompensation) {
		this.enableInstanceCompensation = enableInstanceCompensation;
	}

	public String getAdHoc() {
		return adHoc;
	}

	public void setAdHoc(String adHoc) {
		this.adHoc = adHoc;
	}

	public String getAdHocOrdering() {
		return adHocOrdering;
	}

	public void setAdHocOrdering(String adHocOrdering) {
		this.adHocOrdering = adHocOrdering;
	}

	public String getAdHocCompletionCondition() {
		return adHocCompletionCondition;
	}

	public void setAdHocCompletionCondition(String adHocCompletionCondition) {
		this.adHocCompletionCondition = adHocCompletionCondition;
	}

	public String getDefaultStartActivitySetId() {
		return defaultStartActivitySetId;
	}

	public void setDefaultStartActivitySetId(String defaultStartActivitySetId) {
		this.defaultStartActivitySetId = defaultStartActivitySetId;
	}

	public String getDefaultStartActivityId() {
		return defaultStartActivityId;
	}

	public void setDefaultStartActivityId(String defaultStartActivityId) {
		this.defaultStartActivityId = defaultStartActivityId;
	}

	public XpdlProcessHeader getProcessHeader() {
		return processHeader;
	}

	public void setProcessHeader(XpdlProcessHeader processHeader) {
		this.processHeader = processHeader;
	}

	public XpdlRedefinableHeader getRedefinableHeader() {
		return redefinableHeader;
	}

	public void setRedefinableHeader(XpdlRedefinableHeader redefinableHeader) {
		this.redefinableHeader = redefinableHeader;
	}

	public XpdlFormalParameters getFormalParameters() {
		return formalParameters;
	}

	public void setFormalParameters(XpdlFormalParameters formalParameters) {
		this.formalParameters = formalParameters;
	}

	public XpdlInputSets getInputSets() {
		return inputSets;
	}

	public void setInputSets(XpdlInputSets inputSets) {
		this.inputSets = inputSets;
	}

	public XpdlOutputSets getOutputSets() {
		return outputSets;
	}

	public void setOutputSets(XpdlOutputSets outputSets) {
		this.outputSets = outputSets;
	}

	public XpdlParticipants getParticipants() {
		return participants;
	}

	public void setParticipants(XpdlParticipants participants) {
		this.participants = participants;
	}

	public XpdlApplications getApplications() {
		return applications;
	}

	public void setApplications(XpdlApplications applications) {
		this.applications = applications;
	}

	public XpdlDataFields getDataFields() {
		return dataFields;
	}

	public void setDataFields(XpdlDataFields dataFields) {
		this.dataFields = dataFields;
	}

	public XpdlActivitySets getActivitySets() {
		return activitySets;
	}

	public void setActivitySets(XpdlActivitySets activitySets) {
		this.activitySets = activitySets;
	}

	public XpdlActivities getActivities() {
		return activities;
	}

	public void setActivities(XpdlActivities activities) {
		this.activities = activities;
	}

	public XpdlTransitions getTransitions() {
		return transitions;
	}

	public void setTransitions(XpdlTransitions transitions) {
		this.transitions = transitions;
	}

	public XpdlExtendedAttributes getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(XpdlExtendedAttributes extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}

	public XpdlAssignments getAssignments() {
		return assignments;
	}

	public void setAssignments(XpdlAssignments assignments) {
		this.assignments = assignments;
	}

	public XpdlPartnerLinks getPartnerLinks() {
		return partnerLinks;
	}

	public void setPartnerLinks(XpdlPartnerLinks partnerLinks) {
		this.partnerLinks = partnerLinks;
	}

	public XpdlObject getObject() {
		return object;
	}

	public void setObject(XpdlObject object) {
		this.object = object;
	}

	public XpdlExtensions getExtensions() {
		return extensions;
	}

	public void setExtensions(XpdlExtensions extensions) {
		this.extensions = extensions;
	}
	
	private Swimlane retrieveParentPool(BPMNDiagram bpmn, Map<String, BPMNNode> id2node, XpdlPools pools) {
		Swimlane bpmnParentPool = null;
		if(pools != null) {
			for(XpdlPool pool : pools.getList()) {
				if(pool.getProcess().equals(id)) {
					bpmnParentPool = (Swimlane)id2node.get(pool.getId());
				}
			}
		}
		return bpmnParentPool;
	}
}
