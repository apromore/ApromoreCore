package cs.ut.ui.adapters

import cs.ut.exceptions.Left
import cs.ut.exceptions.perform
import cs.ut.ui.FieldComponent
import cs.ut.ui.GridValueProvider
import cs.ut.util.COMP_ID
import cs.ut.util.IdentColumns
import cs.ut.util.NirdizatiTranslator
import org.zkoss.zul.Combobox
import org.zkoss.zul.Label
import org.zkoss.zul.Row

/**
 * Adapter that is used when generating data set parameter modal
 */
class ColumnRowValueAdapter(private val valueList: List<String>, private val identifiedCols: Map<String, String>) :
    GridValueProvider<String, Row> {

    override fun provide(data: String): Pair<FieldComponent, Row> {
        val noResource = "modals.param.no_resource"
        val row = Row()

        val label = Label(NirdizatiTranslator.localizeText("modals.param.$data"))
        label.setAttribute(COMP_ID, data)
        label.sclass = "param-modal-label"

        val comboBox = Combobox()

        val identified = identifiedCols[data]
        comboBox.isReadonly = true
        comboBox.setConstraint("no empty")

        valueList.forEach {
            val comboItem = comboBox.appendItem(it)
            comboItem.setValue(it)

            if (it == identified) comboBox.selectedItem = comboItem
        }

        // Add empty value as well if resource column is not present
        if (data == IdentColumns.RESOURCE.value) {
            comboBox.appendItem(NirdizatiTranslator.localizeText(noResource)).setValue("")
        }

        val res = perform { comboBox.selectedItem }
        when (res) {
            is Left -> comboBox.selectedItem = (comboBox.getItemAtIndex(0))
        }

        row.appendChild(label)
        row.appendChild(comboBox)

        return FieldComponent(label, comboBox) to row
    }
}