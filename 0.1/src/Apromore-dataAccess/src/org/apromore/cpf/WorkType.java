
package org.apromore.cpf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WorkType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WorkType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apromore.org/CPF}NodeType">
 *       &lt;sequence>
 *         &lt;element name="resourceTypeRef" type="{http://www.apromore.org/CPF}resourceTypeRefType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="objectRef" type="{http://www.apromore.org/CPF}objectRefType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkType", propOrder = {
    "resourceTypeRef",
    "objectRef"
})
@XmlSeeAlso({
    TaskType.class,
    EventType.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-12T04:33:20+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class WorkType
    extends NodeType
{

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-12T04:33:20+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<ResourceTypeRefType> resourceTypeRef;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-12T04:33:20+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    protected List<ObjectRefType> objectRef;

    /**
     * Gets the value of the resourceTypeRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resourceTypeRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResourceTypeRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ResourceTypeRefType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-12T04:33:20+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<ResourceTypeRefType> getResourceTypeRef() {
        if (resourceTypeRef == null) {
            resourceTypeRef = new ArrayList<ResourceTypeRefType>();
        }
        return this.resourceTypeRef;
    }

    /**
     * Gets the value of the objectRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the objectRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObjectRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectRefType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-12T04:33:20+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
    public List<ObjectRefType> getObjectRef() {
        if (objectRef == null) {
            objectRef = new ArrayList<ObjectRefType>();
        }
        return this.objectRef;
    }

}
