package org.apromore.processmining.plugins.xpdl.idname;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="WorkflowProcess" type="xpdl:ProcessType"> <xsd:key
 *         name="ActivitySetIds.WorkflowProcess"> <xsd:selector
 *         xpath="./xpdl:ActivitySets/xpdl:ActivitySet"/> <xsd:field
 *         xpath="@Id"/> </xsd:key> <xsd:key name="ActivityIds.WorkflowProcess">
 *         <xsd:selector xpath="./xpdl:Activities/xpdl:Activity | ./xpdl:ActivitySets/xpdl:ActivitySet/xpdl:Activities/xpdl:Activity"
 *         /> <xsd:field xpath="@Id"/> </xsd:key> <xsd:key
 *         name="ActivityIdsTopLevel.WorkflowProcess"> <xsd:selector
 *         xpath="./xpdl:Activities/xpdl:Activity"/> <xsd:field xpath="@Id"/>
 *         </xsd:key> <xsd:key name="TransitionIdsTopLevel.WorkflowProcess">
 *         <xsd:selector xpath="./xpdl:Transitions/xpdl:Transition"/> <xsd:field
 *         xpath="@Id"/> </xsd:key> <xsd:keyref
 *         name="DefaultStartActivitySetIdRef.WorkflowProcess"
 *         refer="xpdl:ActivitySetIds.WorkflowProcess"> <xsd:selector
 *         xpath="."/> <xsd:field xpath="@DefaultStartActivitySetId"/>
 *         </xsd:keyref> <xsd:keyref
 *         name="DefaultStartActivityIdRef.WorkflowProcess"
 *         refer="xpdl:ActivityIds.WorkflowProcess"> <xsd:selector xpath="."/>
 *         <xsd:field xpath="@DefaultStartActivityId"/> </xsd:keyref>
 *         <xsd:keyref name="BlockActivityActivitySetIdRef.WorkflowProcess"
 *         refer="xpdl:ActivitySetIds.WorkflowProcess"> <xsd:selector
 *         xpath=".//xpdl:BlockActivity"/> <xsd:field xpath="@ActivitySetId"/>
 *         </xsd:keyref> <xsd:keyref
 *         name="BlockActivityStartActivityIdRef.WorkflowProcess"
 *         refer="xpdl:ActivityIds.WorkflowProcess"> <xsd:selector
 *         xpath=".//xpdl:BlockActivity"/> <xsd:field xpath="@StartActivityId"/>
 *         </xsd:keyref> <xsd:keyref name="TransitionFromRef.WorkflowProcess"
 *         refer="xpdl:ActivityIdsTopLevel.WorkflowProcess"> <xsd:selector
 *         xpath="./xpdl:Transitions/xpdl:Transition"/> <xsd:field
 *         xpath="@From"/> </xsd:keyref> <xsd:keyref
 *         name="TransitionToRef.WorkflowProcess"
 *         refer="xpdl:ActivityIdsTopLevel.WorkflowProcess"> <xsd:selector
 *         xpath="./xpdl:Transitions/xpdl:Transition"/> <xsd:field xpath="@To"/>
 *         </xsd:keyref> <xsd:keyref name="TransitionRefIdRef.WorkflowProcess"
 *         refer="xpdl:TransitionIdsTopLevel.WorkflowProcess"> <xsd:selector
 *         xpath="./xpdl:Activities/xpdl:Activity/xpdl:TransitionRestrictions/xpdl:TransitionRestriction/xpdl:Split/xpdl:TransitionRefs/xpdl:TransitionRef"
 *         /> <xsd:field xpath="@Id"/> </xsd:keyref> <!-- constrain to only
 *         activities in the top-level, not activitysets --> <!-- constrain to
 *         only transitions in the top-level, not activitysets --> <!-- check
 *         that specified default start activityset exists --> <!-- check that
 *         specified default start activity exists (note: incomplete test, does
 *         not constrain to optional activtyset specified by
 *         DefaultStartActivitySetId) --> <!-- check that the activityset
 *         specified in a blockactivity exists --> <!-- check that the start
 *         activity specified in a blockactivity exists (note: incomplete test,
 *         does not constrain to activtyset specified by ActivitySetId) --> <!--
 *         check that the from and to specified in a transition exists --> <!--
 *         check that the id specified in a transitionref exists -->
 *         </xsd:element>
 */
public class XpdlWorkflowProcess extends XpdlProcessType {

	public XpdlWorkflowProcess(String tag) {
		super(tag);
	}
}
