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

package cs.ut.engine.events

import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Annotation used by JobManager to call back on event that has happened
 */
@Retention(AnnotationRetention.RUNTIME)
annotation class Callback(val event: KClass<*>)

fun findCallback(target: Class<*>, eventType: KClass<*>): Method? {
    val methods: List<Method> = target.methods.filter { it.isAnnotationPresent(Callback::class.java) }
    methods.forEach { m ->
        val annotations = m.annotations
        annotations.forEach {
            if (it.annotationClass == Callback::class) {
                it as Callback
                if (it.event == eventType) return m
            }
        }
    }
    return null
}
