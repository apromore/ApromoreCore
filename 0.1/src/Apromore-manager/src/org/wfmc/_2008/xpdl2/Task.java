
package org.wfmc._2008.xpdl2;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskService"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskReceive"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskManual"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskReference"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskScript"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskSend"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskUser"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TaskApplication"/>
 *       &lt;/choice>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "taskService",
    "taskReceive",
    "taskManual",
    "taskReference",
    "taskScript",
    "taskSend",
    "taskUser",
    "taskApplication"
})
@XmlRootElement(name = "Task")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class Task {

    @XmlElement(name = "TaskService")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskService taskService;
    @XmlElement(name = "TaskReceive")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskReceive taskReceive;
    @XmlElement(name = "TaskManual")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskManual taskManual;
    @XmlElement(name = "TaskReference")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskReference taskReference;
    @XmlElement(name = "TaskScript")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskScript taskScript;
    @XmlElement(name = "TaskSend")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskSend taskSend;
    @XmlElement(name = "TaskUser")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskUser taskUser;
    @XmlElement(name = "TaskApplication")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TaskApplication taskApplication;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * BPMN: TaskType = Service.  In BPMN generally signifies any automated activity.
     * 
     * @return
     *     possible object is
     *     {@link TaskService }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskService getTaskService() {
        return taskService;
    }

    /**
     * Sets the value of the taskService property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskService }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskService(TaskService value) {
        this.taskService = value;
    }

    /**
     * BPMN: TaskType = Receive.  Waits for a message, then continues. Equivalent to a "catching" message event.  In BPMN, "message" generally signifies any signal from outside the process (pool).
     * 
     * @return
     *     possible object is
     *     {@link TaskReceive }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskReceive getTaskReceive() {
        return taskReceive;
    }

    /**
     * Sets the value of the taskReceive property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskReceive }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskReceive(TaskReceive value) {
        this.taskReceive = value;
    }

    /**
     * BPMN: TaskType = Manual.  Used for human tasks other than those accessed via workflow.
     * 
     * @return
     *     possible object is
     *     {@link TaskManual }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskManual getTaskManual() {
        return taskManual;
    }

    /**
     * Sets the value of the taskManual property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskManual }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskManual(TaskManual value) {
        this.taskManual = value;
    }

    /**
     * BPMN: TaskType = Reference.  Task properties defined in referenced activity.
     * 
     * @return
     *     possible object is
     *     {@link TaskReference }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskReference getTaskReference() {
        return taskReference;
    }

    /**
     * Sets the value of the taskReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskReference }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskReference(TaskReference value) {
        this.taskReference = value;
    }

    /**
     * BPMN: TaskType = Script.  Used for automated tasks executed by scripts on process engine, to distinguish from automated tasks performed externally (Service).
     * 
     * @return
     *     possible object is
     *     {@link TaskScript }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskScript getTaskScript() {
        return taskScript;
    }

    /**
     * Sets the value of the taskScript property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskScript }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskScript(TaskScript value) {
        this.taskScript = value;
    }

    /**
     * BPMN: Task Type = Send.  Equivalent to a "throwing" message event.  Sends a message immediately and continues.  In BPMN, "message" signifies any signal sent outside the process (pool).
     * 
     * @return
     *     possible object is
     *     {@link TaskSend }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskSend getTaskSend() {
        return taskSend;
    }

    /**
     * Sets the value of the taskSend property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskSend }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskSend(TaskSend value) {
        this.taskSend = value;
    }

    /**
     * BPMN: Task Type = User.  Generally used for human tasks.  
     * 
     * @return
     *     possible object is
     *     {@link TaskUser }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskUser getTaskUser() {
        return taskUser;
    }

    /**
     * Sets the value of the taskUser property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskUser }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskUser(TaskUser value) {
        this.taskUser = value;
    }

    /**
     * Gets the value of the taskApplication property.
     * 
     * @return
     *     possible object is
     *     {@link TaskApplication }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TaskApplication getTaskApplication() {
        return taskApplication;
    }

    /**
     * Sets the value of the taskApplication property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskApplication }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTaskApplication(TaskApplication value) {
        this.taskApplication = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-22T11:04:04+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
