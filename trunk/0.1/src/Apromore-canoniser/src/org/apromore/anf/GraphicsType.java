
package org.apromore.anf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GraphicsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GraphicsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apromore.org/ANF}AnnotationType">
 *       &lt;sequence>
 *         &lt;element name="position" type="{http://www.apromore.org/ANF}positionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="size" type="{http://www.apromore.org/ANF}sizeType" minOccurs="0"/>
 *         &lt;element name="fill" type="{http://www.apromore.org/ANF}fillType" minOccurs="0"/>
 *         &lt;element name="line" type="{http://www.apromore.org/ANF}lineType" minOccurs="0"/>
 *         &lt;element name="font" type="{http://www.apromore.org/ANF}fontType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GraphicsType", propOrder = {
    "position",
    "size",
    "fill",
    "line",
    "font"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class GraphicsType
    extends AnnotationType
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	protected List<PositionType> position;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	protected SizeType size;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	protected FillType fill;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	protected LineType line;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	protected FontType font;

    /**
     * Gets the value of the position property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the position property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPosition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PositionType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public List<PositionType> getPosition() {
        if (position == null) {
            position = new ArrayList<PositionType>();
        }
        return this.position;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link SizeType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public SizeType getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link SizeType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setSize(SizeType value) {
        this.size = value;
    }

    /**
     * Gets the value of the fill property.
     * 
     * @return
     *     possible object is
     *     {@link FillType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public FillType getFill() {
        return fill;
    }

    /**
     * Sets the value of the fill property.
     * 
     * @param value
     *     allowed object is
     *     {@link FillType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setFill(FillType value) {
        this.fill = value;
    }

    /**
     * Gets the value of the line property.
     * 
     * @return
     *     possible object is
     *     {@link LineType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public LineType getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link LineType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setLine(LineType value) {
        this.line = value;
    }

    /**
     * Gets the value of the font property.
     * 
     * @return
     *     possible object is
     *     {@link FontType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public FontType getFont() {
        return font;
    }

    /**
     * Sets the value of the font property.
     * 
     * @param value
     *     allowed object is
     *     {@link FontType }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-13T04:08:15+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setFont(FontType value) {
        this.font = value;
    }

}
