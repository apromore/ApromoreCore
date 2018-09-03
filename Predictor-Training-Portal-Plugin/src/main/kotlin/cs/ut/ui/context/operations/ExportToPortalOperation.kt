package cs.ut.ui.context.operations

import cs.ut.json.JSONHandler
import cs.ut.jobs.SimulationJob
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import java.io.File
import java.io.FileInputStream
import org.apromore.plugin.portal.predictortraining.PortalPlugin

class ExportToPortalOperation(context: SimulationJob) : Operation<SimulationJob>(context) {

    private val log = NirdizatiLogger.getLogger(ExportToPortalOperation::class)

    override fun perform() {

        // Transfer the pkl to MySQL
        val file = File(DirectoryConfiguration.dirPath(Dir.PKL_DIR)).listFiles().firstOrNull { it.name.contains(context.id) }
        val pkl = FileInputStream(file)
        val predictorName = uniqueLogName("${context.logName} (${context.configuration.bucketing.parameter}, ${context.configuration.encoding.parameter}, ${context.configuration.learner.parameter})")
        val predictor = PortalPlugin.globalPredictiveMonitorService!!.createPredictor(predictorName, context.configuration.outcome.parameter, pkl)
    }

    private fun logNameExists(logName: String): Boolean {
        return false
    }

    private fun uniqueLogName(name: String): String {
        var count = 1
        var result = name
        while (logNameExists(result)) {
            count++
            result = "${name} #${count}"
        }
        return result
    }
}
