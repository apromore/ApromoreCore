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

package cs.ut.util

import cs.ut.configuration.ConfigurationReader
import cs.ut.logging.NirdizatiLogger
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStream
import java.io.Reader

interface UploadItem {

    val bufferSize: Int
        get() = ConfigurationReader.findNode("fileUpload").valueWithIdentifier("uploadBufferSize").value()

    fun write(file: File)

}

class NirdizatiReader(private val reader: Reader) : UploadItem {

    override fun write(file: File) {
        var total = 0

        val buffer = CharArray(bufferSize)

        var read = reader.read(buffer)

        FileWriter(file).use {
            while (read != -1) {
                if (read < bufferSize) {
                    it.write(buffer.sliceArray(0 until read))
                } else {
                    it.write(buffer)
                }

                total += read
                read = reader.read(buffer)
            }
        }

        log.debug("Read total of $total bytes for file ${file.name}")
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(NirdizatiReader::class)
    }

}

class NirdizatiInputStream(private val inputStream: InputStream) : UploadItem {

    override fun write(file: File) {
        val buffer = ByteArray(bufferSize)
        var total = 0

        var read = inputStream.read(buffer)
        FileOutputStream(file).use {
            while (read != -1) {
                if (read < bufferSize) {
                    it.write(buffer.sliceArray(0 until read))
                } else {
                    it.write(buffer)
                }

                total += read
                read = inputStream.read(buffer)
            }
        }

        log.debug("Read total of $total bytes for file ${file.name}")
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(NirdizatiInputStream::class)
        private const val NULL: Byte = 0x00
    }
}