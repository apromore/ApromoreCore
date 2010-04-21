
package org.wfmc._2002.xpdl1;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Automatic"/>
 *         &lt;element ref="{http://www.wfmc.org/2002/XPDL1.0}Manual"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "automatic",
    "manual"
})
@XmlRootElement(name = "FinishMode")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class FinishMode {

    @XmlElement(name = "Automatic")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Automatic automatic;
    @XmlElement(name = "Manual")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Manual manual;

    /**
     * Gets the value of the automatic property.
     * 
     * @return
     *     possible object is
     *     {@link Automatic }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Automatic getAutomatic() {
        return automatic;
    }

    /**
     * Sets the value of the automatic property.
     * 
     * @param value
     *     allowed object is
     *     {@link Automatic }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAutomatic(Automatic value) {
        this.automatic = value;
    }

    /**
     * Gets the value of the manual property.
     * 
     * @return
     *     possible object is
     *     {@link Manual }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Manual getManual() {
        return manual;
    }

    /**
     * Sets the value of the manual property.
     * 
     * @param value
     *     allowed object is
     *     {@link Manual }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T04:25:43+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setManual(Manual value) {
        this.manual = value;
    }

}
