package cs.ut.ui

import cs.ut.configuration.ConfigFetcher
import org.zkoss.zul.Html
import org.zkoss.zul.Popup

/**
 * Parses tooltips from configuration into objects
 */
class TooltipParser {
    private val configNode by ConfigFetcher("tooltip/tooltips")

    /**
     * Read tooltip from configuration with given id
     *
     * @param id of tooltip to read
     *
     * @return ZK popup object that can be appended to the page
     */
    fun readTooltip(id: String): Popup {
        val popup = Popup()

        configNode.childNodes.firstOrNull { it.identifier == id }?.apply tooltip@ {
            popup.id = id
            popup.appendChild(
                Html(this.valueWithIdentifier("label").value).apply { this.id = id })
        }

        return popup
    }
}