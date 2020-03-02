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

package cs.ut.csv

import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.stream.Stream

typealias Attributes = MutableList<Attribute>

data class Case(val id: String,
                var attributes: Attributes) {
    constructor(caseId: String) : this(caseId, mutableListOf<Attribute>())
}


data class Attribute(val name: String, var values: MutableSet<String> = hashSetOf()) {

    val isEventAttribute by lazy { values.size == 1 }
}

data class MainColumns(
        val caseId: String,
        val activity: String,
        val timestamp: String,
        val resource: String
) {
    constructor() : this("", "", "", "")
}


enum class ColumnValue(val value: String) {
    CASE_ID("case_id"),
    ACTIVITY("activity"),
    TIMESTAMP("timestamp_col")
}

abstract class DataSource<T> : Closeable {

    abstract fun reset()

    abstract fun stream(): Stream<T>

    abstract fun readOne(): T

    abstract fun skip(n: Int)
}

data class FileDataSource(val f: File) : DataSource<String>() {
    private var reader: BufferedReader? = null

    override fun reset() {
        if (reader == null) {
            return
        } else {
            reader!!.close()
            reader = createReader()
        }
    }

    override fun readOne(): String {
        return if (reader != null) {
            reader!!.readLine()
        } else {
            reader = createReader()
            reader!!.readLine()
        }
    }

    override fun stream(): Stream<String> {
        if (reader == null) {
            reader = createReader()
        }

        return reader!!.lines()
    }

    override fun close() {
        if (reader != null) {
            reader!!.close()
            reader = null
        } else {
            throw IOException("Stream closed")
        }
    }

    override fun skip(n: Int) {
        if (n < 1) {
            throw IllegalArgumentException("Cannot skip less that 1 lines")
        }

        if (reader == null) {
            reader = createReader()
        }

        for (i in 0 until n) {
            reader!!.readLine()
        }
    }

    private fun createReader() = BufferedReader(FileReader(f))
}