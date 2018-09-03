package cs.ut.configuration

import java.io.File
import javax.xml.bind.JAXBContext
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElementRef
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * XML root node for configuration element
 */
@XmlRootElement(name = "Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = ["childNodes"])
data class Configuration(

    @XmlElementRef(name = "Node")
    val childNodes: MutableList<ConfigNode>
) {
    constructor() : this(mutableListOf())

    companion object {
        private const val nodeName = "Configuration"

        /**
         * Reads configuration from the configuration file
         * @return deserialized configuration
         */
        fun readSelf(): Configuration {
            val dbf = DocumentBuilderFactory.newInstance()
            dbf.isNamespaceAware = true

            val db: DocumentBuilder = dbf.newDocumentBuilder()
            val doc = db.parse(File(this::class.java.classLoader.getResource("config.xml").file!!))

            val node = doc.getElementsByTagName(nodeName)

            val jaxbContext = JAXBContext.newInstance(Configuration::class.java)
            return jaxbContext.createUnmarshaller().unmarshal(node.item(0), Configuration::class.java).value
        }
    }
}