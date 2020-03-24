package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerResultCompensation"> <xsd:annotation>
 *         <xsd:documentation> BPMN: Must be present if if Trigger or ResultType
 *         is Compensation. </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="ActivityId" type="xsd:NMTOKEN"
 *         use="optional"> <xsd:annotation> <xsd:documentation> This supplies
 *         the Id of the Activity to be Compensated. Used only for intermediate
 *         events or end events in the seuence flow. Events attached to the
 *         boundary of an activity already know the Id. </xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:attribute
 *         name="WaitForCompletion" type="xsd:boolean" use="optional"
 *         default="true"> <xsd:annotation> <xsd:documentation> The default here
 *         is true which is the default for BPMN 2.0 but the default for BPMN
 *         1.2 is false </xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTriggerResultCompensation extends XpdlElement {

	/*
	 * Attributes
	 */
	private String activityId;

	public XpdlTriggerResultCompensation(String tag) {
		super(tag);

		activityId = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ActivityId");
		if (value != null) {
			activityId = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (activityId != null) {
			s += exportAttribute("ActivityId", activityId);
		}
		return s;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
}
