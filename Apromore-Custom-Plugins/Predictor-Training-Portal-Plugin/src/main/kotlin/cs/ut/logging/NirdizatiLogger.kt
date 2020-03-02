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
