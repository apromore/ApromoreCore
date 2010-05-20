
package org.apromore.cpf;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JoinType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JoinType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.apromore.org/CPF}RoutingType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JoinType")
@XmlSeeAlso({
    ORJoinType.class,
    ANDJoinType.class,
    XORJoinType.class
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-19T03:21:26+10:00", comments = "JAXB RI vhudson-jaxb-ri-2.1-833")
public class JoinType
    extends RoutingType
{


}
