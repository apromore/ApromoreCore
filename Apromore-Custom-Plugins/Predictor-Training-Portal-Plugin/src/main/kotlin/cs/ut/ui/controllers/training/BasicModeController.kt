/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package cs.ut.ui.controllers.training

import cs.ut.engine.item.ModelParameter
import cs.ut.exceptions.Either
import cs.ut.exceptions.Right
import cs.ut.json.TrainingConfiguration
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.ModelParamProvider
import cs.ut.ui.UIComponent
import org.zkoss.zk.ui.Component
import org.zkoss.zul.Vlayout

class BasicModeController(gridContainer: Vlayout, private val logName: String) : AbstractModeController(gridContainer),
        ModeController, UIComponent {
    private val log = NirdizatiLogger.getLogger(BasicModeController::class, getSessionId())
    private val optimized: Either<Exception, TrainingConfiguration> = ModelParamProvider.getOptimizedParameters(logName)

    init {
        log.debug("Initializing basic mode controller")
        this.gridContainer.getChildren<Component>().clear()
    }

    override fun isValid(): Boolean = true

    override fun gatherValues(): Map<String, List<ModelParameter>> =
            if (optimized is Right) {
                log.debug("Found optimized parameters for log $logName")
                val config = optimized.result
                listOf(config.encoding, config.bucketing, config.learner, config.outcome).groupBy { it.type }
            } else {
                log.debug("Did not find optimized params for log $log. Using default params")
                provider.getBasicParameters().groupBy { it.type }
            }
}