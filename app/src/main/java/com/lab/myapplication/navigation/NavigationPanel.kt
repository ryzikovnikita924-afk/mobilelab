package com.lab.myapplication.navigation


import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lab.myapplication.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon



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
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = "Карта"
                )
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
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Избранное"
                )
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
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Настройки"
                )
            },
            label = {
                Text(stringResource(R.string.nav_settings))
            }
        )
    }
}