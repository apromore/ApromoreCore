package cs.ut.ui.context.operations

import cs.ut.jobs.SimulationJob
import cs.ut.util.NirdizatiDownloader

class ExportResultsOperation(context: SimulationJob) : Operation<SimulationJob>(context) {

    override fun perform() {
        NirdizatiDownloader.downloadFilesAsZip(context)
    }
}