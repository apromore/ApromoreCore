package cs.ut.ui.adapters

import cs.ut.configuration.ConfigNode
import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.item.ModelParameter
import cs.ut.ui.FieldComponent
import cs.ut.ui.GridValueProvider
import cs.ut.ui.TooltipParser
import cs.ut.ui.components.CheckBoxGroup
import cs.ut.util.COMP_ID
import cs.ut.util.NirdizatiTranslator
import org.zkoss.zk.ui.event.Events
import org.zkoss.zul.A
import org.zkoss.zul.Checkbox
import org.zkoss.zul.Hbox
import org.zkoss.zul.Label
import org.zkoss.zul.Popup
import org.zkoss.zul.Row

data class GeneratorArgument(val id: String, val params: List<ModelParameter>)

/**
 * Implementation that is used when generating grid for the training view
 */
class AdvancedModeAdapter : GridValueProvider<GeneratorArgument, Row> {
    private val parser: TooltipParser = TooltipParser()
    lateinit var fields: MutableList<FieldComponent>

    override fun provide(data: GeneratorArgument): Pair<FieldComponent, Row> {
        val row = Row()

        val label = Label(NirdizatiTranslator.localizeText(data.id)).apply {
            this.sclass = "param-label"
            this.hflex = "min"
            this.setAttribute(COMP_ID, data.params.first().type)
        }

        val icons = ConfigurationReader.findNode("iconClass")
        row.appendChild(Hbox().apply {
            this.vflex = "1"
            this.align = "center"
            this.appendChild(label)
        })
        row.appendChild(Hbox().apply {
            this.appendChild(getTooltip(data.id, icons))
        })

        val group = CheckBoxGroup(CheckBoxGroup.Mode.ANY)
        data.params.forEach { param ->
            row.appendChild(
                    Hbox().also {
                        it.align = "center"
                        val checkBox = Checkbox().apply {
                            setValue(param)
                            sclass = "big-scale"
                        }

                        group.addComponent(checkBox)
                        val nameLabel = Label(NirdizatiTranslator.localizeText(param.type + "." + param.id))
                        it.appendChild(nameLabel)

                        it.appendChild(checkBox)
                        it.appendChild(nameLabel)
                    })
            row.appendChild(
                    Hbox().also {
                        it.appendChild(getTooltip(param.id, icons))
                    })
        }

        return FieldComponent(label, group) to row
    }

    /**
     * Generate wrapper for tooltip with a tooltip that is shown on hover
     *
     * @param tooltip id of the tooltip to load
     * @return wrapper with a tooltip that is shown on hover
     */
    private fun getTooltip(tooltip: String, config: ConfigNode): A {
        return A().apply {
            this.vflex = "1"
            this.hflex = "min"
            this.iconSclass = config.valueWithIdentifier("tooltip").value
            this.sclass = "validation-btn"

            this.addEventListener(Events.ON_MOUSE_OVER, { _ ->
                desktop.components.firstOrNull { it.id == tooltip && it is Popup }?.detach()
                parser.readTooltip(tooltip).also {
                    this.appendChild(it)
                    it.sclass = "n-popup"
                    it.id = tooltip
                }.open(this, "end_after")
            })
            this.addEventListener(Events.ON_MOUSE_OUT, { _ ->
                desktop.components.forEach { (it as? Popup)?.close() }
            })
        }
    }
}
