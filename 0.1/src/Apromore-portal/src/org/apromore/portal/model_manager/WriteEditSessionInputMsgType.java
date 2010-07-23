
package org.apromore.portal.model_manager;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WriteEditSessionInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WriteEditSessionInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EditSession" type="{http://www.apromore.org/manager/model_portal}EditSessionType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WriteEditSessionInputMsgType", propOrder = {
    "editSession"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:33:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class WriteEditSessionInputMsgType {

    @XmlElement(name = "EditSession", required = true)
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:33:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected EditSessionType editSession;

    /**
     * Gets the value of the editSession property.
     * 
     * @return
     *     possible object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:33:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public EditSessionType getEditSession() {
        return editSession;
    }

    /**
     * Sets the value of the editSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link EditSessionType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:33:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setEditSession(EditSessionType value) {
        this.editSession = value;
    }

}
