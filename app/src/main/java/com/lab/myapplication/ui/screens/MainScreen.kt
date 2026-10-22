package com.lab.myapplication.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView



@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(null)

                getMapAsync { map ->
                    map.setStyle(
                        "https://tiles.openfreemap.org/styles/liberty"
                    )

                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(55.751244, 37.618423))
                        .zoom(12.0)
                        .build()
                }
            }
        }
    )
}
