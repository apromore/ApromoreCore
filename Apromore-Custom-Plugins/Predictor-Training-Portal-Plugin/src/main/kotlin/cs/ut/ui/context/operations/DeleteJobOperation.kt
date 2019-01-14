package cs.ut.ui.context.operations

import cs.ut.engine.JobManager
import cs.ut.engine.tasks.DisposalTask
import cs.ut.jobs.SimulationJob
import cs.ut.ui.NirdizatiGrid
import cs.ut.ui.controllers.validation.ValidationController
import cs.ut.util.Cookies
import org.zkoss.zk.ui.Executions

class DeleteJobOperation(context: SimulationJob) : Operation<SimulationJob>(context) {

    @Suppress("UNCHECKED_CAST")
    override fun perform() {
        DisposalTask() dispose context
        JobManager.cache.apply {
            val key = Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
            val items = this.cachedItems()[key]
            items?.removeItem(context)

            Executions.getCurrent().desktop.components.firstOrNull { it.id == ValidationController.gridId }?.also {
                it as NirdizatiGrid<SimulationJob>
                it.generate(items?.rawData() ?: listOf(), true)
            }
        }
    }

    override fun isEnabled(): Boolean {
        return context.owner == Cookies.getCookieKey(Executions.getCurrent().nativeRequest)
    }
}