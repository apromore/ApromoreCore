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

import cs.ut.engine.item.ModelParameter
import cs.ut.exceptions.Either
import cs.ut.exceptions.Left
import cs.ut.exceptions.Right
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.ModelParamProvider
import cs.ut.util.Field

object JSONService {
    private val log = NirdizatiLogger.getLogger(JSONService::class)
    private val handler = JSONHandler()

    fun getTrainingData(id: String) = handler.fromFile<TrainingData>(id, Dir.DATA_DIR)

    fun getTrainingConfig(ids: List<String>) = ids.map { getTrainingConfig(it) }

    fun getTrainingConfig(id: String, dir: Dir = Dir.TRAIN_DIR): Either<Exception, TrainingConfiguration> {
        log.debug("Loading training config for $id")
        val start = System.currentTimeMillis()

        val res = handler.fromFile<Map<String, Any>>(id, dir)

        val map: MutableMap<String, Any> = when (res) {
            is Right -> res.result.toMutableMap()
            is Left -> return res
        }

        val evaluation = map.remove(JSONKeys.EVALUATION.value) as? Map<*, *> ?: mapOf<String, Any>()
        val info = map.remove(JSONKeys.UI_DATA.value) as Map<*, *>

        val target: String = map.keys.first()

        val secondLevelMap = (map[target] as Map<*, *>)
        val bucketing: String = secondLevelMap.keys.first() as String

        val thirdLevelMap = (secondLevelMap[bucketing] as Map<*, *>)
        val encoding: String = thirdLevelMap.keys.first() as String

        val learnerMap = (thirdLevelMap[encoding] as Map<*, *>)
        val learner: String = learnerMap.keys.first() as String

        var propertiesMap = (learnerMap[learner] as Map<*, *>)
        val isIndex = (propertiesMap.keys.first() as String).toIntOrNull() != null
        if (isIndex) {
            propertiesMap = propertiesMap[propertiesMap.keys.first() as String] as Map<*, *>
        }

        val provider = ModelParamProvider()
        val params: Map<String, List<ModelParameter>> = provider.properties

        val targetParam = params[Field.PREDICTION.value]!!.firstOrNull { it.parameter == target }?.copy(properties = mutableListOf())
         ?: params[Field.PREDICTION.value]!!.first { it.parameter == "-1" }.copy(parameter = target)

        val encodingParam = params[Field.ENCODING.value]!!.first { it.parameter == encoding }.copy(properties = mutableListOf())
        encodingParam.properties = mutableListOf()

        val bucketingParam = params[Field.BUCKETING.value]!!.first { it.parameter == bucketing }.copy(properties = mutableListOf())

        val learnerParam = params[Field.LEARNER.value]!!.first { it.parameter == learner }.copy(properties = mutableListOf())

        val properties = provider.getAllProperties()
        propertiesMap.forEach {
            val key = it.key as String
            val value = it.value

            val prop = properties.first { it.id == key }.copy(property = value.toString())
            learnerParam.properties.add(prop)
        }

        val end = System.currentTimeMillis()
        log.debug("Finished parsing training configuration in ${end - start} ms")

        val config = TrainingConfiguration(
                encodingParam,
                bucketingParam,
                learnerParam,
                targetParam)

        config.info = JobInformation(
                info[JSONKeys.OWNER.value] as String,
                info[JSONKeys.LOG_FILE.value] as String,
                info[JSONKeys.START.value] as String)

        config.evaluation = Report().apply {
            this.metric = (evaluation[JSONKeys.METRIC.value] ?: "") as String
            this.value = evaluation[JSONKeys.VALUE.value] as? Double ?: -1.0
        }

        return Right(config)
    }
}
