
package org.apromore.anf;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fontType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fontType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="family" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="style">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="normal"/>
 *             &lt;enumeration value="italic"/>
 *             &lt;enumeration value="bold"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="weight" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="decoration">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="none"/>
 *             &lt;enumeration value="underline"/>
 *             &lt;enumeration value="overline"/>
 *             &lt;enumeration value="line-through"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="color" type="{http://www.apromore.org/ANF}colorType" />
 *       &lt;attribute name="transparency" type="{http://www.apromore.org/ANF}transparencyType" />
 *       &lt;attribute name="x_position" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="y_position" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="verticalAlign">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="top"/>
 *             &lt;enumeration value="middle"/>
 *             &lt;enumeration value="bottom"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="horizontalAlign">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="left"/>
 *             &lt;enumeration value="center"/>
 *             &lt;enumeration value="right"/>
 *             &lt;enumeration value="justify"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="rotation" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fontType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class FontType {

    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String family;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String style;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String weight;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger size;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String decoration;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String color;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected Integer transparency;
    @XmlAttribute(name = "x_position")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigDecimal xPosition;
    @XmlAttribute(name = "y_position")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigDecimal yPosition;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String verticalAlign;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected String horizontalAlign;
    @XmlAttribute
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigDecimal rotation;

    /**
     * Gets the value of the family property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getFamily() {
        return family;
    }

    /**
     * Sets the value of the family property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setFamily(String value) {
        this.family = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setWeight(String value) {
        this.weight = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigInteger getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setSize(BigInteger value) {
        this.size = value;
    }

    /**
     * Gets the value of the decoration property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getDecoration() {
        return decoration;
    }

    /**
     * Sets the value of the decoration property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setDecoration(String value) {
        this.decoration = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Gets the value of the transparency property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setTransparency(Integer value) {
        this.transparency = value;
    }

    /**
     * Gets the value of the xPosition property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigDecimal getXPosition() {
        return xPosition;
    }

    /**
     * Sets the value of the xPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setXPosition(BigDecimal value) {
        this.xPosition = value;
    }

    /**
     * Gets the value of the yPosition property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigDecimal getYPosition() {
        return yPosition;
    }

    /**
     * Sets the value of the yPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setYPosition(BigDecimal value) {
        this.yPosition = value;
    }

    /**
     * Gets the value of the verticalAlign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getVerticalAlign() {
        return verticalAlign;
    }

    /**
     * Sets the value of the verticalAlign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setVerticalAlign(String value) {
        this.verticalAlign = value;
    }

    /**
     * Gets the value of the horizontalAlign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public String getHorizontalAlign() {
        return horizontalAlign;
    }

    /**
     * Sets the value of the horizontalAlign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setHorizontalAlign(String value) {
        this.horizontalAlign = value;
    }

    /**
     * Gets the value of the rotation property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public BigDecimal getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-06-22T11:54:58+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public void setRotation(BigDecimal value) {
        this.rotation = value;
    }

}
