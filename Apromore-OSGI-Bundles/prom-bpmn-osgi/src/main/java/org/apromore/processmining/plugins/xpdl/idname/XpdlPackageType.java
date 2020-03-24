package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlConformanceClass;
import org.apromore.processmining.plugins.xpdl.XpdlPackageHeader;
import org.apromore.processmining.plugins.xpdl.XpdlRedefinableHeader;
import org.apromore.processmining.plugins.xpdl.XpdlScript;
import org.apromore.processmining.plugins.xpdl.collections.XpdlApplications;
import org.apromore.processmining.plugins.xpdl.collections.XpdlArtifacts;
import org.apromore.processmining.plugins.xpdl.collections.XpdlAssociations;
import org.apromore.processmining.plugins.xpdl.collections.XpdlDataFields;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExternalPackages;
import org.apromore.processmining.plugins.xpdl.collections.XpdlMessageFlows;
import org.apromore.processmining.plugins.xpdl.collections.XpdlPages;
import org.apromore.processmining.plugins.xpdl.collections.XpdlParticipants;
import org.apromore.processmining.plugins.xpdl.collections.XpdlPartnerLinkTypes;
import org.apromore.processmining.plugins.xpdl.collections.XpdlPools;
import org.apromore.processmining.plugins.xpdl.collections.XpdlTypeDeclarations;
import org.apromore.processmining.plugins.xpdl.collections.XpdlWorkflowProcesses;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Package" type="xpdl:PackageType"> <xsd:key
 *         name="ProcessIds.Package"> <xsd:selector
 *         xpath=".//xpdl:WorkflowProcess | .//xpdl:ActivitySet"/> <xsd:field
 *         xpath="@Id"/> </xsd:key> <xsd:keyref name="PoolProcessIdRef.Package"
 *         refer="xpdl:ProcessIds.Package"> <xsd:selector xpath=".//xpdl:Pool"/>
 *         <xsd:field xpath="@Process"/> </xsd:keyref> <xsd:key
 *         name="ProcessIdsTopLevel.Package"> <xsd:selector
 *         xpath=".//xpdl:WorkflowProcess"/> <xsd:field xpath="@Id"/> </xsd:key>
 *         <xsd:keyref name="SubFlowIdRef.Package"
 *         refer="xpdl:ProcessIdsTopLevel.Package"> <xsd:selector
 *         xpath=".//xpdl:SubFlow"/> <xsd:field xpath="@Id"/> </xsd:keyref>
 *         <xsd:key name="ActivitySetIds.Package"> <xsd:selector
 *         xpath=".//xpdl:ActivitySet"/> <xsd:field xpath="@Id"/> </xsd:key>
 *         <xsd:keyref name="SubFlowStartActivitySetIdRef.Package"
 *         refer="xpdl:ActivitySetIds.Package"> <xsd:selector
 *         xpath=".//xpdl:SubFlow"/> <xsd:field xpath="@StartActivitySetId"/>
 *         </xsd:keyref> <xsd:key name="ActivityIds.Package"> <xsd:selector
 *         xpath=".//xpdl:Activity"/> <xsd:field xpath="@Id"/> </xsd:key>
 *         <xsd:keyref name="SubFlowStartActivityIdRef.Package"
 *         refer="xpdl:ActivityIds.Package"> <xsd:selector
 *         xpath=".//xpdl:SubFlow"/> <xsd:field xpath="@StartActivityId"/>
 *         </xsd:keyref> <xsd:keyref name="TaskReferenceTaskRef.Package"
 *         refer="xpdl:ActivityIds.Package"> <xsd:selector
 *         xpath=".//xpdl:TaskReference"/> <xsd:field xpath="@TaskRef"/>
 *         </xsd:keyref> <xsd:key name="LaneIds.Package"> <xsd:selector
 *         xpath=".//xpdl:Lane"/> <xsd:field xpath="@Id"/> </xsd:key>
 *         <xsd:keyref name="NodeGraphicsInfoLaneIdRef.Package"
 *         refer="xpdl:LaneIds.Package"> <xsd:selector
 *         xpath=".//xpdl:NodeGraphicsInfo"/> <xsd:field xpath="@LaneId"/>
 *         </xsd:keyref> <xsd:key name="PageIds.Package"> <xsd:selector
 *         xpath=".//xpdl:Page"/> <xsd:field xpath="@Id"/> </xsd:key>
 *         <xsd:keyref name="GraphicsInfoPageIdRef.Package"
 *         refer="xpdl:PageIds.Package"> <xsd:selector
 *         xpath=".//xpdl:NodeGraphicsInfo | .//xpdl:ConnectorGraphicsInfo"/>
 *         <xsd:field xpath="@PageId"/> </xsd:keyref> <xsd:key
 *         name="PoolAndActivityIds.Package"> <xsd:selector
 *         xpath=".//xpdl:Pool | .//xpdl:Activity"/> <xsd:field xpath="@Id"/>
 *         </xsd:key> <xsd:keyref name="MessageFlowSourceRef.Package"
 *         refer="xpdl:PoolAndActivityIds.Package"> <xsd:selector
 *         xpath=".//xpdl:MessageFlow"/> <xsd:field xpath="@Source"/>
 *         </xsd:keyref> <xsd:keyref name="MessageFlowTargetRef.Package"
 *         refer="xpdl:PoolAndActivityIds.Package"> <xsd:selector
 *         xpath=".//xpdl:MessageFlow"/> <xsd:field xpath="@Target"/>
 *         </xsd:keyref> <!-- checks that process id referred to by pool exists
 *         --> <!-- checks that process id referred to by subflow exists (must
 *         be top-level, not an activityset) --> <!-- checks that start
 *         activityset referred to by subflow exists (note: incomplete test,
 *         does not constrain to process specified by subflow) --> <!-- checks
 *         that start activity referred to by subflow exists (note: incomplete
 *         test, does not constrain to process specified by subflow) --> <!--
 *         checks that activity referred to by taskreference exists (note: may
 *         be incomplete test, does not constrain to same process that contains
 *         the task) --> <!-- checks that lane id referred to by
 *         nodegraphicsinfo exists --> <!-- checks that page id referred to by
 *         grahicsinfo exists --> <!-- checks that source and target referred to
 *         by messageflow exists (note: incomplete test, does check that
 *         source/target are, or are in, separate pools) --> </xsd:element>
 *         <xsd:complexType name="PackageType"> <xsd:sequence> <xsd:element
 *         ref="xpdl:PackageHeader"/> <xsd:element ref="xpdl:RedefinableHeader"
 *         minOccurs="0"/> <xsd:element ref="xpdl:ConformanceClass"
 *         minOccurs="0"/> <xsd:element ref="xpdl:Script" minOccurs="0"/>
 *         <xsd:element ref="xpdl:ExternalPackages" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TypeDeclarations" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Participants" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Applications" minOccurs="0"/> <xsd:element
 *         ref="xpdl:DataFields" minOccurs="0"/> <xsd:element
 *         ref="xpdl:PartnerLinkTypes" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Pages" minOccurs="0"/> <xsd:element ref="xpdl:Pools"
 *         minOccurs="0"/> <xsd:element ref="xpdl:MessageFlows" minOccurs="0"/>
 *         <xsd:element ref="xpdl:Associations" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Artifacts" minOccurs="0"/> <xsd:element
 *         ref="xpdl:WorkflowProcesses" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ExtendedAttributes" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xpdl:Id" use="required"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Corresponds to BPD identifier. Target of @DiagramRef
 *         in Subflow.</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="Name" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="Language" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="QueryLanguage" type="xsd:string"
 *         use="optional"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType>
 */
public class XpdlPackageType extends XpdlIdName {

	/*
	 * Constants 
	 */
	private static final String XPDL2_2_NAMESPACE = "http://www.wfmc.org/2009/XPDL2.2";
	private static final String XPDL2_2_SCHEMA_LOCATION = XPDL2_2_NAMESPACE 
			+ " http://www.xpdl.org/standards/xpdl-2.2/bpmnxpdl_40a.xsd";
	private static final String XSI_PREFIX = "xsi";
	private static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
	/*
	 * Attributes
	 */
	private String language;
	private String queryLanguage;

	/*
	 * Elements
	 */
	private XpdlPackageHeader packageHeader;
	private XpdlRedefinableHeader redefinableHeader;
	private XpdlConformanceClass conformanceClass;
	private XpdlScript script;
	private XpdlExternalPackages externalPackages;
	private XpdlTypeDeclarations typeDeclarations;
	private XpdlParticipants participants;
	private XpdlApplications applications;
	private XpdlDataFields dataFields;
	private XpdlPartnerLinkTypes partnerLinkTypes;
	private XpdlPages pages;
	private XpdlPools pools;
	private XpdlMessageFlows messageFlows;
	private XpdlAssociations associations;
	private XpdlArtifacts artifacts;
	private XpdlWorkflowProcesses workflowProcesses;
	private XpdlExtendedAttributes extendedAttributes;

	public XpdlPackageType(String tag) {
		super(tag);

		language = null;
		queryLanguage = null;

		packageHeader = null;
		redefinableHeader = null;
		conformanceClass = null;
		script = null;
		externalPackages = null;
		typeDeclarations = null;
		participants = null;
		applications = null;
		dataFields = null;
		partnerLinkTypes = null;
		pages = null;
		pools = null;
		messageFlows = null;
		associations = null;
		artifacts = null;
		workflowProcesses = null;
		extendedAttributes = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("PackageHeader")) {
			packageHeader = new XpdlPackageHeader("PackageHeader");
			packageHeader.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("RedefinableHeader")) {
			redefinableHeader = new XpdlRedefinableHeader("RedefinableHeader");
			redefinableHeader.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ConformanceClass")) {
			conformanceClass = new XpdlConformanceClass("ConformanceClass");
			conformanceClass.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Script")) {
			script = new XpdlScript("Script");
			script.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExternalPackages")) {
			externalPackages = new XpdlExternalPackages("ExternalPackages");
			externalPackages.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TypeDeclarations")) {
			typeDeclarations = new XpdlTypeDeclarations("TypeDeclarations");
			typeDeclarations.importElement(xpp, xpdl);
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
		if (xpp.getName().equals("PartnerLinkTypes")) {
			partnerLinkTypes = new XpdlPartnerLinkTypes("PartnerLinkTypes");
			partnerLinkTypes.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Pages")) {
			pages = new XpdlPages("Pages");
			pages.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Pools")) {
			pools = new XpdlPools("Pools");
			pools.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("MessageFlows")) {
			messageFlows = new XpdlMessageFlows("MessageFlows");
			messageFlows.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Associations")) {
			associations = new XpdlAssociations("Associations");
			associations.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Artifacts")) {
			artifacts = new XpdlArtifacts("Artifacts");
			artifacts.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("WorkflowProcesses")) {
			workflowProcesses = new XpdlWorkflowProcesses("WorkflowProcesses");
			workflowProcesses.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExtendedAttributes")) {
			extendedAttributes = new XpdlExtendedAttributes("ExtendedAttributes");
			extendedAttributes.importElement(xpp, xpdl);
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
		if (packageHeader != null) {
			s += packageHeader.exportElement();
		}
		if (redefinableHeader != null) {
			s += redefinableHeader.exportElement();
		}
		if (conformanceClass != null) {
			s += conformanceClass.exportElement();
		}
		if (script != null) {
			s += script.exportElement();
		}
		if (externalPackages != null) {
			s += externalPackages.exportElement();
		}
		if (typeDeclarations != null) {
			s += typeDeclarations.exportElement();
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
		if (partnerLinkTypes != null) {
			s += partnerLinkTypes.exportElement();
		}
		if (pages != null) {
			s += pages.exportElement();
		}
		if (pools != null) {
			s += pools.exportElement();
		}
		if (messageFlows != null) {
			s += messageFlows.exportElement();
		}
		if (associations != null) {
			s += associations.exportElement();
		}
		if (artifacts != null) {
			s += artifacts.exportElement();
		}
		if (workflowProcesses != null) {
			s += workflowProcesses.exportElement();
		}
		if (extendedAttributes != null) {
			s += extendedAttributes.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Language");
		if (value != null) {
			language = value;
		}
		value = xpp.getAttributeValue(null, "QueryLanguage");
		if (value != null) {
			queryLanguage = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (language != null) {
			s += exportAttribute("Language", language);
		}
		if (queryLanguage != null) {
			s += exportAttribute("QueryLanguage", queryLanguage);
		}
		s += exportAttribute("xmlns", XPDL2_2_NAMESPACE);
		s += exportAttribute("xmlns:"+ XSI_PREFIX, XSI_NAMESPACE);
		s += exportAttribute(XSI_PREFIX + ":schemaLocation", XPDL2_2_SCHEMA_LOCATION);
		
		return s;
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		if (pools != null) {
			pools.convertToBpmn(bpmn, id2node);
		}
		if (workflowProcesses != null) {
			workflowProcesses.convertToBpmn(bpmn, id2node, pools);
		}
		if (messageFlows != null) {
			messageFlows.convertToBpmn(bpmn, id2node);
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getQueryLanguage() {
		return queryLanguage;
	}

	public void setQueryLanguage(String queryLanguage) {
		this.queryLanguage = queryLanguage;
	}

	public XpdlPackageHeader getPackageHeader() {
		return packageHeader;
	}

	public void setPackageHeader(XpdlPackageHeader packageHeader) {
		this.packageHeader = packageHeader;
	}

	public XpdlRedefinableHeader getRedefinableHeader() {
		return redefinableHeader;
	}

	public void setRedefinableHeader(XpdlRedefinableHeader redefinableHeader) {
		this.redefinableHeader = redefinableHeader;
	}

	public XpdlConformanceClass getConformanceClass() {
		return conformanceClass;
	}

	public void setConformanceClass(XpdlConformanceClass conformanceClass) {
		this.conformanceClass = conformanceClass;
	}

	public XpdlScript getScript() {
		return script;
	}

	public void setScript(XpdlScript script) {
		this.script = script;
	}

	public XpdlExternalPackages getExternalPackages() {
		return externalPackages;
	}

	public void setExternalPackages(XpdlExternalPackages externalPackages) {
		this.externalPackages = externalPackages;
	}

	public XpdlTypeDeclarations getTypeDeclarations() {
		return typeDeclarations;
	}

	public void setTypeDeclarations(XpdlTypeDeclarations typeDeclarations) {
		this.typeDeclarations = typeDeclarations;
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

	public XpdlPartnerLinkTypes getPartnerLinkTypes() {
		return partnerLinkTypes;
	}

	public void setPartnerLinkTypes(XpdlPartnerLinkTypes partnerLinkTypes) {
		this.partnerLinkTypes = partnerLinkTypes;
	}

	public XpdlPages getPages() {
		return pages;
	}

	public void setPages(XpdlPages pages) {
		this.pages = pages;
	}

	public XpdlPools getPools() {
		return pools;
	}

	public void setPools(XpdlPools pools) {
		this.pools = pools;
	}

	public XpdlMessageFlows getMessageFlows() {
		return messageFlows;
	}

	public void setMessageFlows(XpdlMessageFlows messageFlows) {
		this.messageFlows = messageFlows;
	}

	public XpdlAssociations getAssociations() {
		return associations;
	}

	public void setAssociations(XpdlAssociations associations) {
		this.associations = associations;
	}

	public XpdlArtifacts getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(XpdlArtifacts artifacts) {
		this.artifacts = artifacts;
	}

	public XpdlWorkflowProcesses getWorkflowProcesses() {
		return workflowProcesses;
	}

	public void setWorkflowProcesses(XpdlWorkflowProcesses workflowProcesses) {
		this.workflowProcesses = workflowProcesses;
	}

	public XpdlExtendedAttributes getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(XpdlExtendedAttributes extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}
}
