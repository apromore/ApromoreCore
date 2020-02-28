/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package cs.ut.engine.item

/**
 * Class that represents model parameter that are used to train models
 * @param id identifier of given model parameter
 * @param parameter which value to use when passing parameter to the script
 * @param type type of the parameter (e.g. encoding, bucketing, learner or predictiontype)
 * @param properties hyper parameter properties for given model parameter
 */
data class ModelParameter(
        var id: String,
        var parameter: String,
        var type: String,
        var enabled: Boolean,
        var properties: MutableList<Property>
) {

    var translate = true

    /**
     * Get translation key for given parameter
     */
    fun getTranslateName() = this.type + "." + this.id

    constructor() : this("", "", "", false, mutableListOf())

    override fun equals(other: Any?): Boolean {
        return other is ModelParameter
                && this.id == other.id
                && this.enabled == other.enabled
                && this.type == other.type
                && this.parameter == other.parameter
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + parameter.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + enabled.hashCode()
        return result
    }

    override fun toString(): String = "$type.$id"
}