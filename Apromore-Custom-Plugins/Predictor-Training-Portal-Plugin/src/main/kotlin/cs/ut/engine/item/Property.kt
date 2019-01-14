package cs.ut.engine.item

/**
 * Class that represent hyper parameter properties for model parameter
 *
 * @see ModelParameter
 */
data class Property(
        var id: String,

        var type: String,

        var property: String,

        var maxValue: Double,

        var minValue: Double
) {
    constructor() : this("", "", "", -1.0, -1.0)


    override fun equals(other: Any?): Boolean {
        return other is Property && this.id == other.id && this.type == other.type && this.property == other.property &&
                this.maxValue == other.maxValue && this.minValue == other.minValue
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + property.hashCode()
        result = 31 * result + maxValue.hashCode()
        result = 31 * result + minValue.hashCode()
        return result
    }
}

