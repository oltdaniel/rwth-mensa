package at.oltdaniel.rwthmensa

import org.jsoup.nodes.Element
import java.nio.charset.Charset
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class DayMenuParserRunnable(
    private val element: Element,
    private val days : ArrayList<DayMenu>
) : Runnable{
    override fun run() {
        // Clean up element
        element.select(".nutr-info, .menue-nutr, sup").forEach { e -> e.text("") }
        // Get date
        val dateNodes = element.getElementsByTag("h3")
        if(dateNodes.size != 1) {
            return
        }
        val dateNode = dateNodes[0].text()
        val date = stringToDate(dateNode) ?: return
        // Initialize new dishes of day list
        val dishes = ArrayList<Dish>()
        // Find all dishes
        val dishNodes = element.select(".menues tr")
        // Parse each dish
        dishNodes.forEach {
            // Find all possible nodes
            val labelNodes = it.select(".menue-category")
            val priceNodes = it.select(".menue-price")
            val foodNodes = it.select(".menue-desc")
            // Check if exactly 1 element exists, else break
            if(labelNodes.size != 1 || priceNodes.size != 1 || foodNodes.size != 1) {
                return
            }
            // Get first nodes
            val labelNode = toUtf8(labelNodes[0].text())
            val priceNode = toUtf8(priceNodes[0].text())
            val foodNode = toUtf8(foodNodes[0].text())
            // Parse values
            val label = labelNode
            val price = stringPriceToDouble(priceNode)
            val food = foodNode
            // Add dish
            dishes.add(Dish(label, price, food))
        }
        // Find all extras
        val extraNodes = element.select(".extras tr")
        if(extraNodes.size != 2) {
            return
        }
        // Parse extras
        val mainExtra = DishExtra("Hauptbeilage", elementToExtra(extraNodes[0]))
        val secondExtra = DishExtra("Nebenbeilage", elementToExtra(extraNodes[1]))
        // Add day menu
        days.add(DayMenu(date, mainExtra, secondExtra, dishes))
    }

    private fun toUtf8(a: String): String {
        return String(
            a.toByteArray(
                charset("ISO-8859-1")
            ),
            Charset.forName("utf-8")
        )
    }

    private fun stringToDate(text: String) : Date? {
        return DateFormat.getDateInstance().parse(text.substring(text.length-10,text.length))
    }

    private fun stringPriceToDouble(text : String) : Double {
        return text.substring(0,4).replace(',','.').toDouble()
    }

    private fun elementToExtra(el : Element) : String {
        el.select(".menue-category").forEach { e -> e.text("") }
        el.select(".seperator").forEach { e ->
            val org = e.text()
            e.text(" $org ")
        }
        return toUtf8(el.text())
    }
}