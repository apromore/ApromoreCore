
package org.apromore.cpf;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaskType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apromore.org/CPF}WorkType">
 *       &lt;attribute name="subnetId" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="external" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class TaskType
    extends WorkType
{

    @XmlAttribute(name = "subnetId")
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger subnetId;
    @XmlAttribute(name = "external")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected Boolean external;

    /**
     * Gets the value of the subnetId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getSubnetId() {
        return subnetId;
    }

    /**
     * Sets the value of the subnetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setSubnetId(BigInteger value) {
        this.subnetId = value;
    }

    /**
     * Gets the value of the external property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public boolean isExternal() {
        if (external == null) {
            return false;
        } else {
            return external;
        }
    }

    /**
     * Sets the value of the external property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-12-05T10:42:28+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setExternal(Boolean value) {
        this.external = value;
    }

}
