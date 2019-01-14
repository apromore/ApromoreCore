package cs.ut.jobs

import cs.ut.exceptions.NirdizatiRuntimeException
import cs.ut.json.JSONHandler
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import cs.ut.util.Columns
import cs.ut.util.IdentColumns
import java.io.File

/**
 * Generates data set for a job
 *
 * @param parameters to generate the JSON file from
 * @param currentFile file name to include in JSON
 */
class DataSetGenerationJob(
        val parameters: MutableMap<String, MutableList<String>>,
        currentFile: File
) : Job() {

    private val fileName = currentFile.nameWithoutExtension
    private val finalParameters: MutableMap<String, Any> = mutableMapOf()

    private val cols by lazy { Columns.values().map { it.value } }

    @Suppress("UNCHECKED_CAST")
    override fun preProcess() {
        // Resource column should always be dynamic categorical
        parameters[Columns.DYNAMIC_CAT_COLS.value]?.apply {
            val resource = parameters.remove(IdentColumns.RESOURCE.value)!![0]
            if (resource.isNotEmpty()) {
                this.add(resource)
            }
        }

        parameters.forEach {
            if (it.value.size == 1 && !isColumn(it.key)) {
                finalParameters[it.key] = it.value[0]
            } else {
                finalParameters[it.key] = it.value
            }
        }
    }

    private fun isColumn(key: String): Boolean = key in cols

    override fun execute() {
        JSONHandler().writeToFile(finalParameters, fileName, Dir.DATA_DIR)
    }

    override fun postExecute() {
        val result = File(DirectoryConfiguration.dirPath(Dir.DATA_DIR) + fileName + ".json")
        if (!result.exists()) {
            throw NirdizatiRuntimeException("Could not write file to disk <${result.absolutePath}>")
        }
    }
}