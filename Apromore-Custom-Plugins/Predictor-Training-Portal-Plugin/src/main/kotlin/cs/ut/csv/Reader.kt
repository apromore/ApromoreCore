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

package cs.ut.csv

import cs.ut.configuration.ConfigurationReader
import cs.ut.exceptions.Either
import cs.ut.exceptions.Left
import cs.ut.exceptions.NirdizatiRuntimeException
import cs.ut.exceptions.Right
import cs.ut.logging.NirdizatiLogger
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.File

data class Reader(val dataSource: DataSource<String>) {
    private var header: List<String> = listOf()

    private lateinit var deferred: Deferred<Unit>

    private lateinit var classRes: MainColumns
    private var cases: MutableMap<String, Case> = mutableMapOf()

    fun detectUserColumns(): Either<Exception, MainColumns> {
        val start = System.currentTimeMillis()

        var res = MainColumns()

        var escapedHeader: List<String>
        var escapedContent: List<String>
        dataSource.use {
            val headerLine: String = it.readOne()
            val content: String = it.readOne()

            escapedHeader = escapeCSV(headerLine).split(splitter)
            if (escapedHeader.size < 3) {
                return Left(NirdizatiRuntimeException("Log has less that 3 columns"))
            }
            log.debug("Read header of the file: found ${escapedHeader.size} columns")

            escapedContent = escapeCSV(content).split(splitter)
            if (escapedContent.size < 3 || escapedContent.size != escapedHeader.size) {
                return Left(NirdizatiRuntimeException("Inconsistent separation: header has ${escapedHeader.size} and content has ${escapedContent.size}"))
            }

            var caseId = ""
            var activity = ""
            var resource = ""
            escapedHeader.forEach { col ->
                caseId = if (caseId == "" && caseValues.any { it == col }) col else caseId
                activity = if (activity == "" && activityValues.any { it == col }) col else activity
                resource = if (resource == "" && resourceValues.any { it == col }) col else resource
            }
            header = escapedHeader
            log.debug("Header classification finished: case id=$caseId, activity=$activity, resource=$resource")

            var timestamp = ""
            escapedContent.forEach { col ->
                timestamp = if (timestamp == "" && dateFormats.any { it.matches(col) }) col else timestamp
            }
            log.debug("Finished with content parsing: timestamp=$timestamp")

            res = MainColumns(caseId, activity, timestamp, resource)
            classRes = res
        }

        deferred = async {
            val caseId = classRes.caseId
            val index = header.indexOf(caseId)

            dataSource.reset()
            dataSource.skip(1)

            dataSource.stream().forEach {
                val attrs = escapeCSV(it).split(splitter)
                val case = cases[attrs[index]] ?: Case(attrs[index])

                attrs.withIndex().forEach { (i, col) ->
                    if (i != index) {
                        val attrName = header[i]
                        val attribute: Attribute? = case.attributes.firstOrNull { it.name == attrName }

                        if (attribute == null) {
                            val a = Attribute(attrName, hashSetOf(col))
                            case.attributes.add(a)
                        } else {
                            attribute.values.add(col)
                        }
                    }
                }

                if (attrs[index] !in cases) cases[attrs[index]] = case

                if (cases.size == 100) return@forEach
            }

            val eventAttributes: MutableSet<String> = mutableSetOf()

            val attrs = cases.flatMap { it.value.attributes }
            for (attr in attrs) {
                if (attr.isEventAttribute) {
                    eventAttributes.add(attr.name)
                }
            }

            val isColumnNumeric: Map<String, Boolean> = attrs
                    .groupBy { it.name }
                    .mapValues {
                        it.value
                                .flatMap { it.values }
                                .map { it.toFloatOrNull() }
                    }
                    .mapValues { null !in it.value && it.value.size >= threshold }

            dataSource.close()
        }

        val end = System.currentTimeMillis()

        log.debug("Result acquired in ${end - start} ms.")
        return Right(res)
    }

    suspend fun getCases(): Map<String, Case> {
        return if (deferred.isCompleted) {
            cases
        } else {
            deferred.await()
            cases
        }
    }

    private fun escapeCSV(row: String): String {
        return row.replace(escapeNode.valueWithIdentifier("regex").value.toRegex()
                , transform = {
            var item = it.value
            escapeNode.childNodes.first { it.identifier == "replacement" }.values.forEach {
                item = item.replace(it.identifier, it.value)
            }
            item
        })
    }

    companion object {
        private val log = NirdizatiLogger.getLogger(Reader::class)
        private val escapeNode = ConfigurationReader.findNode("csv/escape")

        private val configNode = ConfigurationReader.findNode("csv")
        private val splitter = configNode.valueWithIdentifier("splitter").value.toRegex()
        private val activityValues = ConfigurationReader.findNode("csv/activityId").itemListValues()
        private val caseValues = ConfigurationReader.findNode("csv/caseId").itemListValues()
        private val resourceValues = ConfigurationReader.findNode("csv/resource").itemListValues()
        private val threshold: Int = configNode.valueWithIdentifier("threshold").value()

        private val dateFormats = ConfigurationReader.findNode("csv/timestamp").itemListValues().map { it.toRegex() }
    }
}

fun main(args: Array<String>) {
    val dataSource = FileDataSource(File("/home/zukkari/Downloads/nirdizati/BPI2012A.csv"))
    val reader = Reader(dataSource)
    println(reader.detectUserColumns())

    runBlocking { println(reader.getCases()) }
}
