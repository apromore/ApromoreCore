//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.09.15 at 04:54:36 PM EST 
//


package de.epml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for typeEPML complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeEPML">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.epml.de}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="graphicsDefault" type="{http://www.epml.de}typeGraphicsDefault" minOccurs="0"/>
 *         &lt;element name="coordinates" type="{http://www.epml.de}typeCoordinates"/>
 *         &lt;element name="definitions" type="{http://www.epml.de}typeDefinitions" minOccurs="0"/>
 *         &lt;element name="attributeTypes" type="{http://www.epml.de}typeAttrTypes" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="directory" type="{http://www.epml.de}typeDirectory" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeEPML", propOrder = {
    "graphicsDefault",
    "coordinates",
    "definitions",
    "attributeTypes",
    "directory",
    "epcs"
})
public class TypeEPML
    extends TExtensibleElements
{

    protected TypeGraphicsDefault graphicsDefault;
    @XmlElement(required = true)
    protected TypeCoordinates coordinates;
    protected TypeDefinitions definitions;
    protected List<TypeAttrTypes> attributeTypes;
    @XmlElement(required = true)
    protected List<TypeDirectory> directory;
    
    // modify
    @XmlElements({
        @XmlElement(name = "epc", type = TypeEPC.class)
    })
    protected List<TypeEPC> epcs;
    
    /**
     * Gets the value of the epc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the epc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEpc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeEPC }
     * 
     */
    public List<TypeEPC> getEpcs()
    {
    	if (epcs == null) {
            epcs = new ArrayList<TypeEPC>();
        }
        return this.epcs;
    }
    // end

    /**
     * Gets the value of the graphicsDefault property.
     * 
     * @return
     *     possible object is
     *     {@link TypeGraphicsDefault }
     *     
     */
    public TypeGraphicsDefault getGraphicsDefault() {
        return graphicsDefault;
    }

    /**
     * Sets the value of the graphicsDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeGraphicsDefault }
     *     
     */
    public void setGraphicsDefault(TypeGraphicsDefault value) {
        this.graphicsDefault = value;
    }

    /**
     * Gets the value of the coordinates property.
     * 
     * @return
     *     possible object is
     *     {@link TypeCoordinates }
     *     
     */
    public TypeCoordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeCoordinates }
     *     
     */
    public void setCoordinates(TypeCoordinates value) {
        this.coordinates = value;
    }

    /**
     * Gets the value of the definitions property.
     * 
     * @return
     *     possible object is
     *     {@link TypeDefinitions }
     *     
     */
    public TypeDefinitions getDefinitions() {
        return definitions;
    }

    /**
     * Sets the value of the definitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeDefinitions }
     *     
     */
    public void setDefinitions(TypeDefinitions value) {
        this.definitions = value;
    }

    /**
     * Gets the value of the attributeTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeAttrTypes }
     * 
     * 
     */
    public List<TypeAttrTypes> getAttributeTypes() {
        if (attributeTypes == null) {
            attributeTypes = new ArrayList<TypeAttrTypes>();
        }
        return this.attributeTypes;
    }

    /**
     * Gets the value of the directory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the directory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDirectory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeDirectory }
     * 
     * 
     */
    public List<TypeDirectory> getDirectory() {
        if (directory == null) {
            directory = new ArrayList<TypeDirectory>();
        }
        return this.directory;
    }
    
    /**
     * Sets the value of the EPML id.
     * 
     * @param URI
     *     String
     *     
     */
    public void setId(String URI)
    {
    	QName name1 = new QName("xmlns:apromore");
		String value1 = "http://www.apromore.org";
		QName name2 = new QName("apromore:URI");
		String value2 = URI;
		this.getOtherAttributes().put(name1, value1);
		this.getOtherAttributes().put(name2, value2);
    }
    
    /**
     * Gets the value of the EPML id.
     * 
     * @return URI
     *     String
     *     
     */
    public String getId()
    {
    	QName name = new QName("apromore:URI");
    	return this.getOtherAttributes().get(name);
    }

}
