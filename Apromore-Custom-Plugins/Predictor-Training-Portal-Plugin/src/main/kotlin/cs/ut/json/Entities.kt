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

package cs.ut.json

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.item.ModelParameter
import cs.ut.util.Algorithm
import cs.ut.util.Node

enum class JSONKeys(val value: String) {
    UI_DATA("ui_data"),
    EVALUATION("evaluation"),
    OWNER("job_owner"),
    LOG_FILE("log_file"),
    START("start_time"),
    METRIC("metric"),
    VALUE("value")
}

data class TrainingConfiguration(
        @get:JsonIgnore val encoding: ModelParameter,
        @get:JsonIgnore val bucketing: ModelParameter,
        @get:JsonIgnore val learner: ModelParameter,
        @get:JsonIgnore val outcome: ModelParameter
) {
    lateinit var info: JobInformation
        @JsonIgnore get

    lateinit var evaluation: Report
        @JsonIgnore get

    @JsonAnyGetter
    fun getProperties(): Map<String, Any> {
        fun String.safeConvert(): Number = this.toIntOrNull() ?: this.toFloat()

        val map = mutableMapOf<String, Any>()

        val learnerMap = mutableMapOf<String, Any>()
        if (bucketing.id == Algorithm.PREFIX.value) {
            val propMap = mutableMapOf<String, Any>()
            learner.properties.forEach { propMap[it.id] = it.property.safeConvert() }

            val node = ConfigurationReader.findNode("models/parameters/prefix_length_based").
                    valueWithIdentifier(Node.EVENT_NUMBER.value).value<Int>()

            for (i in 1..node) {
                learnerMap[i.toString()] = propMap
            }

        } else {
            learner.properties.forEach { learnerMap[it.id] = it.property.safeConvert() }
        }

        val encodingMap = mapOf<String, Any>(learner.parameter to learnerMap)
        val bucketingMap = mapOf<String, Any>(encoding.parameter to encodingMap)
        val targetMap = mapOf<String, Any>(bucketing.parameter to bucketingMap)

        map[outcome.parameter] = targetMap
        map[JSONKeys.UI_DATA.value] = JSONHandler().toMap(info)

        return map
    }
}

data class JobInformation(
        @field:JsonProperty(value = "job_owner") var owner: String,
        @field:JsonProperty(value = "log_file") var logFile: String,
        @field:JsonProperty(value = "start_time") var startTime: String
) {
    constructor() : this("", "", "")
}

class Report {
    var metric: String = ""
    var value: Double = 0.0
}

class TrainingData {

    @JsonProperty(value = "static_cat_cols")
    var staticCategorical = listOf<String>()

    @JsonProperty(value = "dynamic_cat_cols")
    var dynamicCategorical = listOf<String>()

    @JsonProperty(value = "static_num_cols")
    var staticNumeric = listOf<String>()

    @JsonProperty(value = "dynamic_num_cols")
    var dynamicNumeric = listOf<String>()

    @JsonProperty(value = "case_id_col")
    var caseId = ""

    @JsonProperty(value = "activity_col")
    var activity = ""

    @JsonProperty(value = "timestamp_col")
    var timestamp = ""

    @JsonProperty(value = "future_values")
    var futureValues = listOf<String>()

    fun getAllColumns(): List<String> = ((staticCategorical + staticNumeric + futureValues).toList() - activity)
}
