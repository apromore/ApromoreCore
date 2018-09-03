package cs.ut.charts

import cs.ut.util.NirdizatiTranslator
import org.zkoss.zk.ui.util.Clients
import java.io.File

/**
 * Scatter plot sever side data representation for client side scatter plot
 */
class ScatterPlot(name: String, payload: String, file: File) : Chart(name, payload, file) {

    override fun render() {
        Clients.evalJavaScript("scatterPlot('$payload','${NirdizatiTranslator.localizeText(getCaption())}')")
    }
}