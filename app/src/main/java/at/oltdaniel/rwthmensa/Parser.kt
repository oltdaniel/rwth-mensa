package at.oltdaniel.rwthmensa

import org.jsoup.Jsoup
import kotlin.collections.ArrayList

class Parser {
    fun parseDays(data: String) : ArrayList<DayMenu>? {
        // Store all running threads
        val threads = ArrayList<Thread>()
        // Get all days to parser
        val soup = Jsoup.parse(data).select(".accordion > div")
        if(soup.size == 0) {
            return null
        }
        // Start parse Routine for each day
        val days = ArrayList<DayMenu>()
        soup.forEach {
            val thread = Thread(DayMenuParserRunnable(it, days))
            thread.start()
            threads.add(thread)
        }
        // Join all threads
        threads.forEach {
            it.join()
        }
        return days
    }
}