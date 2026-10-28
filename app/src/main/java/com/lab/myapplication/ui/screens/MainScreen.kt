package com.lab.myapplication.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

import com.lab.myapplication.LocationPermission
import com.lab.myapplication.R
import com.lab.myapplication.route.RouteController
import com.lab.myapplication.route.RouteService
import com.lab.myapplication.search.SearchRes
import com.lab.myapplication.ui.map.MapComponents
import com.lab.myapplication.ui.map.MapVis
import com.lab.myapplication.search.SearchPanel
import com.lab.myapplication.ui.PlaceDetailsPanel

import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    favoritePlaces: List<SearchRes>,
    onFavoriteToggle: (SearchRes) -> Unit,
    placeToOpenOnMap: SearchRes?,
    onPlaceHandled: () -> Unit,
    isNavigating: Boolean,
    onNavigatingChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var hasLocationPermission by remember {
        mutableStateOf(false)
    }

    var selectedPlace by remember {
        mutableStateOf<SearchRes?>(null)
    }

    var mapComponents by remember {
        mutableStateOf<MapComponents?>(null)
    }

    var cameraInitialized by remember {
        mutableStateOf(false)
    }

    var currentLocation by remember {
        mutableStateOf<LatLng?>(null)
    }

    var isBuildingRoute by remember {
        mutableStateOf(false)
    }

    LocationPermission { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(placeToOpenOnMap, mapComponents) {
        val place = placeToOpenOnMap ?: return@LaunchedEffect
        val map = mapComponents?.map ?: return@LaunchedEffect

        selectedPlace = place
        cameraInitialized = true

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(place.latitude, place.longitude),
                16.0
            ),
            1000
        )

        onPlaceHandled()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MapVis(
            modifier = Modifier.fillMaxSize(),
            onMapReady = { components ->
                mapComponents = components
                RouteService.restoreRoute(components.map)
            }
        )

        if (isNavigating) {
            Button(
                onClick = {
                    mapComponents?.map?.let { map ->
                        RouteService.clearRoute(map)
                        currentLocation?.let { position ->
                            map.animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder()
                                        .target(position)
                                        .zoom(15.0)
                                        .tilt(0.0)
                                        .bearing(0.0)
                                        .build()
                                ),
                                700
                            )
                        }
                    }

                    onNavigatingChange(false)
                    isBuildingRoute = false
                    selectedPlace = null
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            ) {
                Text(stringResource(R.string.finish_route))
            }
        }

        if (!isNavigating) {
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
            )
        }

        if (!isNavigating) {
            selectedPlace?.let { place ->
                PlaceDetailsPanel(
                    place = place,
                    isFavorite = favoritePlaces.contains(place),

                    onFavoriteClick = {
                        val alreadyFavorite = favoritePlaces.any {
                            it.latitude == place.latitude &&
                                    it.longitude == place.longitude
                        }

                        onFavoriteToggle(place)

                    },

                    onDrawRoute = {
                        RouteController.buildRoute(
                            context = context,
                            coroutineScope = coroutineScope,
                            start = currentLocation,
                            destinationLatitude = place.latitude,
                            destinationLongitude = place.longitude,
                            map = mapComponents?.map,

                            onLoadingChanged = { loading ->
                                isBuildingRoute = loading
                            },

                            onRouteBuild = {
                                onNavigatingChange(true)

                                val position = currentLocation
                                val map = mapComponents?.map

                                if (position != null && map != null) {
                                    val cameraPosition =
                                        CameraPosition.Builder()
                                            .target(position)
                                            .zoom(17.5)
                                            .tilt(60.0)
                                            .bearing(
                                                map.cameraPosition.bearing
                                            )
                                            .build()

                                    map.animateCamera(
                                        CameraUpdateFactory
                                            .newCameraPosition(cameraPosition),
                                        700
                                    )
                                }
                            }
                        )
                    },

                    onClose = {
                        selectedPlace = null
                    },

                    modifier = Modifier.align(
                        Alignment.BottomCenter
                    )
                )
            }
        }
    }

    DisposableEffect(
        hasLocationPermission,
        mapComponents,
        isNavigating
    ) {
        val marker = mapComponents?.marker
        val map = mapComponents?.map

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
                override fun onLocationResult(
                    result: LocationResult
                ) {
                    val location = result.lastLocation ?: return

                    val position = LatLng(
                        location.latitude,
                        location.longitude
                    )

                    currentLocation = position

                    marker?.update(
                        location.latitude,
                        location.longitude
                    )

                    if (isNavigating) {
                        RouteService.updateRemainingRoute(
                            map = map,
                            currentLatitude = location.latitude,
                            currentLongitude = location.longitude
                        )
                    }

                    if (isNavigating) {
                        val bearing =
                            if (
                                location.hasBearing() &&
                                location.speed > 1.0f
                            ) {
                                location.bearing.toDouble()
                            } else {
                                map.cameraPosition.bearing
                            }

                        val cameraPosition =
                            CameraPosition.Builder()
                                .target(position)
                                .zoom(17.5)
                                .tilt(60.0)
                                .bearing(bearing)
                                .build()

                        map.animateCamera(
                            CameraUpdateFactory
                                .newCameraPosition(cameraPosition),
                            500
                        )
                    } else if (!cameraInitialized) {
                        cameraInitialized = true

                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                position,
                                15.0
                            ),
                            1000
                        )
                    }
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
                fusedLocationClient.removeLocationUpdates(
                    locationCallback
                )
            }
        }
    }
}
