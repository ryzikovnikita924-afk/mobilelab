package com.lab.myapplication.ui.screens

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest


import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lab.myapplication.LocationPermission
import com.lab.myapplication.ui.map.MapVis
import com.lab.myapplication.ui.map.MapComponents
import com.lab.myapplication.ui.map.MapVis
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.Symbol
import org.maplibre.android.plugins.annotation.SymbolOptions

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(false)
    }

    LocationPermission { granted ->
        hasLocationPermission = granted
    }
    var mapComponents by remember {
        mutableStateOf<MapComponents?>(null)
    }
    MapVis (
        modifier = modifier.fillMaxSize(),
        onMapReady = { components ->
            mapComponents  = components
        }
    )



    DisposableEffect(hasLocationPermission, mapComponents) {
        val marker = mapComponents?.marker
        val map = mapComponents?.map
        val mapview = mapComponents?.mapView
        if (!hasLocationPermission || map == null) {
            onDispose { }
        } else {
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                3000L
            )
                .setMinUpdateIntervalMillis(1500L)
                .build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    val position =  LatLng(
                        location.latitude,
                        location.longitude
                    )
                    marker?.update(location.latitude, location.longitude)


                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(position, 15.0),
                        1000
                    )
                }
            }
            val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (fineGranted || coarseGranted) {
                @SuppressLint("MissingPermission")
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            onDispose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                marker?.remove()
            }
        }
    }
}
