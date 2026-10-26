package com.lab.myapplication.ui.map


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.annotation.SymbolManager


@Composable
fun MapVis(
    modifier: Modifier = Modifier,
    onMapReady: (MapComponents) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                getMapAsync { map ->
                    map.setStyle(
                        Style.Builder().fromUri(
                            "https://tiles.openfreemap.org/styles/liberty"
                        )
                    ) { style ->
                        LocationMarker.addImageToStyle(style)

                        val manager = SymbolManager(
                            this,
                            map,
                            style
                        )

                        manager.iconAllowOverlap = true
                        manager.iconIgnorePlacement = true

                        onMapReady(
                            MapComponents(
                                mapView = this,
                                map = map,
                                marker = LocationMarker(manager)
                            )
                        )
                    }
                }
            }
        }
    )
}
