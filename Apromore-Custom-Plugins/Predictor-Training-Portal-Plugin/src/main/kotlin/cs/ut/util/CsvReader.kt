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

import cs.ut.configuration.ConfigNode
import cs.ut.configuration.ConfigurationReader
import cs.ut.configuration.Value
import cs.ut.engine.item.Case
import cs.ut.exceptions.NirdizatiRuntimeException
import cs.ut.logging.NirdizatiLogger
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.Double.max
import java.util.Collections
import java.util.HashMap
import java.util.LinkedHashMap

class CsvReader(private val f: File) {
    private val log = NirdizatiLogger.getLogger(CsvReader::class)

    private val splitter: Regex
    private val emptyValues: List<String>
    private val confThreshold: Int
    private val sampleSize: Int
    private val caseId: List<String>
    private val activityId: List<String>
    private val dateFormats: List<Regex>
    private val resourceId: List<String>
    private val escapeNode: ConfigNode

    init {
        log.debug("Initializing csv reader...")

        val configNode = ConfigurationReader.findNode("csv")
        splitter = configNode.valueWithIdentifier("splitter").value.toRegex()
        confThreshold = configNode.valueWithIdentifier("threshold").value()
        sampleSize = configNode.valueWithIdentifier("sampleSize").value()

        emptyValues = ConfigurationReader.findNode("csv/empty").itemListValues()
        activityId = ConfigurationReader.findNode("csv/activityId").itemListValues()
        caseId = ConfigurationReader.findNode("csv/caseId").itemListValues()
        dateFormats = ConfigurationReader.findNode("csv/timestamp").itemListValues().map { it.toRegex() }
        resourceId = ConfigurationReader.findNode("csv/resource").itemListValues()

        escapeNode = ConfigurationReader.findNode("csv/escape")

        log.debug("Finished initializing csv reader...")
    }

    fun readTableHeader(): List<String> {
        log.debug("Reading table header...")
        BufferedReader(FileReader(f)).use { return it.readLine().split(splitter) }
    }

    tailrec fun identifyUserColumns(cols: List<String>, result: MutableMap<String, String>) {
        if (cols.isNotEmpty()) {
            val head = cols.first()

            identifyColumn(head, caseId.toMutableList(), IdentColumns.CASE_ID.value, result)
            identifyColumn(head, activityId.toMutableList(), IdentColumns.ACTIVITY.value, result)
            identifyColumn(head, resourceId.toMutableList(), IdentColumns.RESOURCE.value, result)
            identifyUserColumns(cols.drop(1), result)
        }
    }

    private tailrec fun identifyColumn(
            col: String,
            ids: MutableList<String>,
            type: String,
            result: MutableMap<String, String>
    ) {
        if (ids.isNotEmpty()) {
            if (col.toLowerCase() in ids.first()) {
                result[type] = col
            } else {
                identifyColumn(col, ids.drop(1).toMutableList(), type, result)
            }
        }
    }

    fun generateDataSetParams(userCols: MutableMap<String, Any>): MutableMap<String, MutableList<String>> {
        val start = System.currentTimeMillis()
        val case = userCols[IdentColumns.CASE_ID.value] ?: throw NirdizatiRuntimeException("No case id column in log")
        val cases = parseCsv(case as String)

        val colValues = HashMap<String, MutableSet<String>>()

        cases.forEach {
            it.attributes.remove(userCols[IdentColumns.TIMESTAMP.value])
            it.attributes.remove(userCols[IdentColumns.CASE_ID.value])
            it.attributes.remove(userCols[IdentColumns.ACTIVITY.value])
            it.attributes.remove(userCols[IdentColumns.RESOURCE.value])

            if (it.attributes.keys.contains(Algorithm.REMTIME.value)) {
                it.attributes.remove(Algorithm.REMTIME.value)
            }

            if (it.attributes.keys.contains(Algorithm.OUTCOME.value)) {
                it.attributes.remove(Algorithm.OUTCOME.value)
            }

            classifyColumns(it)

            it.attributes.forEach { k, v ->
                if (colValues.containsKey(k)) colValues[k]!!.addAll(v)
                else {
                    colValues[k] = mutableSetOf()
                    colValues[k]!!.addAll(v)
                }
            }
        }

        val alreadyClassified = mutableSetOf<String>()
        val resultCols = mutableMapOf<String, MutableList<String>>()
        resultCols[Columns.STATIC_CAT_COLS.value] = mutableListOf()
        resultCols[Columns.STATIC_NUM_COLS.value] = mutableListOf()
        resultCols[Columns.DYNAMIC_CAT_COLS.value] = mutableListOf()
        resultCols[Columns.DYNAMIC_NUM_COLS.value] = mutableListOf()

        cases.forEach { c ->
            c.dynamicCols.forEach { insertIntoMap(c.classifiedColumns, ColumnPart.DYNAMIC.value, it, colValues[it]) }
            c.staticCols.forEach { insertIntoMap(c.classifiedColumns, ColumnPart.STATIC.value, it, colValues[it]) }
            postProcessCase(resultCols, c, alreadyClassified)
        }

        userCols.forEach { k, v -> resultCols[k] = Collections.singletonList(v as String) }
        val end = System.currentTimeMillis()
        log.debug("Finished generating data set params in ${end - start} ms")

        return resultCols
    }

    private fun postProcessCase(
            resultCols: MutableMap<String, MutableList<String>>,
            case: Case,
            alreadyClassified: MutableSet<String>
    ) {
        val caseCols = case.classifiedColumns

        caseCols[Columns.STATIC_NUM_COLS.value]?.forEach {
            categorizeColumn(it, Columns.STATIC_NUM_COLS.value, resultCols, alreadyClassified, emptyList())
        }

        caseCols[Columns.STATIC_CAT_COLS.value]?.forEach {
            categorizeColumn(it, Columns.STATIC_CAT_COLS.value, resultCols, alreadyClassified, listOf(Columns.STATIC_NUM_COLS.value))
        }

        caseCols[Columns.DYNAMIC_NUM_COLS.value]?.forEach {
            categorizeColumn(it, Columns.DYNAMIC_NUM_COLS.value, resultCols, alreadyClassified, listOf(Columns.STATIC_NUM_COLS.value))
        }

        caseCols[Columns.DYNAMIC_CAT_COLS.value]?.forEach {
            categorizeColumn(
                    it, Columns.DYNAMIC_CAT_COLS.value, resultCols, alreadyClassified,
                    listOf(Columns.STATIC_NUM_COLS.value, Columns.STATIC_CAT_COLS.value, Columns.DYNAMIC_NUM_COLS.value)
            )
        }
    }

    private fun categorizeColumn(
            column: String,
            key: String,
            resultCols: MutableMap<String, MutableList<String>>,
            alreadyClassified: MutableSet<String>,
            lookThrough: List<String>
    ) {
        if (column !in alreadyClassified) {
            alreadyClassified.add(column)
            resultCols[key]!!.add(column)
        } else {
            lookThrough.forEach {
                if (resultCols[it]!!.contains(column)) {
                    resultCols[it]!!.remove(column)
                    resultCols[key]!!.add(column)
                }
            }
        }
    }

    private fun insertIntoMap(
            map: MutableMap<String, MutableSet<String>>,
            category: String,
            col: String,
            values: Set<String>?
    ) {
        var isNumeric = true

        values!!.forEach {
            try {
                it.toDouble()
            } catch (e: NumberFormatException) {
                isNumeric = false
                return@forEach
            }
        }

        val threshold = max(confThreshold.toDouble(), 0.001 * sampleSize)
        if (values.size < threshold || !isNumeric) {
            if (category + ColumnPart.CAT_COLS.value in map.keys) map[category + ColumnPart.CAT_COLS.value]!!.add(col)
            else map[category + ColumnPart.CAT_COLS.value] = mutableSetOf(col)
        } else {
            if (category + ColumnPart.NUM_COLS.value in map.keys) map[category + ColumnPart.NUM_COLS.value]!!.add(col)
            else map[category + ColumnPart.NUM_COLS.value] = mutableSetOf(col)
        }
    }

    private fun classifyColumns(case: Case) {
        case.attributes.forEach { k, v ->
            emptyValues.forEach {
                if (v.contains(it)) v.remove(it)
            }

            if (v.size == 1) case.staticCols.add(k)
            else case.dynamicCols.add(k)
        }
    }

    private fun identifyTimestampColumn(attributes: LinkedHashMap<String, MutableSet<String>>): String? {
        return attributes
                .asSequence()
                .filter { isDateCol(it.value.first()) }
                .first()
                .key
    }

    private fun isDateCol(col: String): Boolean {
        dateFormats.forEach {
            if (col.matches(it)) return true
        }
        return false
    }

    private fun parseCsv(caseIdColumn: String): List<Case> {
        log.debug("Started parsing csv")
        val start = System.currentTimeMillis()

        val cases = mutableSetOf<Case>()
        var caseIdColIndex: Int

        var header: List<String>
        BufferedReader(FileReader(f)).use {
            val line = it.readLine()
            if (line.isBlank()) throw NirdizatiRuntimeException("File is empty")
            else {
                header = line.split(splitter)
                caseIdColIndex = header.indexOf(caseIdColumn)
            }

            it.lineSequence()
                    .takeWhile { cases.size < sampleSize }
                    .forEach { row -> processRow(row, cases, caseIdColIndex, header) }
        }

        val end = System.currentTimeMillis()
        log.debug("Finished parsing csv in ${end - start} ms")
        return cases.toList()
    }

    private fun processRow(row: String, cases: MutableSet<Case>, caseIndex: Int, head: List<String>) {
        val cols = escapeCSV(row).split(splitter)

        val case = findCaseById(cols[caseIndex], cases) ?: prepareCase(head, cols[caseIndex])
        val keys = case.attributes.keys.toList()

        for ((i, token) in cols.withIndex()) {
            case.attributes[keys[i]]?.add(token)
        }
        cases.add(case)
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

    private fun prepareCase(head: List<String>, id: String): Case {
        val c = Case(id)
        head.forEach { c.attributes[it] = mutableSetOf() }
        return c
    }

    private fun findCaseById(colName: String, cases: Set<Case>): Case? {
        return cases.firstOrNull { colName.toLowerCase() == it.id.toLowerCase() }
    }

    fun getColumnList(): List<Value> =
            ConfigurationReader.findNode("csv/options").itemList()

    private fun readOneCase(): Case {
        val reader = BufferedReader(FileReader(f))
        val heads = reader.readLine().split(splitter)
        val items = reader.readLine().split(splitter)

        val case = Case("")
        heads.zip(items).forEach { case.attributes[it.first] = mutableSetOf(it.second) }

        return case
    }

    fun getTimeStamp(): String = identifyTimestampColumn(readOneCase().attributes) ?: ""
}