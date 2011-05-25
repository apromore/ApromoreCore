
package org.apromore.anf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AnnotationsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AnnotationsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Annotation" type="{http://www.apromore.org/ANF}AnnotationType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnotationsType", propOrder = {
    "annotation"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class AnnotationsType {

    @XmlElement(name = "Annotation", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<AnnotationType> annotation;
    @XmlAttribute(name = "uri", required = true)
    @XmlSchemaType(name = "anyURI")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String uri;
    @XmlAttribute(name = "name")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected String name;

    /**
     * Gets the value of the annotation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AnnotationType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<AnnotationType> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<AnnotationType>();
        }
        return this.annotation;
    }

    /**
     * Gets the value of the uri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setUri(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public void setName(String value) {
        this.name = value;
    }

}
