package com.lab.myapplication.navigation


import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lab.myapplication.R

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
                Text(stringResource(R.string.nav_map))
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
                Text(stringResource(R.string.nav_favorites))
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
                Text(stringResource(R.string.nav_settings))
            }
        )
    }
}