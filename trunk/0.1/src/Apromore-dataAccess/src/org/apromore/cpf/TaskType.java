
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
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:21:26+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class TaskType
    extends WorkType
{

    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:21:26+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger subnetId;

    /**
     * Gets the value of the subnetId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:21:26+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:21:26+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setSubnetId(BigInteger value) {
        this.subnetId = value;
    }

}
