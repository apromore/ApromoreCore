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

package cs.ut.providers

import cs.ut.configuration.ConfigFetcher
import org.apromore.plugin.portal.predictortraining.PortalPlugin

/**
 * Object that provides easy access to configured directories
 */
object DirectoryConfiguration {

    private const val NODE_NAME = "directories"
    private val configNode by ConfigFetcher(NODE_NAME)

    private val dirs: Map<String, String>

    init {
        dirs = mutableMapOf<String, String>().apply {
            configNode.itemList().forEach {
                this[it.identifier] = it.value
            }
        }
    }

    /**
     * Get directory path
     *
     * @param dir to get path for
     */
    fun dirPath(dir: Dir): String {
        return when(dir) {
            Dir.PYTHON   -> PortalPlugin.python!!
            Dir.TMP_DIR  -> PortalPlugin.tmpDir!!
            Dir.LOG_FILE -> PortalPlugin.logFile!!
            else         -> "${PortalPlugin.backend!!}${dirs[dir.value()]!!}"
        }
    }
}

enum class Dir(private val id: String) {
    PYTHON("python"),
    USER_LOGS("userLogDirectory"),
    SCRIPT_DIR("scriptDirectory"),
    TRAIN_DIR("trainDirectory"),
    DATA_DIR("datasetDirectory"),
    PKL_DIR("pklDirectory"),
    OHP_DIR("ohpdir"),
    DETAIL_DIR("detailedDir"),
    FEATURE_DIR("featureDir"),
    VALIDATION_DIR("validationDir"),
    TMP_DIR("tmpDir"),
    CORE_DIR("coreDir"),
    LOG_FILE("logFile");

    fun value(): String = id
}
