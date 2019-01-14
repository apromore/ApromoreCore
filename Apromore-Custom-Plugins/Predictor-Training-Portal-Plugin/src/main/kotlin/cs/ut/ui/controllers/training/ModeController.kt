package cs.ut.ui.controllers.training

import cs.ut.engine.item.ModelParameter

interface ModeController {

    /**
     * Is data in given controller valid
     */
    fun isValid(): Boolean

    /**
     * Function that will be called in order to gather data from given controller
     *
     * @return map of gathered values
     */
    fun gatherValues(): Map<String, List<ModelParameter>>

    /**
     * Function that is called in order to gracefully destroy the component
     */
    fun preDestroy() = Unit
}