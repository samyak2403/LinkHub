package com.samyak.linkhub.utils

import android.content.Context
import android.net.Uri
import com.samyak.linkhub.data.Link
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

object LinkUtils {
    
    // Get favicon URL from website
    fun getFaviconUrl(websiteUrl: String): String {
        return try {
            val url = URL(websiteUrl)
            val domain = "${url.protocol}://${url.host}"
            "$domain/favicon.ico"
        } catch (e: Exception) {
            ""
        }
    }
    
    // Fetch page title from URL
    suspend fun fetchPageTitle(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            BufferedReader(InputStreamReader(connection.getInputStream())).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val titleStart = line?.indexOf("<title>", ignoreCase = true) ?: -1
                    if (titleStart != -1) {
                        val titleEnd = line?.indexOf("</title>", titleStart, ignoreCase = true) ?: -1
                        if (titleEnd != -1) {
                            return@withContext line?.substring(titleStart + 7, titleEnd)?.trim()
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }
    
    // Export links to JSON
    fun exportLinksToJson(links: List<Link>): String {
        val jsonArray = JSONArray()
        links.forEach { link ->
            val jsonObject = JSONObject().apply {
                put("id", link.id)
                put("title", link.title)
                put("url", link.url)
                put("category", link.category)
                put("isFavorite", link.isFavorite)
                put("clickCount", link.clickCount)
                put("createdAt", link.createdAt)
                put("lastOpened", link.lastOpened)
                put("notes", link.notes)
                put("faviconUrl", link.faviconUrl)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString(2)
    }
    
    // Import links from JSON
    fun importLinksFromJson(jsonString: String): List<Link> {
        val links = mutableListOf<Link>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val link = Link(
                    id = 0, // Let Room auto-generate new IDs
                    title = jsonObject.getString("title"),
                    url = jsonObject.getString("url"),
                    category = jsonObject.optString("category", "General"),
                    isFavorite = jsonObject.optBoolean("isFavorite", false),
                    clickCount = jsonObject.optInt("clickCount", 0),
                    createdAt = jsonObject.optLong("createdAt", System.currentTimeMillis()),
                    lastOpened = jsonObject.optLong("lastOpened", 0),
                    notes = jsonObject.optString("notes", ""),
                    faviconUrl = jsonObject.optString("faviconUrl", "")
                )
                links.add(link)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return links
    }
    
    // Save JSON to file
    suspend fun saveJsonToFile(context: Context, uri: Uri, jsonString: String): Boolean = 
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray())
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    
    // Read JSON from file
    suspend fun readJsonFromFile(context: Context, uri: Uri): String? = 
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().use { it.readText() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
