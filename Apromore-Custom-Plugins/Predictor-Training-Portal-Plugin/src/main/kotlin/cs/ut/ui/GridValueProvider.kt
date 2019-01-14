package cs.ut.ui

/**
 * Interface that allows to create custom grid row generators
 */
@FunctionalInterface
interface GridValueProvider<in T, out Row> {
    /**
     * Get row representation of given data
     *
     * @param data to be represented as a row
     *
     * @return row with the represented data
     */
    fun provide(data: T): Pair<FieldComponent, Row>
}