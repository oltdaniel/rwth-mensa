package at.oltdaniel.rwthmensa

class Dish(
    val label: String,
    val price: Double,
    val food: String
) {
    fun getPrice() : String {
        return "${price.format(2)}€"
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)