package com.lab.myapplication.route

import android.content.Context
import android.widget.Toast
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import kotlinx.coroutines.launch
import java.util.Locale

object RouteController {

    fun buildRoute(
        context: Context,
        coroutineScope: kotlinx.coroutines.CoroutineScope,
        start: LatLng?,
        destinationLatitude: Double?,
        destinationLongitude: Double?,
        map: MapLibreMap?,
        onLoadingChanged: (Boolean) -> Unit,
        onRouteBuild : () -> Unit
    ) {
        when {
            start == null -> {
                Toast.makeText(
                    context,
                    "Ожидаем определение местоположения",
                    Toast.LENGTH_SHORT
                ).show()
            }

            destinationLatitude == null || destinationLongitude == null -> {
                Toast.makeText(
                    context,
                    "Сначала выберите место назначения",
                    Toast.LENGTH_SHORT
                ).show()
            }

            map == null -> {
                Toast.makeText(
                    context,
                    "Карта ещё не готова",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                coroutineScope.launch {
                    onLoadingChanged(true)

                    try {

                        val route = GetRoute.getRoute(
                            startLatitude = start.latitude,
                            startLongitude = start.longitude,
                            endLatitude = destinationLatitude,
                            endLongitude = destinationLongitude
                        )


                        RouteService.drawRoute(
                            map = map,
                            coordinates = route.coordinates
                        )
                        onRouteBuild()

                        val distanceKm = route.distanceMeters / 1000.0
                        val durationMinutes =
                            (route.durationSeconds / 60.0).toInt()

                        Toast.makeText(
                            context,
                            "Расстояние: ${
                                String.format(
                                    Locale.getDefault(),
                                    "%.1f",
                                    distanceKm
                                )
                            } км, время: $durationMinutes мин.",
                            Toast.LENGTH_LONG
                        ).show()

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            e.message ?: "Не удалось построить маршрут",
                            Toast.LENGTH_LONG
                        ).show()
                    } finally {
                        onLoadingChanged(false)
                    }
                }
            }
        }
    }
}