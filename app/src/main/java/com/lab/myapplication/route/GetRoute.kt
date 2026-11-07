package com.lab.myapplication.route


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class RouteResult(
    val coordinates: List<Pair<Double, Double>>,
    val distanceMeters: Double,
    val durationSeconds: Double
)

object GetRoute {

    suspend fun getRoute(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): RouteResult = withContext(Dispatchers.IO) {

        val urlString =
            "https://router.project-osrm.org/route/v1/driving/" +
                    "$startLongitude,$startLatitude;" +
                    "$endLongitude,$endLatitude" +
                    "?overview=full&geometries=geojson&steps=true"

        val connection =
            URL(urlString).openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            connection.setRequestProperty(
                "User-Agent",
                "MyNavigatorAndroid/1.0"
            )
            connection.setRequestProperty(
                "Accept",
                "application/json"
            )
            val responseCode = connection.responseCode

            if (responseCode !in 200..299) {
                throw IOException("Ошибка сервера: HTTP $responseCode")
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(response)

            if (json.optString("code") != "Ok") {
                throw IOException(
                    "Не удалось построить маршрут: ${json.optString("code")}"
                )
            }

            val routes = json.getJSONArray("routes")

            if (routes.length() == 0) {
                throw IOException("Сервер не вернул маршрут")
            }

            val route = routes.getJSONObject(0)

            val geometry = route
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            val coordinates = buildList {
                for (i in 0 until geometry.length()) {
                    val point = geometry.getJSONArray(i)


                    val longitude = point.getDouble(0)
                    val latitude = point.getDouble(1)


                    add(latitude to longitude)
                }
            }

            if (coordinates.size < 2) {
                throw IOException("Маршрут содержит недостаточно точек")
            }

            RouteResult(
                coordinates = coordinates,
                distanceMeters = route.getDouble("distance"),
                durationSeconds = route.getDouble("duration")
            )

        } finally {
            connection.disconnect()
        }
    }
}