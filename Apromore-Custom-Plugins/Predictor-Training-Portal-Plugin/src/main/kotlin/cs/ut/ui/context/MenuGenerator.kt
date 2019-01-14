package cs.ut.ui.context

import cs.ut.configuration.ConfigurationReader
import cs.ut.logging.NirdizatiLogger
import cs.ut.util.NirdizatiTranslator
import org.zkoss.zul.Menupopup


class MenuGenerator<T>(private val menuMode: MenuMode) {
    enum class MenuMode(val value: String) {
        VALIDATION("validation")
    }

    private val pkg = "cs.ut.ui.context.operations."

    fun generate(): Menupopup {
        val start = System.currentTimeMillis()

        log.debug("Generating context menu in mode $menuMode")
        val config = ConfigurationReader.findNode("context/${menuMode.value}")
        log.debug("Generating ${config.childNodes.size} operations")

        val menu = NirdizatiContextMenu<T>()
        menu.id = menuMode.value

        config.childNodes.forEach {
            val menuItem = NirdizatiContextMenu.NirdizatiContextMenuItem(
                    NirdizatiTranslator.localizeText(it.valueWithIdentifier("label").value),
                    Class.forName(pkg + it.valueWithIdentifier("operation").value))

            menuItem.id = it.identifier
            menuItem.iconSclass = it.valueWithIdentifier("icon").value
            menuItem.sclass = "n-menu-item"

            menu.appendChild(menuItem)
        }

        System.currentTimeMillis().apply {
            log.debug("Finished context menu generation in ${this - start} ms.")
        }

        menu.finalize()
        return menu
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(MenuGenerator::class)
    }
}