package cs.ut.charts

import cs.ut.util.NirdizatiTranslator
import org.zkoss.zk.ui.util.Clients
import java.io.File

/**
 * Heat map data server side representation for heat map chart on client side.
 */
class HeatMap(name: String, payload: String, private val xLabels: String, private val yLabels: String, file: File) :
    Chart(name, payload, file) {

    override fun render() {
        Clients.evalJavaScript(
            "heatMap('$payload', " +
                    "'${NirdizatiTranslator.localizeText(getCaption())}'" +
                    ",'$xLabels'" +
                    ", '$yLabels')"
        )
    }
}