package cs.ut.ui.components

import cs.ut.logging.NirdizatiLogger
import org.zkoss.zul.Checkbox


class CheckBoxGroup(mode: Mode) : ComponentGroup<Checkbox>() {
    enum class Mode {
        ALL,
        ANY
    }

    lateinit var gatherer: (Checkbox, MutableMap<String, Any>, String) -> Unit

    init {
        validator = when (mode) {
            Mode.ALL -> { items ->
                items.asSequence()
                        .any(Checkbox::isChecked)
            }
            Mode.ANY -> { items ->
                items.asSequence()
                        .all(Checkbox::isChecked)
            }
        }

        log.debug("Validation mode set to $mode")
    }

    fun gather(valueMap: MutableMap<String, Any>, label: String) {
        components.asSequence()
                .forEach { component -> gatherer(component, valueMap, label) }
    }

    companion object {
        val log = NirdizatiLogger.getLogger(CheckBoxGroup::class)
    }
}