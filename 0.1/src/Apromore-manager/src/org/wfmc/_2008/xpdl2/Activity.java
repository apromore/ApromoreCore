
package org.wfmc._2008.xpdl2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.wfmc._2002.xpdl1.FinishMode;
import org.wfmc._2002.xpdl1.StartMode;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Description" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Limit" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Route"/>
 *           &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Implementation"/>
 *           &lt;choice minOccurs="0">
 *             &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}BlockActivity"/>
 *             &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}BlockActivity"/>
 *           &lt;/choice>
 *           &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Event"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Transaction" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Performers" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Performer" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}StartMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}FinishMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Priority" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Deadline" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Deadline" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}SimulationInformation" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Icon" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Documentation" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}TransitionRestrictions" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}ExtendedAttributes" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}DataFields" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}InputSets" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}OutputSets" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}IORules" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Loop" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Assignments" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Object" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}NodeGraphicsInfos" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;sequence>
 *             &lt;element name="Extensions" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *             &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required" type="{http://www.wfmc.org/2008/XPDL2.1}Id" />
 *       &lt;attribute name="IsForCompensation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="StartActivity" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="Status" default="None">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="None"/>
 *             &lt;enumeration value="Ready"/>
 *             &lt;enumeration value="Active"/>
 *             &lt;enumeration value="Cancelled"/>
 *             &lt;enumeration value="Aborting"/>
 *             &lt;enumeration value="Aborted"/>
 *             &lt;enumeration value="Completing"/>
 *             &lt;enumeration value="Completed"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="StartMode">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="Automatic"/>
 *             &lt;enumeration value="Manual"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="FinishMode">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="Automatic"/>
 *             &lt;enumeration value="Manual"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="StartQuantity" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="CompletionQuantity" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="IsATransaction" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
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
    "content"
})
@XmlRootElement(name = "Activity")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class Activity {

    @XmlElementRefs({
        @XmlElementRef(name = "Deadline", namespace = "http://www.wfmc.org/2002/XPDL1.0", type = org.wfmc._2002.xpdl1.Deadline.class),
        @XmlElementRef(name = "Assignments", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Assignments.class),
        @XmlElementRef(name = "BlockActivity", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = org.wfmc._2008.xpdl2.BlockActivity.class),
        @XmlElementRef(name = "IORules", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = IORules.class),
        @XmlElementRef(name = "SimulationInformation", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = SimulationInformation.class),
        @XmlElementRef(name = "Implementation", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Implementation.class),
        @XmlElementRef(name = "NodeGraphicsInfos", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = NodeGraphicsInfos.class),
        @XmlElementRef(name = "ExtendedAttributes", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = ExtendedAttributes.class),
        @XmlElementRef(name = "Object", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = org.wfmc._2008.xpdl2.Object.class),
        @XmlElementRef(name = "Priority", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Priority.class),
        @XmlElementRef(name = "OutputSets", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = OutputSets.class),
        @XmlElementRef(name = "Transaction", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Transaction.class),
        @XmlElementRef(name = "TransitionRestrictions", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = TransitionRestrictions.class),
        @XmlElementRef(name = "Performers", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Performers.class),
        @XmlElementRef(name = "Extensions", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = JAXBElement.class),
        @XmlElementRef(name = "DataFields", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = DataFields.class),
        @XmlElementRef(name = "Route", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Route.class),
        @XmlElementRef(name = "Loop", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Loop.class),
        @XmlElementRef(name = "Performer", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Performer.class),
        @XmlElementRef(name = "Deadline", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = org.wfmc._2008.xpdl2.Deadline.class),
        @XmlElementRef(name = "Description", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Description.class),
        @XmlElementRef(name = "Limit", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Limit.class),
        @XmlElementRef(name = "Icon", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Icon.class),
        @XmlElementRef(name = "StartMode", namespace = "http://www.wfmc.org/2002/XPDL1.0", type = StartMode.class),
        @XmlElementRef(name = "Documentation", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Documentation.class),
        @XmlElementRef(name = "FinishMode", namespace = "http://www.wfmc.org/2002/XPDL1.0", type = FinishMode.class),
        @XmlElementRef(name = "InputSets", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = InputSets.class),
        @XmlElementRef(name = "BlockActivity", namespace = "http://www.wfmc.org/2002/XPDL1.0", type = org.wfmc._2002.xpdl1.BlockActivity.class),
        @XmlElementRef(name = "Event", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Event.class)
    })
    @XmlAnyElement(lax = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<java.lang.Object> content;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String id;
    @XmlAttribute(name = "IsForCompensation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean isForCompensation;
    @XmlAttribute(name = "Name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;
    @XmlAttribute(name = "StartActivity")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean startActivity;
    @XmlAttribute(name = "Status")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String status;
    @XmlAttribute(name = "StartMode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String startMode;
    @XmlAttribute(name = "FinishMode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String finishMode;
    @XmlAttribute(name = "StartQuantity")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger startQuantity;
    @XmlAttribute(name = "CompletionQuantity")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger completionQuantity;
    @XmlAttribute(name = "IsATransaction")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean isATransaction;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "BlockActivity" is used by two different parts of a schema. See: 
     * line 43 of file:/home/fauvet/apromore-workspace/Apromore-manager/WebContent/wsdl/bpmnxpdl_31_modified.xsd
     * line 42 of file:/home/fauvet/apromore-workspace/Apromore-manager/WebContent/wsdl/bpmnxpdl_31_modified.xsd
     * <p>
     * To get rid of this property, apply a property customization to one 
     * of both of the following declarations to change their names: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.wfmc._2002.xpdl1.Deadline }
     * {@link Assignments }
     * {@link org.wfmc._2008.xpdl2.BlockActivity }
     * {@link IORules }
     * {@link SimulationInformation }
     * {@link Implementation }
     * {@link NodeGraphicsInfos }
     * {@link ExtendedAttributes }
     * {@link org.wfmc._2008.xpdl2.Object }
     * {@link Priority }
     * {@link java.lang.Object }
     * {@link OutputSets }
     * {@link Transaction }
     * {@link TransitionRestrictions }
     * {@link Performers }
     * {@link JAXBElement }{@code <}{@link java.lang.Object }{@code >}
     * {@link DataFields }
     * {@link Route }
     * {@link Loop }
     * {@link Performer }
     * {@link org.wfmc._2008.xpdl2.Deadline }
     * {@link Description }
     * {@link Limit }
     * {@link Icon }
     * {@link StartMode }
     * {@link Documentation }
     * {@link InputSets }
     * {@link FinishMode }
     * {@link org.wfmc._2002.xpdl1.BlockActivity }
     * {@link Element }
     * {@link Event }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<java.lang.Object> getContent() {
        if (content == null) {
            content = new ArrayList<java.lang.Object>();
        }
        return this.content;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the isForCompensation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Boolean isIsForCompensation() {
        return isForCompensation;
    }

    /**
     * Sets the value of the isForCompensation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setIsForCompensation(Boolean value) {
        this.isForCompensation = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the startActivity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Boolean isStartActivity() {
        return startActivity;
    }

    /**
     * Sets the value of the startActivity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setStartActivity(Boolean value) {
        this.startActivity = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getStatus() {
        if (status == null) {
            return "None";
        } else {
            return status;
        }
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the startMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getStartMode() {
        return startMode;
    }

    /**
     * Sets the value of the startMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setStartMode(String value) {
        this.startMode = value;
    }

    /**
     * Gets the value of the finishMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getFinishMode() {
        return finishMode;
    }

    /**
     * Sets the value of the finishMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setFinishMode(String value) {
        this.finishMode = value;
    }

    /**
     * Gets the value of the startQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigInteger getStartQuantity() {
        if (startQuantity == null) {
            return new BigInteger("1");
        } else {
            return startQuantity;
        }
    }

    /**
     * Sets the value of the startQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setStartQuantity(BigInteger value) {
        this.startQuantity = value;
    }

    /**
     * Gets the value of the completionQuantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigInteger getCompletionQuantity() {
        if (completionQuantity == null) {
            return new BigInteger("1");
        } else {
            return completionQuantity;
        }
    }

    /**
     * Sets the value of the completionQuantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setCompletionQuantity(BigInteger value) {
        this.completionQuantity = value;
    }

    /**
     * Gets the value of the isATransaction property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public boolean isIsATransaction() {
        if (isATransaction == null) {
            return false;
        } else {
            return isATransaction;
        }
    }

    /**
     * Sets the value of the isATransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setIsATransaction(Boolean value) {
        this.isATransaction = value;
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
