//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2012.11.06 um 03:23:25 PM EST 
//


package de.epml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für typeObject complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="typeObject">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.epml.de}tEpcElement">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="configurableObject" type="{http://www.epml.de}typeCObject"/>
 *         &lt;/choice>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="attribute" type="{http://www.epml.de}typeAttribute"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="input"/>
 *             &lt;enumeration value="output"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="consumed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="initial" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="final" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeObject", propOrder = {
    "configurableObject",
    "attribute"
})
public class TypeObject
    extends TEpcElement
{

    protected TypeCObject configurableObject;
    protected List<TypeAttribute> attribute;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "optional")
    protected Boolean optional;
    @XmlAttribute(name = "consumed")
    protected Boolean consumed;
    @XmlAttribute(name = "initial")
    protected Boolean initial;
    @XmlAttribute(name = "final")
    protected Boolean _final;

    /**
     * Ruft den Wert der configurableObject-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TypeCObject }
     *     
     */
    public TypeCObject getConfigurableObject() {
        return configurableObject;
    }

    /**
     * Legt den Wert der configurableObject-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeCObject }
     *     
     */
    public void setConfigurableObject(TypeCObject value) {
        this.configurableObject = value;
    }

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeAttribute }
     * 
     * 
     */
    public List<TypeAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<TypeAttribute>();
        }
        return this.attribute;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der optional-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOptional() {
        if (optional == null) {
            return false;
        } else {
            return optional;
        }
    }

    /**
     * Legt den Wert der optional-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptional(Boolean value) {
        this.optional = value;
    }

    /**
     * Ruft den Wert der consumed-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isConsumed() {
        if (consumed == null) {
            return false;
        } else {
            return consumed;
        }
    }

    /**
     * Legt den Wert der consumed-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConsumed(Boolean value) {
        this.consumed = value;
    }

    /**
     * Ruft den Wert der initial-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isInitial() {
        if (initial == null) {
            return false;
        } else {
            return initial;
        }
    }

    /**
     * Legt den Wert der initial-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInitial(Boolean value) {
        this.initial = value;
    }

    /**
     * Ruft den Wert der final-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isFinal() {
        if (_final == null) {
            return false;
        } else {
            return _final;
        }
    }

    /**
     * Legt den Wert der final-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFinal(Boolean value) {
        this._final = value;
    }

}
