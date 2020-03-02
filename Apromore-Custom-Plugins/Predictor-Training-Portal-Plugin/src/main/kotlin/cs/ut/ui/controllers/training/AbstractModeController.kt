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