
package org.apromore.toolbox.model_da;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MergedSources complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MergedSources">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mergedSource" type="{http://www.apromore.org/data_access/model_toolbox}MergedSource" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MergedSources", propOrder = {
    "mergedSource"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
public class MergedSources {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    protected List<MergedSource> mergedSource;

    /**
     * Gets the value of the mergedSource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mergedSource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMergedSource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MergedSource }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2011-07-27T04:47:56+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.2-27")
    public List<MergedSource> getMergedSource() {
        if (mergedSource == null) {
            mergedSource = new ArrayList<MergedSource>();
        }
        return this.mergedSource;
    }

}
