package org.apromore.processmining.plugins.xpdl;

/**
 * @author hverbeek
 * 
 *         <xsd:complexType name="ApplicationType"> <xsd:choice> <xsd:element
 *         name="Ejb"> <xsd:annotation> <xsd:documentation> Call EJB component
 *         -- There can be max one formal parameter that is OUT, if it exists it
 *         has to be the last formal parameter. no INOUT formal parameters
 *         </xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="JndiName"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element> <xsd:element name="HomeClass"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element> <xsd:element name="Method"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element> <xsd:element name="Pojo">
 *         <xsd:annotation> <xsd:documentation> Call method on Java class --
 *         There can be max one formal parameter that is OUT, if it exists it
 *         has to be the last formal parameter. no INOUT formal parameters
 *         </xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="Class"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element> <xsd:element name="Method"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element> <xsd:element name="Xslt">
 *         <xsd:annotation> <xsd:documentation> Execute Tranformation -- Formal
 *         Parameters restrictions: one IN and one OUT formal parameters or only
 *         one INOUT formal parameter </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="location" type="xsd:anyURI"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element> <xsd:element name="Script">
 *         <xsd:annotation> <xsd:documentation> Execute Script -- No additional
 *         restrictions for formal parameters. The suggestion: every Formal
 *         Parameter should be registered in the script scope as a global
 *         variable </xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="Expression"
 *         type="xpdl:ExpressionType" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element> <xsd:element name="WebService"> <xsd:annotation>
 *         <xsd:documentation> For WSDL 1.2 -- Invoke WebService, all IN Fprmal
 *         Parameters will be mapped to input message, all OUT Formal Parameters
 *         will be maped from output message </xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:element
 *         ref="xpdl:WebServiceOperation"/> <xsd:element
 *         ref="xpdl:WebServiceFaultCatch" minOccurs="0" maxOccurs="unbounded"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="InputMsgName" type="xsd:string" use="required">
 *         <xsd:annotation> <xsd:documentation> The name of inputMessage as
 *         defined in the WSDL which will help in uniquely identifying the
 *         operation to be invoked </xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="OutputMsgName"
 *         type="xsd:string" use="optional"> <xsd:annotation>
 *         <xsd:documentation> The name of outputMessage as defined in the WSDL
 *         which will help in uniquely identifying the operation to be invoked
 *         </xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element> <xsd:element name="BusinessRule">
 *         <xsd:annotation> <xsd:documentation>Invoke business
 *         rule</xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="RuleName"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element> <xsd:element name="Location"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:anyURI">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element> <xsd:element name="Form">
 *         <xsd:annotation> <xsd:documentation> Placeholder for all form related
 *         additional information. </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="FormLayout"
 *         minOccurs="0"> <xsd:complexType> <xsd:complexContent> <xsd:extension
 *         base="xsd:anyType"> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:extension> </xsd:complexContent>
 *         </xsd:complexType> </xsd:element> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 *         </xsd:choice> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType>
 */
public class XpdlApplicationType extends XpdlElement {

	/*
	 * For the time being, ignore the contents of this element. If needed, this
	 * needs to be implemented.
	 */

	public XpdlApplicationType(String tag) {
		super(tag);
	}
}
