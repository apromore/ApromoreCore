package cs.ut.ui.context.operations

import cs.ut.jobs.SimulationJob
import cs.ut.providers.Dir
import cs.ut.util.NirdizatiDownloader

class DownloadModelOperation(context: SimulationJob) : Operation<SimulationJob>(context) {

    override fun perform() {
        NirdizatiDownloader(Dir.PKL_DIR, this.context.id).execute()
    }
}