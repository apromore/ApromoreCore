
package org.apromore.manager.model_da;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReadCanonicalAnfInputMsgType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReadCanonicalAnfInputMsgType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="processId" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="withAnnotation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="annotationName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReadCanonicalAnfInputMsgType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class ReadCanonicalAnfInputMsgType {

    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer processId;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String version;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Boolean withAnnotation;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String annotationName;

    /**
     * Gets the value of the processId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getProcessId() {
        return processId;
    }

    /**
     * Sets the value of the processId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setProcessId(Integer value) {
        this.processId = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the withAnnotation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Boolean isWithAnnotation() {
        return withAnnotation;
    }

    /**
     * Sets the value of the withAnnotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setWithAnnotation(Boolean value) {
        this.withAnnotation = value;
    }

    /**
     * Gets the value of the annotationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getAnnotationName() {
        return annotationName;
    }

    /**
     * Sets the value of the annotationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-07-23T05:11:00+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setAnnotationName(String value) {
        this.annotationName = value;
    }

}
