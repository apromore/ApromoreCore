
package org.apromore.anf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DocumentationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DocumentationType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apromore.org/ANF}AnnotationType">
 *       &lt;sequence>
 *         &lt;element name="documentation" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded"/>
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
@XmlType(name = "DocumentationType", propOrder = {
    "documentation"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
public class DocumentationType
    extends AnnotationType
{

    @XmlElement(required = true)
	@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
    protected List<Object> documentation;

    /**
     * Gets the value of the documentation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documentation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocumentation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-05-24T06:54:42+02:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-2")
	public List<Object> getDocumentation() {
        if (documentation == null) {
            documentation = new ArrayList<Object>();
        }
        return this.documentation;
    }

}
