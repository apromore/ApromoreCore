
package org.wfmc._2008.xpdl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;


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
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}XPDLVersion"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Vendor"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Created"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}ModificationDate" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Description" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}Documentation" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}PriorityUnit" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}CostUnit" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}VendorExtensions" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}LayoutInfo" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
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
    "xpdlVersion",
    "vendor",
    "created",
    "modificationDate",
    "description",
    "documentation",
    "priorityUnit",
    "costUnit",
    "vendorExtensions",
    "layoutInfo",
    "any"
})
@XmlRootElement(name = "PackageHeader")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class PackageHeader {

    @XmlElement(name = "XPDLVersion", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected XPDLVersion xpdlVersion;
    @XmlElement(name = "Vendor", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Vendor vendor;
    @XmlElement(name = "Created", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Created created;
    @XmlElement(name = "ModificationDate")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected ModificationDate modificationDate;
    @XmlElement(name = "Description")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Description description;
    @XmlElement(name = "Documentation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Documentation documentation;
    @XmlElement(name = "PriorityUnit")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected PriorityUnit priorityUnit;
    @XmlElement(name = "CostUnit")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected CostUnit costUnit;
    @XmlElement(name = "VendorExtensions")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected VendorExtensions vendorExtensions;
    @XmlElement(name = "LayoutInfo")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected LayoutInfo layoutInfo;
    @XmlAnyElement(lax = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<java.lang.Object> any;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the xpdlVersion property.
     * 
     * @return
     *     possible object is
     *     {@link XPDLVersion }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public XPDLVersion getXPDLVersion() {
        return xpdlVersion;
    }

    /**
     * Sets the value of the xpdlVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link XPDLVersion }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setXPDLVersion(XPDLVersion value) {
        this.xpdlVersion = value;
    }

    /**
     * Gets the value of the vendor property.
     * 
     * @return
     *     possible object is
     *     {@link Vendor }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Vendor getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vendor }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVendor(Vendor value) {
        this.vendor = value;
    }

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link Created }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Created getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link Created }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setCreated(Created value) {
        this.created = value;
    }

    /**
     * Gets the value of the modificationDate property.
     * 
     * @return
     *     possible object is
     *     {@link ModificationDate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public ModificationDate getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets the value of the modificationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModificationDate }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setModificationDate(ModificationDate value) {
        this.modificationDate = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link Description }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Description getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link Description }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDescription(Description value) {
        this.description = value;
    }

    /**
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link Documentation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Documentation getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Documentation }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDocumentation(Documentation value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the priorityUnit property.
     * 
     * @return
     *     possible object is
     *     {@link PriorityUnit }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public PriorityUnit getPriorityUnit() {
        return priorityUnit;
    }

    /**
     * Sets the value of the priorityUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriorityUnit }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setPriorityUnit(PriorityUnit value) {
        this.priorityUnit = value;
    }

    /**
     * Gets the value of the costUnit property.
     * 
     * @return
     *     possible object is
     *     {@link CostUnit }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public CostUnit getCostUnit() {
        return costUnit;
    }

    /**
     * Sets the value of the costUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link CostUnit }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setCostUnit(CostUnit value) {
        this.costUnit = value;
    }

    /**
     * Gets the value of the vendorExtensions property.
     * 
     * @return
     *     possible object is
     *     {@link VendorExtensions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public VendorExtensions getVendorExtensions() {
        return vendorExtensions;
    }

    /**
     * Sets the value of the vendorExtensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link VendorExtensions }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVendorExtensions(VendorExtensions value) {
        this.vendorExtensions = value;
    }

    /**
     * Gets the value of the layoutInfo property.
     * 
     * @return
     *     possible object is
     *     {@link LayoutInfo }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public LayoutInfo getLayoutInfo() {
        return layoutInfo;
    }

    /**
     * Sets the value of the layoutInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayoutInfo }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setLayoutInfo(LayoutInfo value) {
        this.layoutInfo = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * {@link java.lang.Object }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<java.lang.Object> getAny() {
        if (any == null) {
            any = new ArrayList<java.lang.Object>();
        }
        return this.any;
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
