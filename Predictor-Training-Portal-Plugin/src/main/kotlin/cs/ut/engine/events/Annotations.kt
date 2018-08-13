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
