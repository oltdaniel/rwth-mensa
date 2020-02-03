package at.oltdaniel.rwthmensa

class Dish(
    val label: String,
    val price: Double,
    val food: String,
    val types: ArrayList<DISH_TYPES>
) {
    fun getPrice() : String {
        return "${price.format(2)}€"
    }

    fun getTypes() : String {
        return types.map { e ->
            when(e) {
                DISH_TYPES.GEFLUEGEL -> "Geflügel"
                DISH_TYPES.RIND -> "Rind"
                DISH_TYPES.SCHWEIN -> "Schwein"
                DISH_TYPES.OVL -> "OVL"
                DISH_TYPES.VEGAN -> "Vegan"
                DISH_TYPES.FISCH -> "Fisch"
            }
        }.joinToString(", ")
    }
}

enum class DISH_TYPES {
    GEFLUEGEL, RIND, SCHWEIN, OVL, VEGAN, FISCH
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)