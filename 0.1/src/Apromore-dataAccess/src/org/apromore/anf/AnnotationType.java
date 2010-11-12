
package org.apromore.anf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.apache.cxf.jaxb.JAXBToStringBuilder;
import org.apache.cxf.jaxb.JAXBToStringStyle;
import org.w3c.dom.Element;


/**
 * <p>Java class for AnnotationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AnnotationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="cpfId" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnotationType", propOrder = {
    "any"
})
@XmlSeeAlso({
    DocumentationType.class,
    GraphicsType.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class AnnotationType {

    @XmlAnyElement(lax = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<Object> any;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger id;
    @XmlAttribute(name = "cpfId")
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected BigInteger cpfId;
    @XmlAnyAttribute
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Gets the value of the cpfId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public BigInteger getCpfId() {
        return cpfId;
    }

    /**
     * Sets the value of the cpfId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setCpfId(BigInteger value) {
        this.cpfId = value;
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

	/**
     * Generates a String representation of the contents of this type.
     * This is an extension method, produced by the 'ts' xjc plugin
     * 
     */
    @Override
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-11-12T08:51:55+01:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    public String toString() {
        return JAXBToStringBuilder.valueOf(this, JAXBToStringStyle.DEFAULT_STYLE);
    }

}
