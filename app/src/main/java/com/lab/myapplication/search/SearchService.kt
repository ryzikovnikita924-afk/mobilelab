package com.lab.myapplication.search


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object SearchService {

    suspend fun search(query: String): List<SearchRes> =
        withContext(Dispatchers.IO) {

            val encodedQuery = URLEncoder.encode(query, "UTF-8")

            val url = URL(
                "https://photon.komoot.io/api/" +
                        "?q=$encodedQuery" +
                        "&limit=5" +
                        "&lang=en"
            )

            val connection =
                url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                connection.setRequestProperty(
                    "User",
                    "Nav/1.0"
                )
                connection.setRequestProperty(
                    "Accept",
                    "application/json"
                )

                val responseCode = connection.responseCode

                val responseStream =
                    if (responseCode in 200..299) {
                        connection.inputStream
                    } else {
                        connection.errorStream
                    }

                val response = responseStream
                    ?.bufferedReader(Charsets.UTF_8)
                    ?.use { it.readText() }
                    ?: ""

                if (responseCode !in 200..299) {
                    Log.e(
                        "SearchAPI",
                        "HTTP-код: $responseCode, ответ: $response"
                    )
                    throw Exception("Ошибка HTTP: $responseCode")
                }

                val json = JSONObject(response)
                val features = json.getJSONArray("features")

                buildList {
                    for (i in 0 until features.length()) {
                        val feature = features.getJSONObject(i)
                        val properties =
                            feature.getJSONObject("properties")
                        val geometry =
                            feature.getJSONObject("geometry")
                        val coordinates =
                            geometry.getJSONArray("coordinates")

                        val name =
                            properties.optString("name", "")
                        val street =
                            properties.optString("street", "")
                        val city =
                            properties.optString("city", "")
                        val country =
                            properties.optString("country", "")

                        val displayName = listOf(
                            name,
                            street,
                            city,
                            country
                        )
                            .filter { it.isNotBlank() }
                            .distinct()
                            .joinToString(", ")

                        if (displayName.isNotBlank()) {
                            add(
                                SearchRes(
                                    name = displayName,
                                    longitude = coordinates.getDouble(0),
                                    latitude = coordinates.getDouble(1)
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SearchAPI", "Ошибка поиска", e)
                throw e
            } finally {
                connection.disconnect()
            }
        }
}

