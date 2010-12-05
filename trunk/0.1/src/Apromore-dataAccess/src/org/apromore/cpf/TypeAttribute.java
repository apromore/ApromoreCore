
package org.apromore.cpf;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for typeAttribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeAttribute">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="typeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeAttribute")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class TypeAttribute {

    @XmlAttribute(name = "typeRef", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String typeRef;
    @XmlAttribute(name = "value")
    @XmlSchemaType(name = "anySimpleType")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String value;

    /**
     * Gets the value of the typeRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public String getTypeRef() {
        return typeRef;
    }

    /**
     * Sets the value of the typeRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setTypeRef(String value) {
        this.typeRef = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:39:06+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setValue(String value) {
        this.value = value;
    }

}
