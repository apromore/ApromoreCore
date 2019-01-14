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