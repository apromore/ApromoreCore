/**
 * Canonical Process Format XML schema.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */

@javax.xml.bind.annotation.XmlSchema(
    namespace = "http://www.apromore.org/CPF",
    xmlns = {
        @javax.xml.bind.annotation.XmlNs(prefix = "cpf",    namespaceURI = "http://www.apromore.org/CPF"),
        @javax.xml.bind.annotation.XmlNs(prefix = "xsi",    namespaceURI = "http://www.w3.org/2001/XMLSchema-instance")
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED)

package org.apromore.cpf;
