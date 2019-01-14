package cs.ut.ui.components

import org.zkoss.zul.impl.XulElement

abstract class ComponentGroup<T : XulElement> {
    val valid: Boolean
        get() = validator(components)

    val components: MutableList<T> = mutableListOf()

    lateinit var validator: (List<T>) -> Boolean

    fun addComponent(element: T) {
        components.add(element)
    }

    fun applyToAll(addition: (T) -> Unit) {
        components.forEach { component -> addition(component) }
    }
}