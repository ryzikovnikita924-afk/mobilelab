package com.lab.myapplication.navigation


import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NavigationPanel(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == AppScreens.MAP,
            onClick = {
                onNavigate(AppScreens.MAP)
            },
            icon = {
                Text("M")
            },
            label = {
                Text("Карта")
            }
        )

        NavigationBarItem(
            selected = currentRoute == AppScreens.FAVORITES,
            onClick = {
                onNavigate(AppScreens.FAVORITES)
            },
            icon = {
                Text("F")
            },
            label = {
                Text("Избранное")
            }
        )

        NavigationBarItem(
            selected = currentRoute == AppScreens.SETTINGS,
            onClick = {
                onNavigate(AppScreens.SETTINGS)
            },
            icon = {
                Text("S")
            },
            label = {
                Text("Настройки")
            }
        )
    }
}