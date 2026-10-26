package com.lab.myapplication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lab.myapplication.navigation.AppScreens
import com.lab.myapplication.navigation.NavigationPanel
import com.lab.myapplication.search.SearchRes
import com.lab.myapplication.ui.screens.FavoritesScreen
import com.lab.myapplication.ui.screens.MainScreen
import com.lab.myapplication.ui.screens.SettingsScreen

@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val favoritePlaces = remember {
        mutableStateListOf<SearchRes>()
    }

    var placeToOpenOnMap by remember {
        mutableStateOf<SearchRes?>(null)
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationPanel(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreens.MAP,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreens.MAP) {
                MainScreen(
                    favoritePlaces = favoritePlaces,
                    placeToOpenOnMap = placeToOpenOnMap,
                    onPlaceHandled = {
                        placeToOpenOnMap = null
                    }
                )
            }

            composable(AppScreens.FAVORITES) {
                FavoritesScreen(
                    favorites = favoritePlaces,
                    onPlaceClick = { place ->
                        placeToOpenOnMap = place

                    navController.navigate(AppScreens.MAP) {
                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = true
                        }
                    }

                    }
                )
            }

            composable(AppScreens.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}