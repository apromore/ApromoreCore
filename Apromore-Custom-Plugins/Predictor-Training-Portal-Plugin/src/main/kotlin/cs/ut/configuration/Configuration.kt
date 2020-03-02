/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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