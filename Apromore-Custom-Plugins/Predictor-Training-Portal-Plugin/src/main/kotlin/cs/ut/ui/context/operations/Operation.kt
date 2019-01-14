package cs.ut.ui.context.operations

abstract class Operation<T>(val context: T) {

    abstract fun perform()

    open fun isEnabled() = true
}