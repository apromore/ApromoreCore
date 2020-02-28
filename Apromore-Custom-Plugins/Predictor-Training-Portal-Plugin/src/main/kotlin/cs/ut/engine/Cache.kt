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

package cs.ut.engine

import cs.ut.charts.Chart
import cs.ut.exceptions.Right
import cs.ut.jobs.JobStatus
import cs.ut.jobs.SimulationJob
import cs.ut.json.JSONService
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import java.io.File
import java.nio.file.Files
import java.util.Date
//import kotlin.streams.asSequence
//import kotlin.streams.toList

/**
 * Generic structure to represent cached items
 *
 * @param items list where to hold cached items
 */
data class CacheItem<T>(private val items: MutableList<T> = mutableListOf()) {
    private var lastAccessed: Date = Date()

    /**
     * Add item to cache
     *
     * @param item to add
     */
    fun addItem(item: T) {
        lastAccessed = Date()
        items.add(item)
    }

    /**
     * Add multiple items to cache
     *
     * @param items collection of items to add
     */
    fun addItems(items: Collection<T>) {
        lastAccessed = Date()
        this.items.addAll(items)
    }

    /**
     * Retrieve raw items from cache
     */
    fun rawData(): List<T> {
        lastAccessed = Date()
        return items
    }

    fun removeItem(item: T) {
        this.items.remove(item)
    }

    /**
     * Is item expired
     * @param timeToLive how long is item supposed to live
     * @return whether or not item is expired
     */
    fun isExpired(timeToLive: Long): Boolean {
        return Date().time - lastAccessed.time > timeToLive
    }
}

/**
 * Generic structure to hold cached items
 */
open class CacheHolder<T> {
    protected val cachedItems = mutableMapOf<String, CacheItem<T>>()

    /**
     * Add item to existing cache if it exists or create a new item
     *
     * @param key to look for
     * @param item to insert into cache
     *
     * @return cached item
     *
     * @see CacheItem
     */
    open fun addToCache(key: String, item: T) = (cachedItems[key] ?: createNewItem(key)).addItem(item)

    /**
     *  Add a collection of items to cache if it exists or create a new item
     *
     *  @param key to look for
     *  @param items to insert into cache
     *
     *  @return cached item
     *
     *  @see CacheItem
     */
    open fun addToCache(key: String, items: Collection<T>) = (cachedItems[key] ?: createNewItem(key)).addItems(items)

    open fun retrieveFromCache(key: String): CacheItem<T> = cachedItems[key] ?: CacheItem()

    private fun createNewItem(key: String): CacheItem<T> = CacheItem<T>().apply { cachedItems[key] = this }

    fun cachedItems() = cachedItems

    fun flush() = cachedItems.clear()
}

/**
 * Implementation of CacheHolder that holds Simulation jobs
 *
 * @see CacheHolder
 * @see SimulationJob
 */
class JobCacheHolder : CacheHolder<SimulationJob>() {

    override fun retrieveFromCache(key: String): CacheItem<SimulationJob> {
        val existing: CacheItem<SimulationJob>? = cachedItems[key]

        return when (existing) {
            is CacheItem<SimulationJob> -> {
                log.debug("Retrieved item from cache for key -> $key")
                existing
            }
            else -> {
                log.debug("Jobs for key $key not cached, fetching from disk")
                fetchFromDisk(key)
            }
        }
    }

    /**
     * Fetches jobs from disk based on given key
     *
     * @param key job to find
     *
     * @return cached item with simulation job as content
     *
     * @see CacheItem
     */
    private fun fetchFromDisk(key: String): CacheItem<SimulationJob> =
            CacheItem<SimulationJob>().apply {
                val items = trainingFiles(key)
                this.addItems(items)
                cachedItems[key] = this
            }

    fun findJob(key: String) = cachedItems.flatMap { it.value.rawData() }.firstOrNull { it.id == key }
            ?: simulationJobs().firstOrNull { it.id == key }


    companion object {
        val log = NirdizatiLogger.getLogger(JobCacheHolder::class)

        fun trainingFiles(key: String): List<SimulationJob> = simulationJobs().filter { it.owner == key }.toList()

        fun simulationJobs(): List<SimulationJob> {
            val trainingDir = File(DirectoryConfiguration.dirPath(Dir.TRAIN_DIR)).toPath()

            val jobs = Files.walk(trainingDir)
                    .map { it.toFile().nameWithoutExtension to JSONService.getTrainingConfig(it.toFile().nameWithoutExtension) }
                    .filter { it.second is Right }
                    .map {
                        val config = (it.second as Right).result
                        SimulationJob(config, File(config.info.logFile), config.info.owner, it.first).apply {
                            this.startTime = config.info.startTime
                            this.status = JobStatus.COMPLETED
                        }
                    }
                    .filter { it.id !in JobManager.queue.map { it.id } }
                    //.toList()

            val list: MutableList<SimulationJob> = mutableListOf()
            jobs.forEach { JobManager.cache.addToCache(it.owner, it); list.add(it) }

            return list
            //return jobs
        }
    }
}

/**
 * Object that holds job and chart cache in Nirdizati Training System
 */
object Cache {
    var jobCache = JobCacheHolder()

    var chartCache: MutableMap<String, CacheHolder<Chart>> = mutableMapOf()
}

