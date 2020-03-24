package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.idname.XpdlTaskApplication;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Task"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:choice minOccurs="0"> <xsd:element
 *         ref="xpdl:TaskService"> <xsd:annotation> <xsd:documentation> BPMN:
 *         TaskType = Service. In BPMN generally signifies any automated
 *         activity. </xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:element ref="xpdl:TaskReceive"> <xsd:annotation>
 *         <xsd:documentation> BPMN: TaskType = Receive. Waits for a message,
 *         then continues. Equivalent to a "catching" message event. In BPMN,
 *         "message" generally signifies any signal from outside the process
 *         (pool). </xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:element ref="xpdl:TaskManual"> <xsd:annotation>
 *         <xsd:documentation> BPMN: TaskType = Manual. Used for human tasks
 *         other than those accessed via workflow. </xsd:documentation>
 *         </xsd:annotation> </xsd:element> <xsd:element
 *         ref="xpdl:TaskReference"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in XPDL 2.2</xsd:documentation>
 *         </xsd:annotation> </xsd:element> <xsd:element ref="xpdl:TaskScript">
 *         <xsd:annotation> <xsd:documentation> BPMN: TaskType = Script. Used
 *         for automated tasks executed by scripts on process engine, to
 *         distinguish from automated tasks performed externally (Service).
 *         </xsd:documentation> </xsd:annotation> </xsd:element> <xsd:element
 *         ref="xpdl:TaskSend"> <xsd:annotation> <xsd:documentation> BPMN: Task
 *         Type = Send. Equivalent to a "throwing" message event. Sends a
 *         message immediately and continues. In BPMN, "message" signifies any
 *         signal sent outside the process (pool). </xsd:documentation>
 *         </xsd:annotation> </xsd:element> <xsd:element ref="xpdl:TaskUser">
 *         <xsd:annotation> <xsd:documentation> BPMN: Task Type = User.
 *         Generally used for human tasks. </xsd:documentation>
 *         </xsd:annotation> </xsd:element> <xsd:element
 *         ref="xpdl:TaskApplication"/> <xsd:element
 *         ref="xpdl:TaskBusinessRule"> <xsd:annotation>
 *         <xsd:documentation>BPMN2 new task type.</xsd:documentation>
 *         </xsd:annotation> </xsd:element> </xsd:choice> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTask extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlTaskService taskService;
	private XpdlTaskReceive taskReceive;
	private XpdlTaskManual taskManual;
	private XpdlTaskReference taskReference;
	private XpdlTaskScript taskScript;
	private XpdlTaskSend taskSend;
	private XpdlTaskUser taskUser;
	private XpdlTaskApplication taskApplication;

	public XpdlTask(String tag) {
		super(tag);

		taskService = null;
		taskReceive = null;
		taskManual = null;
		taskReference = null;
		taskScript = null;
		taskSend = null;
		taskUser = null;
		taskApplication = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("TaskService")) {
			taskService = new XpdlTaskService("TaskService");
			taskService.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TaskReceive")) {
			taskReceive = new XpdlTaskReceive("TaskReceive");
			taskReceive.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TaskManual")) {
			taskManual = new XpdlTaskManual("TaskManual");
			taskManual.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TaskReference")) {
			taskReference = new XpdlTaskReference("TaskReference");
			taskReference.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TaskScript")) {
			taskScript = new XpdlTaskScript("TaskScript");
			taskScript.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TaskSend")) {
			taskSend = new XpdlTaskSend("TaskSend");
			taskSend.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TaskUser")) {
			taskUser = new XpdlTaskUser("TaskUser");
			taskUser.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TaskApplication")) {
			taskApplication = new XpdlTaskApplication("TaskApplication");
			taskApplication.importElement(xpp, xpdl);
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
		if (taskService != null) {
			s += taskService.exportElement();
		}
		if (taskReceive != null) {
			s += taskReceive.exportElement();
		}
		if (taskManual != null) {
			s += taskManual.exportElement();
		}
		if (taskReference != null) {
			s += taskReference.exportElement();
		}
		if (taskScript != null) {
			s += taskScript.exportElement();
		}
		if (taskSend != null) {
			s += taskSend.exportElement();
		}
		if (taskUser != null) {
			s += taskUser.exportElement();
		}
		if (taskApplication != null) {
			s += taskApplication.exportElement();
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		int nr = 0;
		if (taskService != null) {
			nr++;
		}
		if (taskReceive != null) {
			nr++;
		}
		if (taskManual != null) {
			nr++;
		}
		if (taskReference != null) {
			nr++;
		}
		if (taskScript != null) {
			nr++;
		}
		if (taskSend != null) {
			nr++;
		}
		if (taskUser != null) {
			nr++;
		}
		if (taskApplication != null) {
			nr++;
		}
		if (nr > 1) {
			xpdl.log(tag, lineNumber, "Expected one task type");
		}
	}

	public XpdlTaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(XpdlTaskService taskService) {
		this.taskService = taskService;
	}

	public XpdlTaskReceive getTaskReceive() {
		return taskReceive;
	}

	public void setTaskReceive(XpdlTaskReceive taskReceive) {
		this.taskReceive = taskReceive;
	}

	public XpdlTaskManual getTaskManual() {
		return taskManual;
	}

	public void setTaskManual(XpdlTaskManual taskManual) {
		this.taskManual = taskManual;
	}

	public XpdlTaskReference getTaskReference() {
		return taskReference;
	}

	public void setTaskReference(XpdlTaskReference taskReference) {
		this.taskReference = taskReference;
	}

	public XpdlTaskScript getTaskScript() {
		return taskScript;
	}

	public void setTaskScript(XpdlTaskScript taskScript) {
		this.taskScript = taskScript;
	}

	public XpdlTaskSend getTaskSend() {
		return taskSend;
	}

	public void setTaskSend(XpdlTaskSend taskSend) {
		this.taskSend = taskSend;
	}

	public XpdlTaskUser getTaskUser() {
		return taskUser;
	}

	public void setTaskUser(XpdlTaskUser taskUser) {
		this.taskUser = taskUser;
	}

	public XpdlTaskApplication getTaskApplication() {
		return taskApplication;
	}

	public void setTaskApplication(XpdlTaskApplication taskApplication) {
		this.taskApplication = taskApplication;
	}
}
