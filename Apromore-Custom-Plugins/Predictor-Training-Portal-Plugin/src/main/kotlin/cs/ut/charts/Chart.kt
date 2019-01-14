package cs.ut.charts

import java.io.File

/**
 * Abstract class to represent server side component for charts
 */
abstract class Chart(val name: String, val payload: String, val file: File) : Renderable {
    companion object {
        const val NAMESPACE = "chart_data."
    }

    /**
     * Caption that will be used as chart name in the browser
     * @return name of the chart
     */
    open fun getCaption(): String = NAMESPACE + name
}