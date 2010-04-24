
package org.apromore.anf;

import java.math.BigDecimal;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for lineType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lineType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="shape">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="straight"/>
 *             &lt;enumeration value="orthogonal"/>
 *             &lt;enumeration value="spline"/>
 *             &lt;enumeration value="beziel"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
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
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="style">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="solid"/>
 *             &lt;enumeration value="dash"/>
 *             &lt;enumeration value="dot"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lineType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class LineType {

    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String shape;
    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String color;
    @XmlAttribute(name = "gradient-color")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String gradientColor;
    @XmlAttribute(name = "gradient-rotation")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String gradientRotation;
    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer transparency;
    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigDecimal width;
    @XmlAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String style;

    /**
     * Gets the value of the shape property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public String getShape() {
        return shape;
    }

    /**
     * Sets the value of the shape property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setShape(String value) {
        this.shape = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setTransparency(Integer value) {
        this.transparency = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public BigDecimal getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setWidth(BigDecimal value) {
        this.width = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setStyle(String value) {
        this.style = value;
    }

}
