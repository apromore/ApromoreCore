package cs.ut.charts

import org.zkoss.zk.ui.util.Clients
import java.io.File

/**
 * Line chart server side data representation for client side line chart (e.g. accuracy comparison)
 */
class LineChart(val id: String, name: String, payload: String, private val numberOfEvents: Int, file: File) : Chart(name, payload, file) {

    override fun render() {
        Clients.evalJavaScript(
            "lineChart('$payload','$id','$numberOfEvents', '${name.toUpperCase()}')"
        )
    }
}