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