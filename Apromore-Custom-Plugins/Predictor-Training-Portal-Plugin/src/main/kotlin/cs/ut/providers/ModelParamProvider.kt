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

package cs.ut.providers

import cs.ut.configuration.ConfigFetcher
import cs.ut.configuration.ConfigNode
import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.item.ModelParameter
import cs.ut.engine.item.Property
import cs.ut.json.JSONService
import cs.ut.util.Field

/**
 * Parses model parameters from configuration
 *
 * @see ModelParameter
 * @see ConfigurationReader
 */
class ModelParamProvider {
    private val config by ConfigFetcher("models")

    var properties: Map<String, List<ModelParameter>> = mutableMapOf()

    init {
        parseParameters()
    }

    /**
     * Get basic parameters based on configuration definition
     */
    fun getBasicParameters(): List<ModelParameter> {
        val basicParameters = ConfigurationReader.findNode("models/basic").itemListValues()
        return mutableListOf<ModelParameter>().apply {
            basicParameters.forEach { p ->
                this.add(properties.flatMap { it.value }.first { it.id == p })
            }
        }
    }

    /**
     * Get all prediction types
     *
     * @return list of model parameters marked as 'predictiontype'
     */
    fun getPredictionTypes() = properties[Field.PREDICTION.value]!!

    /**
     * Get all properties from all model parameters
     *
     * @return list of properties
     * @see Property
     */
    fun getAllProperties(): List<Property> = properties.flatMap { it.value.flatMap { it.properties } }

    /**
     * Parses config nodes into model parameters
     *
     * @see ConfigNode
     * @see ModelParameter
     */
    private fun parseParameters() {
        val params = mutableListOf<ModelParameter>()
        config.childNodes.first { it.identifier == PARAM_NODE }.childNodes.forEach {
            params.add(ModelParameter().apply {
                this.enabled = it.values.first { it.identifier == ENABLED }.value()
                this.id = it.identifier
                this.type = it.values.first { it.identifier == TYPE }.value
                this.parameter = it.values.first { it.identifier == PARAMETER }.value

                val propNode = it.childNodes.firstOrNull { it.identifier == PROPERTIES }
                this.properties = parseProperties(propNode)
            })
        }

        properties = params.groupBy { it.type }
    }

    /**
     * Parses config nodes into properties
     *
     * @param modelParameter model parameter that is owner of the properties
     * @param propNode node where to take properties from
     *
     * @return list of properties defined by the configuration
     *
     * @see Property
     * @see ConfigNode
     */
    private fun parseProperties(propNode: ConfigNode?): MutableList<Property> {
        val properties = mutableListOf<Property>()

        propNode?.childNodes?.forEach {
            properties.add(Property().apply {
                this.id = it.identifier
                this.type = it.values.firstOrNull { it.identifier == CONTROL }?.value ?: ""
                this.maxValue = it.values.firstOrNull { it.identifier == MAX }?.value() ?: -1.0
                this.minValue = it.values.firstOrNull { it.identifier == MIN }?.value() ?: -1.0
                this.property = it.values.firstOrNull { it.identifier == DEFAULT }?.value ?: ""
            })
        }

        return properties
    }

    companion object {
        const val PARAM_NODE = "parameters"
        const val ENABLED = "isEnabled"
        const val TYPE = "type"
        const val PROPERTIES = "properties"
        const val CONTROL = "control"
        const val MAX = "max"
        const val MIN = "min"
        const val DEFAULT = "default"
        const val PARAMETER = "parameter"

        fun getOptimizedParameters(fileName: String) = JSONService.getTrainingConfig(fileName, Dir.OHP_DIR)
    }
}