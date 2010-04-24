
package org.apromore.cpf;

import java.math.BigInteger;
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
 * <p>Java class for NetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Node" type="{http://www.apromore.org/CPF}NodeType" maxOccurs="unbounded"/>
 *         &lt;element name="Edge" type="{http://www.apromore.org/CPF}EdgeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetType", propOrder = {
    "node",
    "edge"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class NetType {

    @XmlElement(name = "Node", required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<NodeType> node;
    @XmlElement(name = "Edge")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<EdgeType> edge;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "positiveInteger")
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected BigInteger id;

    /**
     * Gets the value of the node property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the node property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public List<NodeType> getNode() {
        if (node == null) {
            node = new ArrayList<NodeType>();
        }
        return this.node;
    }

    /**
     * Gets the value of the edge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the edge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEdge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EdgeType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public List<EdgeType> getEdge() {
        if (edge == null) {
            edge = new ArrayList<EdgeType>();
        }
        return this.edge;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
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
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-04-24T05:20:07+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
	public void setId(BigInteger value) {
        this.id = value;
    }

}
