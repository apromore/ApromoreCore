
package org.wfmc._2002.xpdl1;

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
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}DataType"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Description" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="Index" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="Mode" default="IN">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="IN"/>
 *             &lt;enumeration value="OUT"/>
 *             &lt;enumeration value="INOUT"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dataType",
    "description"
})
@XmlRootElement(name = "FormalParameter")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class FormalParameter {

    @XmlElement(name = "DataType", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected DataType dataType;
    @XmlElement(name = "Description")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String description;
    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String id;
    @XmlAttribute(name = "Index")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String index;
    @XmlAttribute(name = "Mode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String mode;

    /**
     * Gets the value of the dataType property.
     * 
     * @return
     *     possible object is
     *     {@link DataType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDataType(DataType value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDescription(String value) {
        this.description = value;
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
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setIndex(String value) {
        this.index = value;
    }

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getMode() {
        if (mode == null) {
            return "IN";
        } else {
            return mode;
        }
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setMode(String value) {
        this.mode = value;
    }

}
