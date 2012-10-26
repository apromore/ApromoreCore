/**
 * Annotation Format XML schema.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */

@javax.xml.bind.annotation.XmlSchema(
    namespace = "http://www.apromore.org/ANF",
    xmlns = {
        @javax.xml.bind.annotation.XmlNs(prefix = "anf",    namespaceURI = "http://www.apromore.org/ANF"),
        @javax.xml.bind.annotation.XmlNs(prefix = "xsi",    namespaceURI = "http://www.w3.org/2001/XMLSchema-instance")
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED)

package org.apromore.anf;
