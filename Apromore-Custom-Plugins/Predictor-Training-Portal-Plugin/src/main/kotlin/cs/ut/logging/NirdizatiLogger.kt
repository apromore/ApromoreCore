package cs.ut.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Logger wrapper that logs data with specific tag so it is easier to track different UI actions
 */
class NirdizatiLogger(val name: String, val id: String) {

    private val logger = LoggerFactory.getLogger(name)

    fun debug(message: Any?, t: Throwable? = null) {
        logger.debug("[$id] $message", t)
    }

    fun error(message: Any?, t: Throwable? = null) {
        logger.error("[$id] $message", t)
    }

    fun info(message: Any?, t: Throwable? = null) {
        logger.info("[$id] $message", t)
    }

    companion object {
        fun getLogger(clazz: KClass<*>, id: String = "GLOBAL") = NirdizatiLogger(clazz.java.name, id)
    }
}
