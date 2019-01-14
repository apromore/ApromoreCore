package cs.ut.json

import com.fasterxml.jackson.databind.ObjectMapper
import cs.ut.exceptions.Either
import cs.ut.exceptions.Left
import cs.ut.exceptions.Right
import cs.ut.logging.NirdizatiLogger
import cs.ut.providers.Dir
import cs.ut.providers.DirectoryConfiguration
import java.io.File
import java.io.FileWriter


class JSONHandler {
    val mapper = ObjectMapper()

    inline fun <reified T> transformToObject(json: String): T {
        log.debug("Transforming json to object of ${T::class.java}. JSON: $json")
        return mapper.readValue(json, T::class.java).apply {
            log.debug(this)
        }
    }

    inline fun <reified T> fromFile(fileName: String, directory: Dir): Either<Exception, T> {
        log.debug("Converting $fileName to ${T::class.java}")
        return try {
            Right(mapper.readValue(File(DirectoryConfiguration.dirPath(directory) + "$fileName.json"), T::class.java).apply {
                log.debug(this)
            })
        } catch (e: Exception) {
            Left(e)
        }
    }

    fun toMap(any: Any): Map<*, *> {
        log.debug("Transforming bean $any to map")
        return mapper.convertValue(any, Map::class.java).apply {
            log.debug(this)
        }
    }

    fun fromString(json: String): Map<*, *> {
        log.debug("Transforming $json to Map")
        return mapper.readValue(json, Map::class.java).apply {
            log.debug(this)
        }
    }

    fun writeToFile(any: Any, fileName: String, directory: Dir): File {
        val f = File(DirectoryConfiguration.dirPath(directory) + "$fileName.json")
        log.debug("Writing $any to file $fileName to $directory")
        mapper.writeValue(FileWriter(f), any)

        log.debug("Finished writing to ${f.absolutePath}")
        return f
    }

    fun convert2String(any: Any): String = mapper.writeValueAsString(any)

    companion object {
        val log = NirdizatiLogger.getLogger(JSONHandler::class)
    }
}