package org.apromore.processmining.plugins.xpdl.idname;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlBlockActivity;
import org.apromore.processmining.plugins.xpdl.XpdlDeadline;
import org.apromore.processmining.plugins.xpdl.XpdlEvent;
import org.apromore.processmining.plugins.xpdl.XpdlIORules;
import org.apromore.processmining.plugins.xpdl.XpdlIcon;
import org.apromore.processmining.plugins.xpdl.XpdlImplementation;
import org.apromore.processmining.plugins.xpdl.XpdlLoop;
import org.apromore.processmining.plugins.xpdl.XpdlRoute;
import org.apromore.processmining.plugins.xpdl.XpdlSimulationInformation;
import org.apromore.processmining.plugins.xpdl.XpdlTransaction;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActivitySets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlAssignments;
import org.apromore.processmining.plugins.xpdl.collections.XpdlDataFields;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.collections.XpdlInputSets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlOutputSets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlPerformers;
import org.apromore.processmining.plugins.xpdl.collections.XpdlTransitionRestrictions;
import org.apromore.processmining.plugins.xpdl.deprecated.XpdlFinishMode;
import org.apromore.processmining.plugins.xpdl.deprecated.XpdlStartMode;
import org.apromore.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.apromore.processmining.plugins.xpdl.text.XpdlDocumentation;
import org.apromore.processmining.plugins.xpdl.text.XpdlLimit;
import org.apromore.processmining.plugins.xpdl.text.XpdlPerformer;
import org.apromore.processmining.plugins.xpdl.text.XpdlPriority;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Activity"> <xsd:annotation>
 *         <xsd:documentation>BPMN extension</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:element
 *         ref="xpdl:Description" minOccurs="0"/> <xsd:element ref="xpdl:Limit"
 *         minOccurs="0"/> <xsd:choice minOccurs="0"> <xsd:element
 *         ref="xpdl:Route"/> <xsd:element ref="xpdl:Implementation">
 *         <xsd:annotation> <xsd:documentation>BPMN: corresponds to an activity,
 *         which could be a task or subprocess.[Suggest change element to
 *         BpmnActivity, since there is an attribute Implementation which means
 *         something else entirely.]</xsd:documentation> </xsd:annotation>
 *         </xsd:element> <xsd:choice minOccurs="0"> <xsd:element
 *         ref="deprecated:BlockActivity"/> <xsd:element
 *         ref="xpdl:BlockActivity"/> </xsd:choice> <xsd:element
 *         ref="xpdl:Event"> <xsd:annotation> <xsd:documentation>BPMN:
 *         Identifies XPDL activity as a BPMN event.</xsd:documentation>
 *         </xsd:annotation> </xsd:element> </xsd:choice> <xsd:element
 *         ref="xpdl:Transaction" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Performers" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Performer" minOccurs="0"> <xsd:annotation>
 *         <xsd:documentation>Deprecated from XPDL2.0. Must be a child of
 *         Performers</xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:element ref="deprecated:StartMode" minOccurs="0"/> <xsd:element
 *         ref="deprecated:FinishMode" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Priority" minOccurs="0"/> <xsd:choice minOccurs="0">
 *         <xsd:element ref="deprecated:Deadline" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:element ref="xpdl:Deadline"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:choice> <xsd:element
 *         ref="xpdl:SimulationInformation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Icon" minOccurs="0"/> <xsd:element ref="xpdl:Documentation"
 *         minOccurs="0"/> <xsd:element ref="xpdl:TransitionRestrictions"
 *         minOccurs="0"/> <xsd:element ref="xpdl:ExtendedAttributes"
 *         minOccurs="0"/> <xsd:element ref="xpdl:DataFields" minOccurs="0"/>
 *         <xsd:element ref="xpdl:InputSets" minOccurs="0"/> <xsd:element
 *         ref="xpdl:OutputSets" minOccurs="0"/> <xsd:element ref="xpdl:IORules"
 *         minOccurs="0"/> <xsd:element ref="xpdl:Loop" minOccurs="0"/>
 *         <xsd:element ref="xpdl:Assignments" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Object" minOccurs="0"/> <xsd:element
 *         ref="xpdl:NodeGraphicsInfos" minOccurs="0"/> <xsd:choice
 *         minOccurs="0"> <xsd:sequence> <xsd:element name="Extensions"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> </xsd:choice> </xsd:sequence>
 *         <xsd:attribute name="Id" type="xpdl:Id" use="required">
 *         <xsd:annotation> <xsd:documentation>BPMN: unique identifier of the
 *         flow object</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="IsForCompensation" type="xsd:boolean"
 *         use="optional"/> <xsd:attribute name="Name" type="xsd:string"
 *         use="optional"> <xsd:annotation> <xsd:documentation>BPMN: label of
 *         the flow object in the diagram</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="StartActivity"
 *         type="xsd:boolean" use="optional"> <xsd:annotation>
 *         <xsd:documentation> Designates the first activity to be executed when
 *         the process is instantiated. Used when there is no other way to
 *         determine this Conflicts with BPMN StartEvent and no process
 *         definition should use both.</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="Status" use="optional"
 *         default="None"> <xsd:annotation> <xsd:documentation> BPMN: Status
 *         values are assigned during execution. Status can be treated as a
 *         property and used in expressions local to an Activity. It is unclear
 *         that status belongs in the XPDL document.</xsd:documentation>
 *         </xsd:annotation> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="None"/> <xsd:enumeration
 *         value="Ready"/> <xsd:enumeration value="Active"/> <xsd:enumeration
 *         value="Cancelled"/> <xsd:enumeration value="Aborting"/>
 *         <xsd:enumeration value="Aborted"/> <xsd:enumeration
 *         value="Completing"/> <xsd:enumeration value="Completed"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="StartMode"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="Automatic"/>
 *         <xsd:enumeration value="Manual"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute name="FinishMode">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="Automatic"/> <xsd:enumeration
 *         value="Manual"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:attribute name="StartQuantity"
 *         type="xsd:integer" use="optional" default="1"/> <xsd:attribute
 *         name="CompletionQuantity" type="xsd:integer" use="optional"
 *         default="1"/> <xsd:attribute name="IsATransaction" type="xsd:boolean"
 *         use="optional" default="false"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlActivity extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String isForCompensation;
	private String startActivity;
	private String status;
	private String startMode;
	private String finishMode;
	private String startQuantity;
	private String completionQuantity;
	private String isATransaction;

	/*
	 * Elements
	 */
	private XpdlDescription description;
	private XpdlLimit limit;
	private XpdlRoute route;
	private XpdlImplementation implementation;
	private XpdlBlockActivity blockActivity;
	private XpdlEvent event;
	private XpdlTransaction transaction;
	private XpdlPerformers performers;
	private XpdlPerformer performerDeprecated;
	private XpdlStartMode startModeDeprecated;
	private XpdlFinishMode finishModeDeprecated;
	private XpdlPriority priority;
	private final List<XpdlDeadline> deadlineList;
	private XpdlSimulationInformation simulationInformation;
	private XpdlIcon icon;
	private XpdlDocumentation documentation;
	private XpdlTransitionRestrictions transitionRestrictions;
	private XpdlExtendedAttributes extendedAttributes;
	private XpdlDataFields dataFields;
	private XpdlInputSets inputSets;
	private XpdlOutputSets outputSets;
	private XpdlIORules ioRules;
	private XpdlLoop loop;
	private XpdlAssignments assignments;
	private XpdlObject object;
	private XpdlNodeGraphicsInfos nodeGraphicsInfos;

	public XpdlActivity(String tag) {
		super(tag);

		/*
		 * Attributes
		 */
		isForCompensation = null;
		startActivity = null;
		status = null;
		startMode = null;
		finishMode = null;
		startQuantity = null;
		completionQuantity = null;
		isATransaction = null;

		/*
		 * Elements
		 */
		description = null;
		limit = null;
		route = null;
		implementation = null;
		blockActivity = null;
		event = null;
		transaction = null;
		performers = null;
		performerDeprecated = null;
		startModeDeprecated = null;
		finishModeDeprecated = null;
		priority = null;
		deadlineList = new ArrayList<XpdlDeadline>();
		simulationInformation = null;
		icon = null;
		documentation = null;
		transitionRestrictions = null;
		extendedAttributes = null;
		dataFields = null;
		inputSets = null;
		outputSets = null;
		ioRules = null;
		loop = null;
		assignments = null;
		object = null;
		nodeGraphicsInfos = null;
	}

	/**
	 * Checks whether the current start tag is known. If known, it imports the
	 * corresponding child element and returns true. Otherwise, it returns
	 * false.
	 * 
	 * @return Whether the start tag was known.
	 */
	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Limit")) {
			limit = new XpdlLimit("Limit");
			limit.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Route")) {
			route = new XpdlRoute("Route");
			route.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Implementation")) {
			implementation = new XpdlImplementation("Implementation");
			implementation.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("BlockActivity")) {
			blockActivity = new XpdlBlockActivity("BlockActivity");
			blockActivity.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Event")) {
			event = new XpdlEvent("Event");
			event.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Transaction")) {
			transaction = new XpdlTransaction("Transaction");
			transaction.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Performers")) {
			performers = new XpdlPerformers("Performers");
			performers.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Performer")) {
			performerDeprecated = new XpdlPerformer("Performer");
			performerDeprecated.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("StartMode")) {
			startModeDeprecated = new XpdlStartMode("StartMode");
			startModeDeprecated.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("FinishMode")) {
			finishModeDeprecated = new XpdlFinishMode("FinishMode");
			finishModeDeprecated.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Priority")) {
			priority = new XpdlPriority("Priority");
			priority.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Deadline")) {
			XpdlDeadline deadline = new XpdlDeadline("Deadline");
			deadline.importElement(xpp, xpdl);
			deadlineList.add(deadline);
			return true;
		}
		if (xpp.getName().equals("SimulationInformation")) {
			simulationInformation = new XpdlSimulationInformation("SimulationInformation");
			simulationInformation.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Icon")) {
			icon = new XpdlIcon("Icon");
			icon.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Documentation")) {
			documentation = new XpdlDocumentation("Documentation");
			documentation.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TransitionRestrictions")) {
			transitionRestrictions = new XpdlTransitionRestrictions("TransitionRestrictions");
			transitionRestrictions.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExtendedAttributes")) {
			extendedAttributes = new XpdlExtendedAttributes("ExtendedAttributes");
			extendedAttributes.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("DataFields")) {
			dataFields = new XpdlDataFields("DataFields");
			dataFields.importElement(xpp, xpdl);
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
		if (xpp.getName().equals("IORules")) {
			ioRules = new XpdlIORules("IORules");
			ioRules.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Loop")) {
			loop = new XpdlLoop("Loop");
			loop.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Assignments")) {
			assignments = new XpdlAssignments("Assignments");
			assignments.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Object")) {
			object = new XpdlObject("Object");
			object.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("NodeGraphicsInfos")) {
			nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");
			nodeGraphicsInfos.importElement(xpp, xpdl);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}

	/**
	 * Exports all child elements.
	 */
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if (description != null) {
			s += description.exportElement();
		}
		if (limit != null) {
			s += limit.exportElement();
		}
		if (route != null) {
			s += route.exportElement();
		}
		if (implementation != null) {
			s += implementation.exportElement();
		}
		if (blockActivity != null) {
			s += blockActivity.exportElement();
		}
		if (event != null) {
			s += event.exportElement();
		}
		if (transaction != null) {
			s += transaction.exportElement();
		}
		if (performers != null) {
			s += performers.exportElement();
		}
		if (performerDeprecated != null) {
			s += performerDeprecated.exportElement();
		}
		if (startModeDeprecated != null) {
			s += startModeDeprecated.exportElement();
		}
		if (finishModeDeprecated != null) {
			s += finishModeDeprecated.exportElement();
		}
		if (priority != null) {
			s += priority.exportElement();
		}
		for (XpdlDeadline deadline : deadlineList) {
			s += deadline.exportElement();
		}
		if (simulationInformation != null) {
			s += simulationInformation.exportElement();
		}
		if (icon != null) {
			s += icon.exportElement();
		}
		if (documentation != null) {
			s += documentation.exportElement();
		}
		if (transitionRestrictions != null) {
			s += transitionRestrictions.exportElement();
		}
		if (extendedAttributes != null) {
			s += extendedAttributes.exportElement();
		}
		if (dataFields != null) {
			s += dataFields.exportElement();
		}
		if (inputSets != null) {
			s += inputSets.exportElement();
		}
		if (outputSets != null) {
			s += outputSets.exportElement();
		}
		if (ioRules != null) {
			s += ioRules.exportElement();
		}
		if (loop != null) {
			s += loop.exportElement();
		}
		if (assignments != null) {
			s += assignments.exportElement();
		}
		if (object != null) {
			s += object.exportElement();
		}
		if (nodeGraphicsInfos != null) {
			s += nodeGraphicsInfos.exportElement();
		}
		return s;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "IsForCompensation");
		if (value != null) {
			isForCompensation = value;
		}
		value = xpp.getAttributeValue(null, "StartActivity");
		if (value != null) {
			startActivity = value;
		}
		value = xpp.getAttributeValue(null, "Status");
		if (value != null) {
			status = value;
		}
		value = xpp.getAttributeValue(null, "StartMode");
		if (value != null) {
			startMode = value;
		}
		value = xpp.getAttributeValue(null, "FinishMode");
		if (value != null) {
			finishMode = value;
		}
		value = xpp.getAttributeValue(null, "StartQuantity");
		if (value != null) {
			startQuantity = value;
		}
		value = xpp.getAttributeValue(null, "CompletionQuantity");
		if (value != null) {
			completionQuantity = value;
		}
		value = xpp.getAttributeValue(null, "IsATransaction");
		if (value != null) {
			isATransaction = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (isForCompensation != null) {
			s += exportAttribute("IsForCompensation", isForCompensation);
		}
		if (startActivity != null) {
			s += exportAttribute("StartActivity", startActivity);
		}
		if (status != null) {
			s += exportAttribute("Status", status);
		}
		if (startMode != null) {
			s += exportAttribute("StartMode", startMode);
		}
		if (finishMode != null) {
			s += exportAttribute("FinishMode", finishMode);
		}
		if (startQuantity != null) {
			s += exportAttribute("StartQuantity", startQuantity);
		}
		if (completionQuantity != null) {
			s += exportAttribute("CompletionQuantity", completionQuantity);
		}
		if (isATransaction != null) {
			s += exportAttribute("IsATransaction", isATransaction);
		}
		return s;
	}

	protected void CheckValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		/*
		 * Check attributes
		 */
		checkBoolean(xpdl, "IsForCompensation", isForCompensation, false);
		checkBoolean(xpdl, "StartActivity", startActivity, false);
		checkRestriction(
				xpdl,
				"Status",
				status,
				Arrays.asList("None", "Ready", "Active", "Cancelled", "Aborting", "Aborted", "Completing", "Completed"),
				false);
		checkRestriction(xpdl, "StartMode", startMode, Arrays.asList("Automatic", "Manual"), false);
		checkRestriction(xpdl, "FinishMode", finishMode, Arrays.asList("Automatic", "Manual"), false);

		/*
		 * Check elements
		 */
		int n = (route != null ? 1 : 0) + (implementation != null ? 1 : 0) + (blockActivity != null ? 1 : 0)
				+ (event != null ? 1 : 0);
		if (n > 1) {
			xpdl.log(tag, lineNumber, "Route, Implementation, BlockActivity, and Event are mutually exclusive");
		}
		if (performerDeprecated != null) {
			xpdl.logInfo(tag, lineNumber, "Is deprecated, move it into Performers");
		}
		if (startModeDeprecated != null) {
			xpdl.logInfo(tag, lineNumber, "Is deprecated, use StartMode attribute");
		}
		if (finishModeDeprecated != null) {
			xpdl.logInfo(tag, lineNumber, "Is deprecated, use FinishMode attribute");
		}
	}

	public void convertToBpmn(BPMNDiagram bpmn, DirectedGraphNode parent, XpdlActivitySets activitySets,
			Map<String, BPMNNode> id2node) {
		String name = (this.name != null ? this.name : "");

		// If parent is a Subprocess
		if (parent instanceof SubProcess) {
			if (event != null) {
				event.convertToBpmn(bpmn, id, name, parent, id2node);
			} else if (route != null) {
				route.convertToBpmn(bpmn, id, name, parent, id2node);
			} else {
				if (blockActivity != null) {
					blockActivity.convertToBpmn(bpmn, id, name, parent, activitySets, id2node, loop);
				} else {
					id2node.put(id, bpmn.addActivity(name, (loop != null) && loop.hasType("Standard"), false, false,
							(loop != null) && loop.hasType("MultiInstance"), false, (SubProcess) parent));
				}
			}
		} else {
			// If parent is a Swimlane
			Swimlane laneParent = null;
			if (this.getNodeGraphicsInfos() != null && this.getNodeGraphicsInfos().getList() != null
					&& this.getNodeGraphicsInfos().getList().size() > 0
					&& this.getNodeGraphicsInfos().getList().get(0).getLaneId() != null) {
				laneParent = (Swimlane) id2node.get(this.getNodeGraphicsInfos().getList().get(0).getLaneId());
			}
			// Lane parent exists
			if (laneParent != null) {
				parent = laneParent;
			}
			if (event != null) {
				event.convertToBpmn(bpmn, id, name, parent, id2node);
			} else if (route != null) {
				route.convertToBpmn(bpmn, id, name, parent, id2node);
			} else {
				if (blockActivity != null) {
					blockActivity.convertToBpmn(bpmn, id, name, parent, activitySets, id2node, loop);
				} else {
					if (parent != null) {
						id2node.put(id, bpmn.addActivity(name, (loop != null) && loop.hasType("Standard"), false, false,
							(loop != null) && loop.hasType("MultiInstance"), false, (Swimlane) parent));
					} else {
						id2node.put(id, bpmn.addActivity(name, (loop != null) && loop.hasType("Standard"), false, false,
								(loop != null) && loop.hasType("MultiInstance"), false));
					}
				}
			}
		}

	}

	public String getIsForCompensation() {
		return isForCompensation;
	}

	public void setIsForCompensation(String isForCompensation) {
		this.isForCompensation = isForCompensation;
	}

	public String getStartActivity() {
		return startActivity;
	}

	public void setStartActivity(String startActivity) {
		this.startActivity = startActivity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStartMode() {
		return startMode;
	}

	public void setStartMode(String startMode) {
		this.startMode = startMode;
	}

	public String getFinishMode() {
		return finishMode;
	}

	public void setFinishMode(String finishMode) {
		this.finishMode = finishMode;
	}

	public String getStartQuantity() {
		return startQuantity;
	}

	public void setStartQuantity(String startQuantity) {
		this.startQuantity = startQuantity;
	}

	public String getCompletionQuantity() {
		return completionQuantity;
	}

	public void setCompletionQuantity(String completionQuantity) {
		this.completionQuantity = completionQuantity;
	}

	public String getIsATransaction() {
		return isATransaction;
	}

	public void setIsATransaction(String isATransaction) {
		this.isATransaction = isATransaction;
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}

	public XpdlLimit getLimit() {
		return limit;
	}

	public void setLimit(XpdlLimit limit) {
		this.limit = limit;
	}

	public XpdlRoute getRoute() {
		return route;
	}

	public void setRoute(XpdlRoute route) {
		this.route = route;
	}

	public XpdlImplementation getImplementation() {
		return implementation;
	}

	public void setImplementation(XpdlImplementation implementation) {
		this.implementation = implementation;
	}

	public XpdlBlockActivity getBlockActivity() {
		return blockActivity;
	}

	public void setBlockActivity(XpdlBlockActivity blockActivity) {
		this.blockActivity = blockActivity;
	}

	public XpdlEvent getEvent() {
		return event;
	}

	public void setEvent(XpdlEvent event) {
		this.event = event;
	}

	public XpdlTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(XpdlTransaction transaction) {
		this.transaction = transaction;
	}

	public XpdlPerformers getPerformers() {
		return performers;
	}

	public void setPerformers(XpdlPerformers performers) {
		this.performers = performers;
	}

	public XpdlPerformer getPerformerDeprecated() {
		return performerDeprecated;
	}

	public void setPerformerDeprecated(XpdlPerformer performerDeprecated) {
		this.performerDeprecated = performerDeprecated;
	}

	public XpdlStartMode getStartModeDeprecated() {
		return startModeDeprecated;
	}

	public void setStartModeDeprecated(XpdlStartMode startModeDeprecated) {
		this.startModeDeprecated = startModeDeprecated;
	}

	public XpdlFinishMode getFinishModeDeprecated() {
		return finishModeDeprecated;
	}

	public void setFinishModeDeprecated(XpdlFinishMode finishModeDeprecated) {
		this.finishModeDeprecated = finishModeDeprecated;
	}

	public XpdlPriority getPriority() {
		return priority;
	}

	public void setPriority(XpdlPriority priority) {
		this.priority = priority;
	}

	public XpdlSimulationInformation getSimulationInformation() {
		return simulationInformation;
	}

	public void setSimulationInformation(XpdlSimulationInformation simulationInformation) {
		this.simulationInformation = simulationInformation;
	}

	public XpdlIcon getIcon() {
		return icon;
	}

	public void setIcon(XpdlIcon icon) {
		this.icon = icon;
	}

	public XpdlDocumentation getDocumentation() {
		return documentation;
	}

	public void setDocumentation(XpdlDocumentation documentation) {
		this.documentation = documentation;
	}

	public XpdlTransitionRestrictions getTransitionRestrictions() {
		return transitionRestrictions;
	}

	public void setTransitionRestrictions(XpdlTransitionRestrictions transitionRestrictions) {
		this.transitionRestrictions = transitionRestrictions;
	}

	public XpdlExtendedAttributes getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(XpdlExtendedAttributes extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}

	public XpdlDataFields getDataFields() {
		return dataFields;
	}

	public void setDataFields(XpdlDataFields dataFields) {
		this.dataFields = dataFields;
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

	public XpdlIORules getIoRules() {
		return ioRules;
	}

	public void setIoRules(XpdlIORules ioRules) {
		this.ioRules = ioRules;
	}

	public XpdlLoop getLoop() {
		return loop;
	}

	public void setLoop(XpdlLoop loop) {
		this.loop = loop;
	}

	public XpdlAssignments getAssignments() {
		return assignments;
	}

	public void setAssignments(XpdlAssignments assignments) {
		this.assignments = assignments;
	}

	public XpdlObject getObject() {
		return object;
	}

	public void setObject(XpdlObject object) {
		this.object = object;
	}

	public XpdlNodeGraphicsInfos getNodeGraphicsInfos() {
		return nodeGraphicsInfos;
	}

	public void setNodeGraphicsInfos(XpdlNodeGraphicsInfos nodeGraphicsInfos) {
		this.nodeGraphicsInfos = nodeGraphicsInfos;
	}

	public List<XpdlDeadline> getDeadlineList() {
		return deadlineList;
	}
}
