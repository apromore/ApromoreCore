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

package cs.ut.charts

import cs.ut.engine.Cache
import cs.ut.engine.CacheHolder
import cs.ut.engine.LogManager
import cs.ut.exceptions.Left
import cs.ut.exceptions.Right
import cs.ut.jobs.SimulationJob
import cs.ut.json.JSONHandler
import cs.ut.logging.NirdizatiLogger
import java.io.File

/**
 * Class that acts as a service layer between controllers and filesystem data representation.
 * This is used to provide structured data to controllers based on job.
 */
class ChartGenerator(val job: SimulationJob) {
    private val log = NirdizatiLogger.getLogger(ChartGenerator::class)
    private val chartCache = Cache.chartCache[job.owner]
    private val handler = JSONHandler()

    /**
     * Get all available charts a job given in constructor
     *
     * @return all available charts for given log
     */
    fun getCharts(): List<Chart> {
        log.debug("Fetching charts for job with id ${job.id} for client ${job.owner}")
        val cached = chartCache
        return when (cached) {
            is CacheHolder<Chart> -> getFromCache(cached)
            else -> {
                // No cache for this client, need to fetch and cache
                fetchCharts().apply {
                    synchronized(Cache.chartCache) {
                        Cache.chartCache[job.owner] = CacheHolder()
                        log.debug("Created new slot in cache for ${job.owner}")
                        Cache.chartCache[job.owner]!!.addToCache(job.id, this)
                        log.debug("Added ${this.size} items to cache")
                    }
                }
            }
        }
    }

    /**
     * Retrieves logs from cache instead of fetching them from the filesystem or if charts are not found,
     * fetches them from filesystem.
     *
     * @param cached cache holder where to extract charts from
     * @return list of all available charts for current job
     */
    private fun getFromCache(cached: CacheHolder<Chart>): List<Chart> {
        log.debug("Fetching data for client exists")
        val charts = cached.retrieveFromCache(job.id)
        return if (charts.rawData().isEmpty()) {
            log.debug("Job ${job.id} is not cached for the client, fetching if from disk")
            // Fetch
            fetchCharts().apply {
                log.debug("Fetched ${this.size} items from disk")
                cached.addToCache(job.id, this)
                log.debug("Added items to cache")
            }
        } else {
            // is cached
            log.debug("Charts are cached, returning cached version")
            charts.rawData()
        }
    }

    /**
     * Collects all the charts into single list based on job type (classification vs regression)
     *
     * @return mutable list of charts
     */
    private fun fetchCharts(): MutableList<Chart> {
        val start = System.currentTimeMillis()
        val charts = mutableListOf<Chart>()

        if (LogManager.isClassification(job)) {
            charts.add(generateHeatMap())
            charts.addAll(generateLineCharts())
            charts.addAll(generateBarCharts())
        } else {
            charts.add(generateScatterPlot(TRUE_VS_PREDICTED))
            charts.addAll(generateLineCharts())
            charts.addAll(generateBarCharts())
        }

        val end = System.currentTimeMillis()
        log.debug("Fetching all charts finished in ${end - start} ms")

        return charts
    }

    /**
     * Generates scatter plot with a given name
     *
     * @param name of the chart
     * @see ScatterPlot
     * @return scatter plot entity filled with data for current job
     */
    private fun generateScatterPlot(name: String): ScatterPlot {
        val res = LogManager.getDetailedFile(job)

        return when (res) {
            is Right -> {
                val payload = getLinearPayload(res.result, Mode.SCATTER)
                ScatterPlot(name, handler.convert2String(payload), res.result)
            }
            is Left -> {
                log.error("Error when loading charts", res.error)
                ScatterPlot(res.error.message ?: "File not found", "{}", File("NONE"))
            }
        }
    }

    /**
     * Generates line charts for current job
     *
     * @see LineChart
     * @return list of line charts for current job filled with data for current job
     */
    private fun generateLineCharts(): List<LineChart> {
        val res = LogManager.getValidationFile(job)

        return when (res) {
            is Right -> {
                val payload = getLinearPayload(res.result, Mode.LINE).groupBy { it.dataType }
                var charts = listOf<LineChart>()
                payload.forEach { charts += LineChart(job.id, it.key, handler.convert2String(it.value), it.value.last().x.toInt(), res.result) }
                charts
            }

            is Left -> {
                log.error("Error when loading line charts", res.error)
                return listOf()
            }
        }
    }

    /**
     * Generates bar charts for current job
     *
     * @see BarChart
     * @return list of bar charts for current job filled with data for current job
     */
    private fun generateBarCharts(): List<BarChart> {
        val files = LogManager.getFeatureImportanceFiles(job)
        val charts = mutableListOf<BarChart>()

        when (files) {
            is Right -> {
                (1..files.result.size).zip(files.result).forEach {
                    val payload = getBarChartPayload(it.second)
                    charts.add(
                            BarChart(
                                    it.first.toString(),
                                    handler.convert2String(payload.map { it.value }),
                                    handler.convert2String(payload.map { it.label }),
                                    it.second
                            )
                    )
                }
            }
            is Left -> log.error("Error occurred when fetching feature importance files", files.error)
        }

        return charts.toList()
    }

    /**
     * Generates heat map for given job
     *
     * @see HeatMap
     * @return HeatMap entity filled with data for current job
     */
    private fun generateHeatMap(): HeatMap {
        val file = LogManager.getDetailedFile(job)

        return when (file) {
            is Right -> {
                val heatMap = getHeatMapPayload(file.result)
                HeatMap(
                        TRUE_VS_PREDICTED,
                        handler.convert2String(heatMap.data.map { arrayOf(it.x, it.y, it.value) }),
                        handler.convert2String(heatMap.xLabels),
                        handler.convert2String(heatMap.yLabels),
                        file.result
                )
            }

            is Left -> {
                log.error("Error occurred when loading heat map", file.error)
                HeatMap(
                        file.error.message ?: "File not found",
                        "{}",
                        "{}",
                        "{}",
                        File("NONE")
                )
            }
        }
    }

    companion object {
        const val TRUE_VS_PREDICTED = "true_vs_predicted"
    }
}