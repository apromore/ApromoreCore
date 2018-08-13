package cs.ut.engine.item

/**
 * Data class that used to hold information when parsing the log in CsvReader
 */
data class Case(val id: String) {

    val attributes = LinkedHashMap<String, MutableSet<String>>()
    val staticCols = mutableSetOf<String>()
    val dynamicCols = mutableSetOf<String>()

    val classifiedColumns = mutableMapOf<String, MutableSet<String>>()
}