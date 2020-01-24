package at.oltdaniel.rwthmensa

import android.content.Context
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlin.collections.ArrayList

class Crawler(private val context: Context) {
    private val BASE_URL = "https://www.studierendenwerk-aachen.de/speiseplaene/academica-w.html"
    private var queue = Volley.newRequestQueue(context)
    private val cache = Cache(context, BASE_URL)
    private val parser = Parser()

    /**
     * Return total menu
     */
    fun getTotalMenu() : TotalMenu? {
        // Get content to parse
        val data = request() ?: return null
        // Call parser
        val days = parser.parseDays(data) ?: return null
        // Sort days
        days.sortBy { b -> b.date }
        // Return total menu
        return TotalMenu(days.first().date, days.last().date, days)
    }

    /**
     * Request the URL and return the content
     */
    private fun request() : String? {
        val cacheValue = cache.getCache()
        if(cacheValue != null) {
            return cacheValue
        }
        val requestFuture : RequestFuture<String> = RequestFuture.newFuture()
        val request = StringRequest(BASE_URL, requestFuture, requestFuture)
        queue.add(request)
        val resp = requestFuture.get()
        cache.setCache(resp)
        if(resp.isEmpty()) {
            return null
        }
        return resp
    }
}