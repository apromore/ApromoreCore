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

package cs.ut.engine.item

/**
 * Class that represent hyper parameter properties for model parameter
 *
 * @see ModelParameter
 */
data class Property(
        var id: String,

        var type: String,

        var property: String,

        var maxValue: Double,

        var minValue: Double
) {
    constructor() : this("", "", "", -1.0, -1.0)


    override fun equals(other: Any?): Boolean {
        return other is Property && this.id == other.id && this.type == other.type && this.property == other.property &&
                this.maxValue == other.maxValue && this.minValue == other.minValue
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + property.hashCode()
        result = 31 * result + maxValue.hashCode()
        result = 31 * result + minValue.hashCode()
        return result
    }
}

