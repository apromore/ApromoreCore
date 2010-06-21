
package org.apromore.anf;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fillType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fillType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="image" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="color" type="{http://www.apromore.org/ANF}colorType" />
 *       &lt;attribute name="gradient-color" type="{http://www.apromore.org/ANF}colorType" />
 *       &lt;attribute name="gradient-rotation">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="vertical"/>
 *             &lt;enumeration value="horizontal"/>
 *             &lt;enumeration value="diagonal"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="transparency" type="{http://www.apromore.org/ANF}transparencyType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fillType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class FillType {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String image;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String color;
    @XmlAttribute(name = "gradient-color")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String gradientColor;
    @XmlAttribute(name = "gradient-rotation")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String gradientRotation;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer transparency;

    /**
     * Gets the value of the image property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getImage() {
        return image;
    }

    /**
     * Sets the value of the image property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setImage(String value) {
        this.image = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Gets the value of the gradientColor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getGradientColor() {
        return gradientColor;
    }

    /**
     * Sets the value of the gradientColor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setGradientColor(String value) {
        this.gradientColor = value;
    }

    /**
     * Gets the value of the gradientRotation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getGradientRotation() {
        return gradientRotation;
    }

    /**
     * Sets the value of the gradientRotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setGradientRotation(String value) {
        this.gradientRotation = value;
    }

    /**
     * Gets the value of the transparency property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public Integer getTransparency() {
        return transparency;
    }

    /**
     * Sets the value of the transparency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-21T05:04:01+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTransparency(Integer value) {
        this.transparency = value;
    }

}
