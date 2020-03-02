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

package cs.ut.charts

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

const val ACTUAL = "actual"
const val PREDICTED = "predicted"
const val NR_EVENTS = "nr_events"
const val SCORE = "score"
const val delim = ","
const val METRIC = "metric"
const val MAE = "mae"

/**
 * Class used to represent data model for linear chart
 *
 * @param x horizontal position on the line chart
 * @param y vertical position on the line chart
 * @param dataType what data does this represent (e.g. MAE, RMSE etc)
 */
data class LinearData(val x: Float, val y: Float, val dataType: String)

/**
 * Enum that is used to distinguish which parsing mode to use for given file
 */
enum class Mode {
    SCATTER,
    LINE
}

/**
 * Generate linear data payload based on given file and mode
 *
 * @param file where to extract the data from
 * @param mode what mode to use when parsing the file
 *
 * @return list of linear data representations
 */
fun getLinearPayload(file: File, mode: Mode): List<LinearData> {
    val dataSet = mutableListOf<LinearData>()

    var indexes = Pair(-1, -1)
    var indexOfMetric = -1
    BufferedReader(FileReader(file)).lines().forEach {
        if (indexes.first == -1) {
            val headerItems = it.split(delim)
            indexes = when (mode) {
                Mode.SCATTER -> Pair(headerItems.indexOf(ACTUAL), headerItems.indexOf(PREDICTED))
                Mode.LINE -> Pair(headerItems.indexOf(NR_EVENTS), headerItems.indexOf(SCORE))
            }

            indexOfMetric = if (Mode.SCATTER == mode) -1 else headerItems.indexOf(METRIC)
        } else {
            val items = it.split(delim)
            dataSet.add(
                LinearData(
                    x = items[indexes.first].toFloat(),
                    y = items[indexes.second].toFloat(),
                    dataType = if (Mode.SCATTER == mode) "" else items[indexOfMetric]
                )
            )
        }
    }
    return dataSet.sortedBy { it.dataType }
}

const val LABEL_INDEX = 0
const val VALUE_INDEX = 1

/**
 * Class that represents the data for bar charts
 *
 * @param label name of the parameter in the data set
 * @param value numeric value for given representation
 */
class BarChartData(val label: String, val value: Float)

/**
 * Generate bar chart payload based on given file
 *
 * @param file where to extract the data from
 * @return bar chart data set representation
 */
fun getBarChartPayload(file: File): List<BarChartData> {
    val dataSet = mutableListOf<BarChartData>()

    var rows = BufferedReader(FileReader(file)).use { it.readLines() }
    rows = rows.subList(1, rows.size)

    rows.forEach {
        val items = it.split(delim)
        dataSet.add(BarChartData(items[LABEL_INDEX], items[VALUE_INDEX].toFloat()))
    }

    return dataSet
}

/**
 * Class that represents heat map data set
 *
 * @param x horizontal in the heat map
 * @param y vertical position in the heat map
 * @param value that is held in given slot
 */
data class HeatMapDataSet(val x: Int, val y: Int, val value: Int)

/**
 * Wrapper for heat map data which contains heat map data set data and labels
 * @param xLabels labels for horizontal axis of the chart
 * @param yLabels labels for vertical axis of the chart
 * @param data heat map data set
 */
data class HeatMapData(
    val xLabels: List<String>,
    val yLabels: List<String>,
    val data: List<HeatMapDataSet>
)

/**
 * Heat map payload based on given file
 *
 * @param file file where to extract the data from
 * @return HeatMapData entitity filled with data based on given file
 *
 * @see HeatMapData
 */
fun getHeatMapPayload(file: File): HeatMapData {
    var rows: List<String> = BufferedReader(FileReader(file)).lineSequence().toList()
    var xLabels = rows[0].split(delim)
    xLabels -= xLabels[0]
    rows -= rows[0]

    var yLabels: List<String> = listOf()
    var data: List<HeatMapDataSet> = listOf()

    for ((y, row) in rows.asReversed().withIndex()) {
        var tokens: List<String> = row.split(delim)
        yLabels += tokens[0]
        tokens -= tokens[0]

        for ((x, token) in tokens.withIndex()) {
            data += HeatMapDataSet(x, y, token.toInt())
        }
    }
    return HeatMapData(xLabels, yLabels, data)
}