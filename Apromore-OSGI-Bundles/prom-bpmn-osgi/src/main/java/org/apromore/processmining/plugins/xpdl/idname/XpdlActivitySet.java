package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Arrays;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActivities;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActivitySets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlArtifacts;
import org.apromore.processmining.plugins.xpdl.collections.XpdlAssociations;
import org.apromore.processmining.plugins.xpdl.collections.XpdlTransitions;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ActivitySet"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Activities" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Transitions" minOccurs="0"/> <xsd:element ref="xpdl:Object"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xpdl:Id" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"> <xsd:annotation> <xsd:documentation
 *         source="added to XPDL 2.0"/> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="AdHoc" type="xsd:boolean" use="optional"
 *         default="false"> <xsd:annotation> <xsd:documentation>BPMN: for
 *         Embedded subprocess</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="AdHocOrdering" use="optional"
 *         default="Parallel"> <xsd:annotation> <xsd:documentation>BPMN: for
 *         Embedded subprocess</xsd:documentation> </xsd:annotation>
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="Sequential"/> <xsd:enumeration
 *         value="Parallel"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:attribute name="AdHocCompletionCondition"
 *         type="xsd:string" use="optional"> <xsd:annotation>
 *         <xsd:documentation>BPMN: for Embedded subprocess</xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:attribute
 *         name="DefaultStartActivityId" type="xpdl:IdRef" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> <xsd:key name="ActivityIds.ActivitySet">
 *         <xsd:selector xpath="./xpdl:Activities/xpdl:Activity"/> <xsd:field
 *         xpath="@Id"/> </xsd:key> <xsd:key name="TransitionIds.ActivitySet">
 *         <xsd:selector xpath="./xpdl:Transitions/xpdl:Transition"/> <xsd:field
 *         xpath="@Id"/> </xsd:key> <xsd:keyref
 *         name="DefaultStartActivityIdRef.ActivitySet"
 *         refer="xpdl:ActivityIds.ActivitySet"> <xsd:selector xpath="."/>
 *         <xsd:field xpath="@DefaultStartActivityId"/> </xsd:keyref>
 *         <xsd:keyref name="TransitionFromRef.ActivitySet"
 *         refer="xpdl:ActivityIds.ActivitySet"> <xsd:selector
 *         xpath="./xpdl:Transitions/xpdl:Transition"/> <xsd:field
 *         xpath="@From"/> </xsd:keyref> <xsd:keyref
 *         name="TransitionToRef.ActivitySet"
 *         refer="xpdl:ActivityIds.ActivitySet"> <xsd:selector
 *         xpath="./xpdl:Transitions/xpdl:Transition"/> <xsd:field xpath="@To"/>
 *         </xsd:keyref> <xsd:keyref name="TransitionRefIdRef.ActivitySet"
 *         refer="xpdl:TransitionIds.ActivitySet"> <xsd:selector xpath=
 *         "./xpdl:Activities/xpdl:Activity/xpdl:TransitionRestrictions/xpdl:TransitionRestriction/xpdl:Split/xpdl:TransitionRefs/xpdl:TransitionRef"
 *         /> <xsd:field xpath="@Id"/> </xsd:keyref> <!-- check that the default
 *         start activity id exists --> <!-- check that the from and to
 *         specified in a transition exists --> <!-- check that the id specified
 *         in a transitionref exists --> </xsd:element>
 */

public class XpdlActivitySet extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String adHoc;
	private String adHocOrdering;
	private String adHocCompletionCondition;
	private String defaultStartActivityId;

	/*
	 * Elements
	 */
	private XpdlAssociations associations;
	private XpdlArtifacts artifacts;
	private XpdlActivities activities;
	private XpdlTransitions transitions;
	private XpdlObject object;

	public XpdlActivitySet(String tag) {
		super(tag);

		/*
		 * Attributes
		 */
		adHoc = null;
		adHocOrdering = null;
		adHocCompletionCondition = null;
		defaultStartActivityId = null;

		/*
		 * Elements
		 */
		associations = null;
		artifacts = null;
		activities = null;
		transitions = null;
		object = null;
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
		if (xpp.getName().equals("Object")) {
			object = new XpdlObject("Object");
			object.importElement(xpp, xpdl);
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
		if (artifacts != null) {
			s += artifacts.exportElement();
		}
		if (associations != null) {
			s += associations.exportElement();
		}
		if (activities != null) {
			s += activities.exportElement();
		}
		if (transitions != null) {
			s += transitions.exportElement();
		}
		if (object != null) {
			s += object.exportElement();
		}
		return s;
	}

	/**
	 * Imports all known attributes.
	 */
	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "AdHoc");
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
		if (adHoc != null) {
			s += exportAttribute("AdHoc", adHoc);
		}
		if (adHocOrdering != null) {
			s += exportAttribute("AdHocOrdering", adHocOrdering);
		}
		if (adHocCompletionCondition != null) {
			s += exportAttribute("AdHocCompletionCondition", adHocCompletionCondition);
		}
		if (defaultStartActivityId != null) {
			s += exportAttribute("DefaultStartActivityId", defaultStartActivityId);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkBoolean(xpdl, "AdHoc", adHoc, false);
		checkRestriction(xpdl, "AdHocOrdering", adHocOrdering, Arrays.asList("Sequential", "Parallel"), false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, SubProcess subProcess, XpdlActivitySets activitySets,
			Map<String, BPMNNode> id2node) {
		if (activities != null) {
			activities.convertToBpmn(bpmn, subProcess, activitySets, id2node);
		}
		if (transitions != null) {
			transitions.convertToBpmn(bpmn, subProcess, activitySets, id2node);
		}
	}

	public boolean hasId(String id) {
		return id.equals(this.id);
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

	public String getDefaultStartActivityId() {
		return defaultStartActivityId;
	}

	public void setDefaultStartActivityId(String defaultStartActivityId) {
		this.defaultStartActivityId = defaultStartActivityId;
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

	public XpdlObject getObject() {
		return object;
	}

	public void setObject(XpdlObject object) {
		this.object = object;
	}
	
	public XpdlArtifacts getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(XpdlArtifacts artifacts) {
		this.artifacts = artifacts;
	}
	
	public XpdlAssociations getAssociations() {
		return associations;
	}

	public void setAssociations(XpdlAssociations associations) {
		this.associations = associations;
	}
}
