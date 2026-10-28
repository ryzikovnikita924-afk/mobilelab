package com.lab.myapplication.ui.map


import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

class MapComponents(
    val mapView: MapView,
    val map: MapLibreMap,
    val marker: LocationMarker
)

