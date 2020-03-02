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