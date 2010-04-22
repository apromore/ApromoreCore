//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.07 at 05:02:28 PM EST 
//


package org.apromore.anf;

import java.math.BigDecimal;

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
public class LineType {

    @XmlAttribute
    protected String shape;
    @XmlAttribute
    protected String color;
    @XmlAttribute(name = "gradient-color")
    protected String gradientColor;
    @XmlAttribute(name = "gradient-rotation")
    protected String gradientRotation;
    @XmlAttribute
    protected Integer transparency;
    @XmlAttribute
    protected BigDecimal width;
    @XmlAttribute
    protected String style;

    /**
     * Gets the value of the shape property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
    public void setStyle(String value) {
        this.style = value;
    }

}
