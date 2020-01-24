package at.oltdaniel.rwthmensa

import android.content.Context
import java.io.File
import java.security.MessageDigest
import java.util.*

class Cache(
    private val context: Context,
    private val url: String
) {
    // Shared preferences tags
    private val TAG = "mensa-plan"
    private val TAG_DATE = "mensa-date"
    // Cache file path
    private val TODAY = today()
    private val HASH = hash(TODAY + url)
    private val FILE = context.filesDir.absolutePath + "/" + HASH + ".html"
    // Open shared preferences
    private val sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)

    /**
     * Return the cached response if exists
     */
    fun getCache() : String? {
        if(sharedPreferences.getString(TAG_DATE, "") == TODAY) {
            return File(FILE).readText()
        }
        return null
    }

    fun setCache(content: String) {
        // Ensure file exists
        val file = File(FILE)
        file.createNewFile()
        // Write new content
        file.writeText(content)
        // Update shared preferences
        val sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(TAG_DATE, TODAY)
        sharedPreferencesEditor.apply()
    }

    /**
     * Return today as a String
     */
    private fun today() : String {
        return Date().toDaysOnly()
    }

    private fun hash(text : String) : String {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(text.toByteArray())
        return digest.digest().toString()
    }
}