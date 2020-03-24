package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.text.XpdlDuration;
import org.apromore.processmining.plugins.xpdl.text.XpdlWaitingTime;
import org.apromore.processmining.plugins.xpdl.text.XpdlWorkingTime;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TimeEstimation"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:WaitingTime" minOccurs="0"/> <xsd:element
 *         ref="xpdl:WorkingTime" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Duration" minOccurs="0"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTimeEstimation extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlWaitingTime waitingTime;
	private XpdlWorkingTime workingTime;
	private XpdlDuration duration;

	public XpdlTimeEstimation(String tag) {
		super(tag);

		waitingTime = null;
		workingTime = null;
		duration = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("WaitingTime")) {
			waitingTime = new XpdlWaitingTime("WaitingTime");
			waitingTime.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("WorkingTime")) {
			workingTime = new XpdlWorkingTime("WorkingTime");
			workingTime.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Duration")) {
			duration = new XpdlDuration("Duration");
			duration.importElement(xpp, xpdl);
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
		if (waitingTime != null) {
			s += waitingTime.exportElement();
		}
		if (workingTime != null) {
			s += workingTime.exportElement();
		}
		if (duration != null) {
			s += duration.exportElement();
		}
		return s;
	}

	public XpdlWaitingTime getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(XpdlWaitingTime waitingTime) {
		this.waitingTime = waitingTime;
	}

	public XpdlWorkingTime getWorkingTime() {
		return workingTime;
	}

	public void setWorkingTime(XpdlWorkingTime workingTime) {
		this.workingTime = workingTime;
	}

	public XpdlDuration getDuration() {
		return duration;
	}

	public void setDuration(XpdlDuration duration) {
		this.duration = duration;
	}

}
