/**
 * JAXB classes for BPMN, instrumented for decanonization.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */

@javax.xml.bind.annotation.XmlSchema(
    namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL",
    xmlns = {
        @javax.xml.bind.annotation.XmlNs(prefix = "anf",    namespaceURI = "http://www.apromore.org/ANF"),
        @javax.xml.bind.annotation.XmlNs(prefix = "bpmn",   namespaceURI = "http://www.omg.org/spec/BPMN/20100524/MODEL"),
        @javax.xml.bind.annotation.XmlNs(prefix = "bpmndi", namespaceURI = "http://www.omg.org/spec/BPMN/20100524/DI"),
        @javax.xml.bind.annotation.XmlNs(prefix = "cpf",    namespaceURI = "http://www.apromore.org/CPF"),
        @javax.xml.bind.annotation.XmlNs(prefix = "omgdi",  namespaceURI = "http://www.omg.org/spec/DD/20100524/DI"),
        @javax.xml.bind.annotation.XmlNs(prefix = "omgdc",  namespaceURI = "http://www.omg.org/spec/DD/20100524/DC"),
        @javax.xml.bind.annotation.XmlNs(prefix = "xsi",    namespaceURI = "http://www.w3.org/2001/XMLSchema-instance")
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED)

package org.apromore.canoniser.bpmn.bpmn;
