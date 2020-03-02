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

package cs.ut.util

import cs.ut.configuration.ConfigurationReader
import cs.ut.engine.LogManager
import cs.ut.exceptions.Either
import cs.ut.exceptions.Left
import cs.ut.exceptions.Right
import cs.ut.jobs.SimulationJob
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import org.zkoss.zul.Filedownload
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class NirdizatiDownloader(private val dir: Dir, private val resourceId: String) {
    init {
        log.debug("Nirdizati downloader created with resource -> $resourceId")
    }

    fun execute() {
        log.debug("Executing download operation for resource $resourceId")
        val file = File(DirectoryConfiguration.dirPath(dir))
        val downloadFile = file.listFiles().first { it.name.contains(resourceId) }
        Filedownload.save(FileInputStream(downloadFile), getMime(downloadFile.extension), downloadFile.name)
        log.debug("Finished file download")
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(NirdizatiDownloader::class)
        private val configNode = ConfigurationReader.findNode("downloads")


        fun downloadFilesAsZip(job: SimulationJob) {
            fun prepareZip(featureFiles: Either<Exception, List<File>>, detailedFile: Either<Exception, File>, accuracyFile: Either<Exception, File>): String {
                val fileName = "validation_results_${job.id}"
                val configNode = ConfigurationReader.findNode("downloads/zip")

                log.debug("Started zip composition")
                val start = System.currentTimeMillis()

                val env = mapOf("create" to "true")
                val fileURI = URI.create(DirectoryConfiguration.dirPath(Dir.TMP_DIR) + "$fileName.zip")
                val zipURI = URI(
                        configNode.valueWithIdentifier(ZipDirs.SCHEME.value).value,
                        fileURI.path,
                        null)

                val dirNode = configNode.childNodes.first { it.identifier == "dirNames" }
                FileSystems.newFileSystem(zipURI, env).use { fs ->
                    val featureImportanceDir = dirNode.valueWithIdentifier(ZipDirs.FEATURES.value).value
                    val detailedDir = dirNode.valueWithIdentifier(ZipDirs.DETAILED.value).value
                    val accuracyDir = dirNode.valueWithIdentifier(ZipDirs.ACCURACY.value).value

                    Files.createDirectory(fs.getPath(featureImportanceDir))
                    Files.createDirectory(fs.getPath(detailedDir))
                    Files.createDirectory(fs.getPath(accuracyDir))

                    when (featureFiles) {
                        is Right -> {
                            featureFiles.result.forEach {
                                Files.copy(it.toPath(), fs.getPath(featureImportanceDir + File.separator + it.name), StandardCopyOption.REPLACE_EXISTING)
                            }
                        }

                        is Left -> log.error("Error occurred when fetching feature importance files", featureFiles.error)
                    }

                    when (detailedFile) {
                        is Right -> Files.copy(
                                detailedFile.result.toPath(),
                                fs.getPath(detailedDir + File.separator + detailedFile.result.name),
                                StandardCopyOption.REPLACE_EXISTING)

                        is Left -> log.error("Error occurred when fetching detailed file", detailedFile.error)
                    }

                    when (accuracyFile) {
                        is Right -> Files.copy(
                                accuracyFile.result.toPath(),
                                fs.getPath(accuracyDir + File.separator + accuracyFile.result.name),
                                StandardCopyOption.REPLACE_EXISTING)

                        is Left -> log.error("Error occurred when fetching accuracy file", accuracyFile.error)
                    }
                }

                val end = System.currentTimeMillis()
                log.debug("Zip composition finished in ${end - start} ms")
                return fileURI.path
            }

            val start = System.currentTimeMillis()

            val featureFiles = LogManager.getFeatureImportanceFiles(job)
            val detailedFiles = LogManager.getDetailedFile(job)
            val accuracyFiles = LogManager.getValidationFile(job)

            val toDownload = File(prepareZip(featureFiles, detailedFiles, accuracyFiles))
            Filedownload.save(FileInputStream(toDownload), getMime(toDownload.extension), toDownload.name)

            log.debug("Download executed, deleting file")
            if (toDownload.delete()) log.debug("Successfully deleted temp file -> ${toDownload.absoluteFile}")
            else log.debug("Failed to delete temp file -> ${toDownload.absoluteFile}")

            val end = System.currentTimeMillis()
            log.debug("Finished ZIP download in ${end - start} ms")
        }

        fun executeOnSingleFile(file: File) {
            if (file.path.contains("NONE")) return

            log.debug("Executing download on a single file: ${file.absoluteFile}")
            val start = System.currentTimeMillis()

            Filedownload.save(FileInputStream(file), getMime(file.extension), file.name)

            val end = System.currentTimeMillis()
            log.debug("Finished download execution of a single file in ${end - start} ms")
        }

        private fun getMime(extension: String): String {
            val node = configNode.childNodes.firstOrNull { it.identifier == extension }
                    ?: configNode.childNodes.first { it.identifier == "default" }
            return node.valueWithIdentifier("mime").value
        }
    }
}