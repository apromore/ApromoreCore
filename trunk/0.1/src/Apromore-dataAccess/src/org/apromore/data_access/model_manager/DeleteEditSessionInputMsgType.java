
package org.apromore.data_access.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DeleteEditSessionInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeleteEditSessionInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="EditSessionCode" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeleteEditSessionInputMsgType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-18T10:13:29+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class DeleteEditSessionInputMsgType {

    @XmlAttribute(name = "EditSessionCode")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-18T10:13:29+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer editSessionCode;

    /**
     * Gets the value of the editSessionCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-18T10:13:29+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getEditSessionCode() {
        return editSessionCode;
    }

    /**
     * Sets the value of the editSessionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-18T10:13:29+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setEditSessionCode(Integer value) {
        this.editSessionCode = value;
    }

}
