package com.lab.myapplication.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.annotation.Symbol
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions

class LocationMarker(
    private val manager: SymbolManager
) {
    private var symbol: Symbol? = null

    fun update(latitude: Double, longitude: Double) {
        val position = LatLng(latitude, longitude)

        if (symbol == null) {
            symbol = manager.create(
                SymbolOptions()
                    .withLatLng(position)
                    .withIconImage(ICON_ID)
                    .withIconSize(1.0f)
            )
        } else {
            symbol?.let {
                it.latLng = position
                manager.update(it)
            }
        }
    }

    fun remove() {
        symbol?.let {
            manager.delete(it)
        }

        symbol = null
    }

    companion object {
        const val ICON_ID = "user-location"

        fun addImageToStyle(style: Style) {
            style.addImage(ICON_ID, createBitmap())
        }

        private fun createBitmap(): Bitmap {
            val size = 64

            val bitmap = Bitmap.createBitmap(
                size,
                size,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            paint.color = Color.argb(55, 0, 188, 212)
            canvas.drawCircle(32f, 32f, 30f, paint)

            paint.color = Color.WHITE
            canvas.drawCircle(32f, 32f, 17f, paint)

            paint.color = Color.rgb(0, 188, 212)
            canvas.drawCircle(32f, 32f, 13f, paint)

            return bitmap
        }
    }
}