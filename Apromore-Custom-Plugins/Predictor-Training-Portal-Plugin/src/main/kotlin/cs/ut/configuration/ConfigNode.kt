/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElementRef
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType

/**
 * Object representation of "Node" node in configuration file
 */

@XmlRootElement(name = "Node")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = ["identifier", "values", "childNodes"])
data class ConfigNode(

    @XmlAttribute(name = "Identifier")
    val identifier: String,

    @XmlElementRef(name = "Value")
    val values: MutableList<Value>,

    @XmlElementRef(name = "Node")
    val childNodes: MutableList<ConfigNode>
) {
    constructor() : this("", mutableListOf(), mutableListOf())

    override fun toString(): String =
        "${this::class.simpleName}[Id: $identifier, Values: ${values.size}, Children: ${childNodes.size}]"

    /**
     * Get list of Value.class from child node with Identifier "itemList"
     * @return list of Value.class entities from configuration
     *
     * @see Value
     */
    fun itemList(): List<Value> = childNodes.firstOrNull { it.identifier == itemList }?.values ?: listOf()

    /**
     * Get list of Value.class values from child node with Identifier "itemList"
     * @return list of values
     *
     * @see Value
     */
    fun itemListValues(): List<String> = itemList().map { it.value }

    /**
     * Find Value.class with given identifier
     * @param identifier to look for
     * @return value entity corresponding to given identifier
     *
     * @see Value
     */
    fun valueWithIdentifier(identifier: String) = values.first { it.identifier == identifier }



    /**
     * Whether or not this configuration node is enabled. Looks for a value with identifier "isEnabled"
     * @return boolean value that node contains
     */
    fun isEnabled() = valueWithIdentifier(IS_ENABLED).value<Boolean>()

    companion object {
        const val itemList = "itemList"
        const val IS_ENABLED = "isEnabled"
    }
}