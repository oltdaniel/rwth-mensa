package at.oltdaniel.rwthmensa

import java.util.*
import kotlin.collections.ArrayList

class DayMenu(
    val date: Date,
    val mainExtra: DishExtra,
    val secondExtra: DishExtra,
    val dishes: ArrayList<Dish>
)