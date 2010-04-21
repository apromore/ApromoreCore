
package org.wfmc._2002.xpdl1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Description" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Limit" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Route"/>
 *           &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Implementation"/>
 *           &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}BlockActivity"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Performer" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}StartMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}FinishMode" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Priority" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Deadline" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}SimulationInformation" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Icon" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Documentation" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}TransitionRestrictions" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}ExtendedAttributes" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "limit",
    "route",
    "implementation",
    "blockActivity",
    "performer",
    "startMode",
    "finishMode",
    "priority",
    "deadline",
    "simulationInformation",
    "icon",
    "documentation",
    "transitionRestrictions",
    "extendedAttributes"
})
@XmlRootElement(name = "Activity")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class Activity {

    @XmlElement(name = "Description")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String description;
    @XmlElement(name = "Limit")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String limit;
    @XmlElement(name = "Route")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Route route;
    @XmlElement(name = "Implementation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Implementation implementation;
    @XmlElement(name = "BlockActivity")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BlockActivity blockActivity;
    @XmlElement(name = "Performer")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String performer;
    @XmlElement(name = "StartMode")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected StartMode startMode;
    @XmlElement(name = "FinishMode")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected FinishMode finishMode;
    @XmlElement(name = "Priority")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String priority;
    @XmlElement(name = "Deadline")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<Deadline> deadline;
    @XmlElement(name = "SimulationInformation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected SimulationInformation simulationInformation;
    @XmlElement(name = "Icon")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String icon;
    @XmlElement(name = "Documentation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String documentation;
    @XmlElement(name = "TransitionRestrictions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected TransitionRestrictions transitionRestrictions;
    @XmlElement(name = "ExtendedAttributes")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ExtendedAttributes extendedAttributes;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String id;
    @XmlAttribute(name = "Name")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String name;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the limit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getLimit() {
        return limit;
    }

    /**
     * Sets the value of the limit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setLimit(String value) {
        this.limit = value;
    }

    /**
     * Gets the value of the route property.
     * 
     * @return
     *     possible object is
     *     {@link Route }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Route getRoute() {
        return route;
    }

    /**
     * Sets the value of the route property.
     * 
     * @param value
     *     allowed object is
     *     {@link Route }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setRoute(Route value) {
        this.route = value;
    }

    /**
     * Gets the value of the implementation property.
     * 
     * @return
     *     possible object is
     *     {@link Implementation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Implementation getImplementation() {
        return implementation;
    }

    /**
     * Sets the value of the implementation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Implementation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setImplementation(Implementation value) {
        this.implementation = value;
    }

    /**
     * Gets the value of the blockActivity property.
     * 
     * @return
     *     possible object is
     *     {@link BlockActivity }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BlockActivity getBlockActivity() {
        return blockActivity;
    }

    /**
     * Sets the value of the blockActivity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BlockActivity }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setBlockActivity(BlockActivity value) {
        this.blockActivity = value;
    }

    /**
     * Gets the value of the performer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getPerformer() {
        return performer;
    }

    /**
     * Sets the value of the performer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setPerformer(String value) {
        this.performer = value;
    }

    /**
     * Gets the value of the startMode property.
     * 
     * @return
     *     possible object is
     *     {@link StartMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public StartMode getStartMode() {
        return startMode;
    }

    /**
     * Sets the value of the startMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link StartMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setStartMode(StartMode value) {
        this.startMode = value;
    }

    /**
     * Gets the value of the finishMode property.
     * 
     * @return
     *     possible object is
     *     {@link FinishMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public FinishMode getFinishMode() {
        return finishMode;
    }

    /**
     * Sets the value of the finishMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinishMode }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setFinishMode(FinishMode value) {
        this.finishMode = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setPriority(String value) {
        this.priority = value;
    }

    /**
     * Gets the value of the deadline property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deadline property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeadline().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Deadline }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<Deadline> getDeadline() {
        if (deadline == null) {
            deadline = new ArrayList<Deadline>();
        }
        return this.deadline;
    }

    /**
     * Gets the value of the simulationInformation property.
     * 
     * @return
     *     possible object is
     *     {@link SimulationInformation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public SimulationInformation getSimulationInformation() {
        return simulationInformation;
    }

    /**
     * Sets the value of the simulationInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimulationInformation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setSimulationInformation(SimulationInformation value) {
        this.simulationInformation = value;
    }

    /**
     * Gets the value of the icon property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getIcon() {
        return icon;
    }

    /**
     * Sets the value of the icon property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setIcon(String value) {
        this.icon = value;
    }

    /**
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the transitionRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link TransitionRestrictions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public TransitionRestrictions getTransitionRestrictions() {
        return transitionRestrictions;
    }

    /**
     * Sets the value of the transitionRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransitionRestrictions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTransitionRestrictions(TransitionRestrictions value) {
        this.transitionRestrictions = value;
    }

    /**
     * Gets the value of the extendedAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link ExtendedAttributes }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ExtendedAttributes getExtendedAttributes() {
        return extendedAttributes;
    }

    /**
     * Sets the value of the extendedAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtendedAttributes }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setExtendedAttributes(ExtendedAttributes value) {
        this.extendedAttributes = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setName(String value) {
        this.name = value;
    }

}
