package cs.ut.charts

/**
 * Interface that allows a component to be rendered client side.
 * Implementing components should be able to render themselves client side.
 */
@FunctionalInterface
interface Renderable {

    /**
     * Method that will be called in order to render the component.
     */
    fun render()
}