package cs.ut.ui.context

import cs.ut.ui.context.operations.Operation
import org.zkoss.zk.ui.event.Events
import org.zkoss.zk.ui.event.OpenEvent
import org.zkoss.zul.Menuitem
import org.zkoss.zul.Menupopup
import org.zkoss.zul.impl.XulElement

class NirdizatiContextMenu<T> : Menupopup() {

    class NirdizatiContextMenuItem(caption: String, private val operation: Class<*>) : Menuitem(caption) {
        fun finalize() {
            this.addEventListener(Events.ON_CLICK, { _ ->
                val operationContext = (parent as NirdizatiContextMenu<*>).context!!
                createOperation(operationContext).perform()
            })
        }

        private fun createOperation(operationContext: Any): Operation<*> {
            return operation.getConstructor((operationContext.javaClass)).newInstance(operationContext) as Operation<*>
        }

        fun isEnabled(context: Any): Boolean = createOperation(context).isEnabled()
    }

    private var context: T? = null
    private val styleName = " selectedContext"
    private var selectedComponent: XulElement? = null

    @Suppress("UNCHECKED_CAST")
    fun finalize() {
        this.addEventListener(Events.ON_OPEN, { e ->
            e as OpenEvent
            if (e.isOpen) {
                val ref: XulElement = e.reference as XulElement
                selectedComponent = ref
                ref.sclass += styleName
                context = ref.getAttribute(NirdizatiContextMenu.COMPONENT_VALUE) as T?

                // Update items
                this.getChildren<NirdizatiContextMenuItem>().forEach {
                    it.isDisabled = !it.isEnabled(context!!)
                }
            } else {
                selectedComponent?.sclass = selectedComponent?.sclass?.replace(styleName, "")
                context = null
            }
        })

        this.getChildren<NirdizatiContextMenuItem>().forEach {
            it.finalize()
        }
    }

    companion object {
        const val COMPONENT_VALUE = "component_value"
    }
}