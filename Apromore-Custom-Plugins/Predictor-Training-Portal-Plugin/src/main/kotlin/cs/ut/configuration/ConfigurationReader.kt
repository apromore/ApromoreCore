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

package cs.ut.configuration

import cs.ut.logging.NirdizatiLogger
import kotlin.reflect.KProperty

class ConfigFetcher(val path: String) {
    operator fun getValue(caller: Any, prop: KProperty<*>): ConfigNode {
        return ConfigurationReader.findNode(path).apply {
            log.debug("Delegating $this to caller $caller")
        }
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(ConfigFetcher::class)
    }
}

/**
 * Helper object that reads traverses the configuration tree
 *
 * @see Configuration
 */
object ConfigurationReader {
    private var configuration = Configuration.readSelf()

    private const val pathDelimiter = "/"

    /**
     * Reloads the configuration into memory
     */
    fun reload() {
        configuration = Configuration.readSelf()
    }

    /**
     * Find configuration node based on given path.
     * Path should be delimited using delimiter as the "pathDelimiter" value
     *
     * @param path to look for
     * @return configuration node corresponding to given path
     */
    fun findNode(path: String): ConfigNode {
        var currentNode: ConfigNode? = null

        path.split(pathDelimiter).forEach { p ->
            val nodes = if (currentNode == null) configuration.childNodes else currentNode!!.childNodes
            currentNode = nodes.first { it.identifier == p }
        }

        return currentNode ?: throw NoSuchElementException("Node with path $path could not be found")
    }
}