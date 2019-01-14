package cs.ut.ui.controllers.training

import cs.ut.engine.item.ModelParameter
import cs.ut.providers.ModelParamProvider
import cs.ut.ui.controllers.TrainingController
import org.zkoss.zul.Vlayout

/**
 * Ab
 */
abstract class AbstractModeController(protected val gridContainer: Vlayout) {
    protected val provider = ModelParamProvider()

    protected val parameters: Map<String, List<ModelParameter>> by lazy {
        (provider.properties - TrainingController.PREDICTION)
    }
}