
package org.apromore.cpf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResourceTypeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResourceTypeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="configurable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="attribute" type="{http://www.apromore.org/CPF}typeAttribute" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="specializationIds" type="{http://www.apromore.org/CPF}IdListType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceTypeType", propOrder = {
    "name",
    "configurable",
    "attribute"
})
@XmlSeeAlso({
    NonhumanType.class,
    HumanType.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class ResourceTypeType {

    @XmlElement(required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String name;
    @XmlElement(defaultValue = "false")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean configurable;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	protected List<TypeAttribute> attribute;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger id;
    @XmlAttribute(name = "specializationIds")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<BigInteger> specializationIds;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the configurable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public Boolean isConfigurable() {
        return configurable;
    }

    /**
     * Sets the value of the configurable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setConfigurable(Boolean value) {
        this.configurable = value;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeAttribute }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<TypeAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<TypeAttribute>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the specializationIds property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the specializationIds property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecializationIds().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BigInteger }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<BigInteger> getSpecializationIds() {
        if (specializationIds == null) {
            specializationIds = new ArrayList<BigInteger>();
        }
        return this.specializationIds;
    }

}
