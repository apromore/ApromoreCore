package cs.ut.engine.item

/**
 * Data class that holds information regarding client browser.
 * Used mainly to scale header and job tracker on smalls screens.
 */
data class ClientInfo(
    val screenWidth: Int,
    val screenHeight: Int,
    val windowWidth: Int,
    val windowHeight: Int,
    val colorDepth: Int,
    val orientation: String
)