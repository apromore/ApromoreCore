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

import cs.ut.logging.NirdizatiLogger
import org.apache.commons.codec.binary.Hex
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.Calendar

/**
 * Has to be singleton in order to be thread safe. If used from different threads there is a danger of collision
 * if id is going to be generated at the same time (which is unlikely but possible)
 */
object IdProvider {
    private val log= NirdizatiLogger.getLogger(IdProvider::class)

    private val digest = MessageDigest.getInstance("MD5")!!
    private var previous: String = ""

    /**
     * Generate unique identifier for the job
     *
     * @return unique identifier
     */
    fun getNextId(): String {
        log.debug("New id requested -> generating id using ${digest.algorithm}")
        var iteration = 1

        var new: String = getNextHash()

        synchronized(this) {
            while (previous == new) {
                new = getNextHash()
                iteration += 1
            }

            log.debug("Finished id generation in $iteration iterations")
            previous = new
        }

        log.debug("New id is -> $new")
        return new

    }

    /**
     * Generates hash that might be used as a key
     *
     * @return unique hash based on digest algorithm
     */
    private fun getNextHash(): String {
        val time = Calendar.getInstance().time.toInstant().toString()
        return String(Hex.encodeHex(digest.digest(time.toByteArray(Charset.forName("UTF-8")))))
    }
}