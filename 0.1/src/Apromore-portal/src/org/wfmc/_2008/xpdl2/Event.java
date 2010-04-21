
package org.wfmc._2008.xpdl2;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


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
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}StartEvent" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}IntermediateEvent" minOccurs="0"/>
 *         &lt;element ref="{http://www.wfmc.org/2008/XPDL2.1}EndEvent" minOccurs="0"/>
 *       &lt;/choice>
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
    "startEvent",
    "intermediateEvent",
    "endEvent"
})
@XmlRootElement(name = "Event")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class Event {

    @XmlElement(name = "StartEvent")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected StartEvent startEvent;
    @XmlElement(name = "IntermediateEvent")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected IntermediateEvent intermediateEvent;
    @XmlElement(name = "EndEvent")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected EndEvent endEvent;
    @XmlAnyAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the startEvent property.
     * 
     * @return
     *     possible object is
     *     {@link StartEvent }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public StartEvent getStartEvent() {
        return startEvent;
    }

    /**
     * Sets the value of the startEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link StartEvent }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setStartEvent(StartEvent value) {
        this.startEvent = value;
    }

    /**
     * Gets the value of the intermediateEvent property.
     * 
     * @return
     *     possible object is
     *     {@link IntermediateEvent }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public IntermediateEvent getIntermediateEvent() {
        return intermediateEvent;
    }

    /**
     * Sets the value of the intermediateEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntermediateEvent }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setIntermediateEvent(IntermediateEvent value) {
        this.intermediateEvent = value;
    }

    /**
     * Gets the value of the endEvent property.
     * 
     * @return
     *     possible object is
     *     {@link EndEvent }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public EndEvent getEndEvent() {
        return endEvent;
    }

    /**
     * Sets the value of the endEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link EndEvent }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-21T05:10:19+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setEndEvent(EndEvent value) {
        this.endEvent = value;
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
