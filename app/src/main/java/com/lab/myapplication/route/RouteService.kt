
package com.lab.myapplication.route

import android.graphics.Color
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

object RouteService {

    private const val SOURCE_ID = "route-source"
    private const val LAYER_ID = "route-layer"
    private var savedCoordinates: List<Pair<Double, Double>> = emptyList()
    fun drawRoute(
        map: MapLibreMap,
        coordinates: List<Pair<Double, Double>>
    ) {
        val style = map.style ?: return

        if (coordinates.size < 2) return

        savedCoordinates = coordinates.toList()

        renderRoute(map, savedCoordinates)

        val points = coordinates.map { (latitude, longitude) ->
            Point.fromLngLat(longitude, latitude)
        }

        val lineString = LineString.fromLngLats(points)

        val featureCollection = FeatureCollection.fromFeatures(
            arrayOf(Feature.fromGeometry(lineString))
        )

        val source = style.getSourceAs<GeoJsonSource>(SOURCE_ID)

        if (source != null) {

            source.setGeoJson(featureCollection)
        } else {

            style.addSource(
                GeoJsonSource(SOURCE_ID, featureCollection)
            )

            style.addLayer(
                LineLayer(LAYER_ID, SOURCE_ID).withProperties(
                    lineColor(Color.rgb(0, 150, 136)),
                    lineWidth(5f)
                )
            )
        }
    }
    fun restoreRoute(map: MapLibreMap) {
        if (savedCoordinates.size >= 2) {
            renderRoute(map, savedCoordinates)
        }
    }

    private fun renderRoute(
        map: MapLibreMap,
        coordinates: List<Pair<Double, Double>>
    ) {
        val style = map.style ?: return

        val points = coordinates.map { (latitude, longitude) ->
            Point.fromLngLat(longitude, latitude)
        }

        val lineString = LineString.fromLngLats(points)

        val featureCollection = FeatureCollection.fromFeatures(
            arrayOf(Feature.fromGeometry(lineString))
        )

        val source = style.getSourceAs<GeoJsonSource>(SOURCE_ID)

        if (source != null) {
            source.setGeoJson(featureCollection)
        } else {
            style.addSource(
                GeoJsonSource(SOURCE_ID, featureCollection)
            )

            style.addLayer(
                LineLayer(LAYER_ID, SOURCE_ID).withProperties(
                    lineColor(Color.rgb(0, 150, 136)),
                    lineWidth(5f)
                )
            )
        }
    }


    fun clearRoute(map: MapLibreMap) {

        savedCoordinates = emptyList()

        val style = map.style ?: return

        if (style.getLayer(LAYER_ID) != null) {
            style.removeLayer(LAYER_ID)
        }

        if (style.getSource(SOURCE_ID) != null) {
            style.removeSource(SOURCE_ID)
        }
    }
}
