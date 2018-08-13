package cs.ut.exceptions

/**
 * Business runtime exception which means that we do not allow such behaviour
 */
class NirdizatiRuntimeException(
    message: String,
    cause: Throwable? = null,
    enableSupression: Boolean = true,
    writeableStackTrace: Boolean = true
) : RuntimeException(message, cause, enableSupression, writeableStackTrace)