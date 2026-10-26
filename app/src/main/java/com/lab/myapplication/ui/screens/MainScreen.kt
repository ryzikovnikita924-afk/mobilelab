package com.lab.myapplication.ui.screens

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment


import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.lab.myapplication.LocationPermission
import com.lab.myapplication.route.RouteController
import com.lab.myapplication.route.RouteService.drawRoute
import com.lab.myapplication.search.SearchRes
import com.lab.myapplication.ui.map.SearchPanel
import com.lab.myapplication.ui.map.MapVis
import com.lab.myapplication.ui.map.MapComponents
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng


@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(false)
    }

    var selectedPlace by remember {
        mutableStateOf<SearchRes?>(null)
    }
    val favoritePlaces = remember {
        mutableStateListOf<SearchRes>()
    }
    var mapComponents by remember {
        mutableStateOf<MapComponents?>(null)
    }
    var cameraInitialized by remember(mapComponents) {
        mutableStateOf(false)
    }
    var currentLocation by remember {
        mutableStateOf<LatLng?>(null)
    }

    var isBuildingRoute by remember {
        mutableStateOf(false)
    }
    var isNavigating by remember {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()



    LocationPermission { granted ->
        hasLocationPermission = granted
    }

    Box(modifier = modifier.fillMaxSize()){

        MapVis (
            modifier = modifier.fillMaxSize(),
            onMapReady = { components ->
                mapComponents  = components
            }
        )
        if (!isNavigating){
        SearchPanel(
            modifier = Modifier.align(Alignment.TopCenter),
            onResultSelected = { result ->
                selectedPlace = result

                cameraInitialized = true


                mapComponents?.map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            result.latitude,
                            result.longitude
                        ),
                        16.0
                    ),
                    1000
                )
            }
        )}
        if (!isNavigating) {
            selectedPlace?.let { place ->
                PlaceDetailsPanel(
                    place = place,
                    isFavorite = favoritePlaces.contains(place),

                    onFavoriteClick = {
                        if (favoritePlaces.contains(place)) {
                            favoritePlaces.remove(place)

                            Toast.makeText(
                                context,
                                "Удалено из избранного",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            favoritePlaces.add(place)

                            Toast.makeText(
                                context,
                                "Добавлено в избранное",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onDrawRoute = {
                        val destination = selectedPlace

                        RouteController.buildRoute(
                            context = context,
                            coroutineScope = coroutineScope,
                            start = currentLocation,
                            destinationLatitude = destination?.latitude,
                            destinationLongitude = destination?.longitude,
                            map = mapComponents?.map,
                            onLoadingChanged = { isBuildingRoute = it },
                            onRouteBuild = {
                                isNavigating = true

                                val position = currentLocation
                                val map = mapComponents?.map

                                if (position != null && map != null) {
                                    val cameraPosition = CameraPosition.Builder()
                                        .target(position)
                                        .zoom(17.5)
                                        .tilt(60.0)
                                        .bearing(map.cameraPosition.bearing)
                                        .build()

                                    map.animateCamera(
                                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                                        700
                                    )
                                }
                            }
                        )
                    },


                    onClose = {
                        selectedPlace = null
                    },

                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }





    DisposableEffect(hasLocationPermission, mapComponents, isNavigating) {
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

                    currentLocation = position

                    marker?.update(location.latitude, location.longitude)
                    if (isNavigating) {
                        val bearing = if (
                            location.hasBearing() && location.speed > 1.0f
                        ) {
                            location.bearing.toDouble()
                        } else {
                            map.cameraPosition.bearing
                        }

                        val cameraPosition = CameraPosition.Builder()
                            .target(position)
                            .zoom(17.5)
                            .tilt(60.0)
                            .bearing(bearing)
                            .build()

                        map.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            500
                        )}
                    else if(!cameraInitialized){
                        cameraInitialized = true
                    }
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
