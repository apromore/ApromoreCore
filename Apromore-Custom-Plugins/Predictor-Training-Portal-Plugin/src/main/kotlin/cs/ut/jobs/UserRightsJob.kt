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

package cs.ut.jobs

import cs.ut.configuration.ConfigFetcher
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import java.io.File
import java.nio.charset.Charset

/**
 * Job that updates user rights for a given file to
 * match user rights specified in the configuration
 *
 * @param f to update user rights for
 */
class UserRightsJob(private val f: File) : Job() {
    override fun execute() {
        log.debug("Starting ACL job for $id")

        log.debug("Setting permission ${configNode.valueWithIdentifier("acp").value}")
        updateACL(f)

        log.debug("Changing rights for training JSON")
        val name: String = f.nameWithoutExtension

        val path = "${DirectoryConfiguration.dirPath(Dir.DATA_DIR)}$name.json"
        log.debug("Looking for file -> $path")

        updateACL(File(path))
        log.debug("Changing rights for JSON successfully finished")
    }


    companion object {
        private val configNode by ConfigFetcher("userPreferences")

        val log = NirdizatiLogger.getLogger(UserRightsJob::class)

        /**
         * Update user rights and ownership for file
         *
         * @param f to update rights for
         */
        fun updateACL(f: File) {
            if (!configNode.isEnabled()) {
                log.debug("ACL updating is disabled -> skipping")
                return
            }

            updateOwnership(f)
            updateRights(f)
        }

        /**
         * Update user rights for the file
         *
         * @param f to update user rights for
         */
        private fun updateRights(f: File) {
            log.debug("Updating ACL -> $f")

            val command = if (f.isDirectory) arrayOf("chmod", "-R") else arrayOf("chmod")
            val pb = ProcessBuilder(
                    "sudo",
                    *command,
                    configNode.valueWithIdentifier("acp").value,
                    f.absolutePath
            )
            pb.inheritIO()
            log.debug("Running -> ${pb.command()}")

            val process = pb.start()
            process.waitFor()
        }

        /**
         * Update ownership for the file
         *
         * @param f to update ownership for
         */
        private fun updateOwnership(f: File) {
            val userName = configNode.valueWithIdentifier("userName").value
            val userGroup = configNode.valueWithIdentifier("userGroup").value

            log.debug("Updating ownership for $f -> " +
                    "new owner $userName:$userGroup")
            val pb = ProcessBuilder(
                    "sudo",
                    "-S",
                    "chown",
                    "$userName:$userGroup",
                    f.absolutePath
            )

            log.debug("Command -> ${pb.command()}")
            val process = pb.start()
            val sudo = configNode.valueWithIdentifier("sudo").value
            if (sudo.isNotEmpty()) {
                process.outputStream.write((sudo + "\n\r").toByteArray(Charset.forName("UTF-8")))
            }

            process.waitFor()
            log.debug("Ownership updated for $f")
        }
    }
}