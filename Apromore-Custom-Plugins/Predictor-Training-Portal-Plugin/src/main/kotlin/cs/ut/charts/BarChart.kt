package cs.ut.charts

import cs.ut.util.NirdizatiTranslator
import org.zkoss.zk.ui.util.Clients
import java.io.File

/**
 * Bar chart data server side representation (e.g. feature importance charts)
 */
class BarChart(name: String, payload: String, private val labels: String, file: File) : Chart(name, payload, file) {
    override fun getCaption(): String = name

    override fun render() {
        Clients.evalJavaScript("barChart('$payload','${NirdizatiTranslator.localizeText(getCaption())}', '$labels')")
    }
}