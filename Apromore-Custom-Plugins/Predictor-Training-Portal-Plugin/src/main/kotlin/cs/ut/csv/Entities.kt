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