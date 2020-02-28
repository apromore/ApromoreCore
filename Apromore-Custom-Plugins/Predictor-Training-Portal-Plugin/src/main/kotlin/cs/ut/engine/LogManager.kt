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

// import org.json.JSONObject
import cs.ut.configuration.ConfigurationReader
import cs.ut.exceptions.Either
import cs.ut.exceptions.Left
import cs.ut.exceptions.NirdizatiRuntimeException
import cs.ut.exceptions.Right
import cs.ut.jobs.SimulationJob
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import cs.ut.util.Node
import org.apache.commons.io.FilenameUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

import org.apromore.model.LogSummaryType
import org.apromore.plugin.portal.predictortraining.PortalPlugin

/**
 * Responsible for communication between file system and managing user log files
 */
object LogManager {
    private val log = NirdizatiLogger.getLogger(LogManager::class)

    private const val REGRESSION = "_regr"
    private const val CLASSIFICATION = "_class"
    private const val DETAILED = "detailed_"
    private const val FEATURE = "feat_importance_"
    private const val VALIDATION = "validation_"

    private val eventNumber: Int

    private val allowedExtensions: List<String>

    private val logDirectory: String
    private val validationDir: String
    private val featureImportanceDir: String
    private val detailedDir: String

    init {
        log.debug("Initializing $this")

        logDirectory = DirectoryConfiguration.dirPath(Dir.USER_LOGS)
        log.debug("User log directory -> $logDirectory")

        validationDir = DirectoryConfiguration.dirPath(Dir.VALIDATION_DIR)
        log.debug("Validation directory -> $validationDir")

        featureImportanceDir = DirectoryConfiguration.dirPath(Dir.FEATURE_DIR)
        log.debug("Feature importance directory -> $featureImportanceDir")

        detailedDir = DirectoryConfiguration.dirPath(Dir.DETAIL_DIR)
        log.debug("Detailed log directory -> $detailedDir")

        allowedExtensions = ConfigurationReader.findNode("fileUpload/extensions").itemListValues()

        eventNumber = ConfigurationReader
                .findNode("models/parameters/prefix_length_based")
                .valueWithIdentifier(Node.EVENT_NUMBER.value)
                .value()
    }

    /**
     * Returns all available file names contained in user log directory defined in configuration.xml
     *
     * @return List of all available file names contained in user log directory
     */
    fun getAllAvailableLogs(): List<LogSummaryType> = PortalPlugin.globalSelectedLogSummaryList

    /**
     * Returns file from configured detailed directory that is made as result of given job
     *
     * @param job for which job file should be retrieved
     * @return file that contains job results
     */
    fun getDetailedFile(job: SimulationJob, safe: Boolean = false): Either<Exception, File> {
        log.debug("Getting detailed log information for job '$job'")
        return getFile(detailedDir + job.getFileName(DETAILED), safe)
    }

    /**
     * Returns file from configured validation directory that is made as result of given job
     *
     * @param job for which job file should be retrieved
     * @return file that contains job results
     */
    fun getValidationFile(job: SimulationJob, safe: Boolean = false): Either<Exception, File> {
        log.debug("Getting validation log file for job '$job'")
        return getFile(validationDir + job.getFileName(VALIDATION), safe)
    }

    /**
     * Returns feature importance files for given job
     *
     * @param job to retrieve feature importance files for
     *
     * @return list of files that represent feature importance files for given job
     */
    fun getFeatureImportanceFiles(job: SimulationJob): Either<Exception, List<File>> {
        log.debug("Getting feature importance log information for job: '$job'")

        return try {
            Right(File(featureImportanceDir)
                    .listFiles()
                    .asSequence()
                    .filter { job.id in it.name }
                    .toList())
        } catch (e: Exception) {
            Left(e)
        }
    }

    /**
     * Get file with given name and make sure that file exists
     *
     * @param fileName to find
     *
     * @return file with given file name
     */
    private fun getFile(fileName: String, safe: Boolean): Either<Exception, File> {
        val file = File("$fileName.csv")
        log.debug("Looking for file with name ${file.name}")

        if (!safe && !file.exists()) {
            return Left(NirdizatiRuntimeException("Result file with name ${file.absolutePath} could not be found"))
        }

        log.debug("Successfully found result file with name $fileName")
        return Right(file)
    }

    /**
     * Returns whether given job is classification or regression
     * @param job that needs to be categorized
     */
    fun isClassification(job: SimulationJob): Boolean =
            !File(detailedDir + DETAILED + FilenameUtils.getBaseName(job.logFile.name) + "_" + job.id + REGRESSION + ".csv").exists()

    /**
     * Get file name for given job
     *
     * @param dir to include with the file name
     */
    private fun SimulationJob.getFileName(dir: String): String =
            if (dir == FEATURE)
                dir + this.logFile.nameWithoutExtension + "_" + this.id
            else
                dir + this.logFile.nameWithoutExtension + "_" + this.id + if (isClassification(this)) CLASSIFICATION else REGRESSION

    /**
     * Read contents of the file and return it as string data
     *
     * @param f file to read from
     *
     * @return contents of the file as string data
     */
    private fun readFileContent(f: File): String = BufferedReader(FileReader(f)).readLines().joinToString()

    /**
     * Load all training files found in configured training directory
     *
     * @return list of files in training directory
     */
    private fun loadTrainingFiles(): List<File> {
        log.debug("Loading training files")
        val dir = File(DirectoryConfiguration.dirPath(Dir.TRAIN_DIR))
        log.debug("Looking for training files in ${dir.absolutePath}")
        val files = dir.listFiles() ?: arrayOf()
        log.debug("Found ${files.size} training files total")
        return files.toList()
    }
}
