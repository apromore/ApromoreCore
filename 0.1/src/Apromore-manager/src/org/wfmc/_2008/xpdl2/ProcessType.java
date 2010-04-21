
package org.wfmc._2008.xpdl2;

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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


/**
 * <p>Java class for ProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProcessType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}ProcessHeader"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}RedefinableHeader" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}FormalParameters" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}InputSets" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}OutputSets" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;sequence minOccurs="0">
 *             &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Participants" minOccurs="0"/>
 *             &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Applications" minOccurs="0"/>
 *             &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}DataFields" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;sequence minOccurs="0">
 *             &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}DataFields" minOccurs="0"/>
 *             &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Participants" minOccurs="0"/>
 *             &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Applications" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}ActivitySets" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Activities" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Transitions" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}ExtendedAttributes" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Assignments" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}PartnerLinks" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Object" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;sequence>
 *             &lt;element name="Extensions" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *             &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required" type="{http://www.wfmc.org/2008/XPDL2.1}Id" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="AccessLevel" default="PUBLIC">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="PUBLIC"/>
 *             &lt;enumeration value="PRIVATE"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="ProcessType" default="None">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="None"/>
 *             &lt;enumeration value="Private"/>
 *             &lt;enumeration value="Abstract"/>
 *             &lt;enumeration value="Collaboration"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
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
 *       &lt;attribute name="SuppressJoinFailure" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="EnableInstanceCompensation" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="AdHoc" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="AdHocOrdering" default="Parallel">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="Sequential"/>
 *             &lt;enumeration value="Parallel"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="AdHocCompletionCondition" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="DefaultStartActivitySetId" type="{http://www.wfmc.org/2008/XPDL2.1}IdRef" />
 *       &lt;attribute name="DefaultStartActivityId" type="{http://www.wfmc.org/2008/XPDL2.1}IdRef" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessType", propOrder = {
    "content"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ProcessType {

    @XmlElementRefs({
        @XmlElementRef(name = "Assignments", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Assignments.class),
        @XmlElementRef(name = "Applications", namespace = "http://www.wfmc.org/2002/XPDL1.0", type = org.wfmc._2002.xpdl1.Applications.class),
        @XmlElementRef(name = "Activities", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Activities.class),
        @XmlElementRef(name = "Transitions", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = Transitions.class),
        @XmlElementRef(name = "PartnerLinks", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = PartnerLinks.class),
        @XmlElementRef(name = "DataFields", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = org.wfmc._2008.xpdl2.DataFields.class),
        @XmlElementRef(name = "Extensions", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = JAXBElement.class),
        @XmlElementRef(name = "Participants", namespace = "http://www.wfmc.org/2002/XPDL1.0", type = org.wfmc._2002.xpdl1.Participants.class),
        @XmlElementRef(name = "ExtendedAttributes", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = ExtendedAttributes.class),
        @XmlElementRef(name = "ProcessHeader", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = ProcessHeader.class),
        @XmlElementRef(name = "Object", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = org.wfmc._2008.xpdl2.Object.class),
        @XmlElementRef(name = "OutputSets", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = OutputSets.class),
        @XmlElementRef(name = "Participants", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = org.wfmc._2008.xpdl2.Participants.class),
        @XmlElementRef(name = "InputSets", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = InputSets.class),
        @XmlElementRef(name = "RedefinableHeader", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = RedefinableHeader.class),
        @XmlElementRef(name = "Applications", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = org.wfmc._2008.xpdl2.Applications.class),
        @XmlElementRef(name = "FormalParameters", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = FormalParameters.class),
        @XmlElementRef(name = "ActivitySets", namespace = "http://www.wfmc.org/2008/XPDL2.1", type = ActivitySets.class),
        @XmlElementRef(name = "DataFields", namespace = "http://www.wfmc.org/2002/XPDL1.0", type = org.wfmc._2002.xpdl1.DataFields.class)
    })
    @XmlAnyElement(lax = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<java.lang.Object> content;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String id;
    @XmlAttribute(name = "Name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;
    @XmlAttribute(name = "AccessLevel")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String accessLevel;
    @XmlAttribute(name = "ProcessType")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String processType;
    @XmlAttribute(name = "Status")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String status;
    @XmlAttribute(name = "SuppressJoinFailure")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean suppressJoinFailure;
    @XmlAttribute(name = "EnableInstanceCompensation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean enableInstanceCompensation;
    @XmlAttribute(name = "AdHoc")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean adHoc;
    @XmlAttribute(name = "AdHocOrdering")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String adHocOrdering;
    @XmlAttribute(name = "AdHocCompletionCondition")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String adHocCompletionCondition;
    @XmlAttribute(name = "DefaultStartActivitySetId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String defaultStartActivitySetId;
    @XmlAttribute(name = "DefaultStartActivityId")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String defaultStartActivityId;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the rest of the content model. 
     * 
     * <p>
     * You are getting this "catch-all" property because of the following reason: 
     * The field name "DataFields" is used by two different parts of a schema. See: 
     * line 2144 of file:/home/fauvet/apromore-workspace/Apromore-manager/WebContent/wsdl/bpmnxpdl_31_modified.xsd
     * line 2141 of file:/home/fauvet/apromore-workspace/Apromore-manager/WebContent/wsdl/bpmnxpdl_31_modified.xsd
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
     * {@link Assignments }
     * {@link org.wfmc._2002.xpdl1.Applications }
     * {@link Transitions }
     * {@link Activities }
     * {@link PartnerLinks }
     * {@link org.wfmc._2008.xpdl2.DataFields }
     * {@link JAXBElement }{@code <}{@link java.lang.Object }{@code >}
     * {@link org.wfmc._2002.xpdl1.Participants }
     * {@link ExtendedAttributes }
     * {@link ProcessHeader }
     * {@link org.wfmc._2008.xpdl2.Object }
     * {@link java.lang.Object }
     * {@link OutputSets }
     * {@link org.wfmc._2008.xpdl2.Participants }
     * {@link InputSets }
     * {@link org.wfmc._2008.xpdl2.Applications }
     * {@link RedefinableHeader }
     * {@link FormalParameters }
     * {@link Element }
     * {@link ActivitySets }
     * {@link org.wfmc._2002.xpdl1.DataFields }
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
     * Gets the value of the accessLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getAccessLevel() {
        if (accessLevel == null) {
            return "PUBLIC";
        } else {
            return accessLevel;
        }
    }

    /**
     * Sets the value of the accessLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAccessLevel(String value) {
        this.accessLevel = value;
    }

    /**
     * Gets the value of the processType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getProcessType() {
        if (processType == null) {
            return "None";
        } else {
            return processType;
        }
    }

    /**
     * Sets the value of the processType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessType(String value) {
        this.processType = value;
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
     * Gets the value of the suppressJoinFailure property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public boolean isSuppressJoinFailure() {
        if (suppressJoinFailure == null) {
            return false;
        } else {
            return suppressJoinFailure;
        }
    }

    /**
     * Sets the value of the suppressJoinFailure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setSuppressJoinFailure(Boolean value) {
        this.suppressJoinFailure = value;
    }

    /**
     * Gets the value of the enableInstanceCompensation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public boolean isEnableInstanceCompensation() {
        if (enableInstanceCompensation == null) {
            return false;
        } else {
            return enableInstanceCompensation;
        }
    }

    /**
     * Sets the value of the enableInstanceCompensation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setEnableInstanceCompensation(Boolean value) {
        this.enableInstanceCompensation = value;
    }

    /**
     * Gets the value of the adHoc property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public boolean isAdHoc() {
        if (adHoc == null) {
            return false;
        } else {
            return adHoc;
        }
    }

    /**
     * Sets the value of the adHoc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAdHoc(Boolean value) {
        this.adHoc = value;
    }

    /**
     * Gets the value of the adHocOrdering property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getAdHocOrdering() {
        if (adHocOrdering == null) {
            return "Parallel";
        } else {
            return adHocOrdering;
        }
    }

    /**
     * Sets the value of the adHocOrdering property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAdHocOrdering(String value) {
        this.adHocOrdering = value;
    }

    /**
     * Gets the value of the adHocCompletionCondition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getAdHocCompletionCondition() {
        return adHocCompletionCondition;
    }

    /**
     * Sets the value of the adHocCompletionCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAdHocCompletionCondition(String value) {
        this.adHocCompletionCondition = value;
    }

    /**
     * Gets the value of the defaultStartActivitySetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDefaultStartActivitySetId() {
        return defaultStartActivitySetId;
    }

    /**
     * Sets the value of the defaultStartActivitySetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDefaultStartActivitySetId(String value) {
        this.defaultStartActivitySetId = value;
    }

    /**
     * Gets the value of the defaultStartActivityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDefaultStartActivityId() {
        return defaultStartActivityId;
    }

    /**
     * Sets the value of the defaultStartActivityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDefaultStartActivityId(String value) {
        this.defaultStartActivityId = value;
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
