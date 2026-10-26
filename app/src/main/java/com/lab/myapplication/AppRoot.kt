package com.lab.myapplication

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.lab.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.os.LocaleListCompat
import com.lab.myapplication.data.AppDatabase
import com.lab.myapplication.data.toFavoriteEntity
import com.lab.myapplication.data.toSearchRes
import kotlinx.coroutines.launch

@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember {
        AppDatabase.getDatabase(context)
    }

    val favoriteDao = remember {
        database.favoriteDao()
    }

    val favoriteEntities by favoriteDao
        .getAllFavorites()
        .collectAsState(initial = emptyList())

    val favoritePlaces = remember(favoriteEntities) {
        favoriteEntities.map { it.toSearchRes() }
    }
    var isNavigating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val preferences = remember { UserPreferences(context) }
    val settings by preferences.settings.collectAsState(
        initial = UserSettings()
    )

    var placeToOpenOnMap by remember {
        mutableStateOf<SearchRes?>(null)
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    MyApplicationTheme(darkTheme = settings.darkTheme) {
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
                        onFavoriteToggle = { place ->
                            coroutineScope.launch {
                                val favoriteId = "${place.latitude},${place.longitude}"

                                if (favoritePlaces.any {
                                        "${it.latitude},${it.longitude}" == favoriteId
                                    }) {
                                    favoriteDao.deleteFavorite(favoriteId)
                                } else {
                                    favoriteDao.insertFavorite(place.toFavoriteEntity())
                                }
                            }
                        },
                        placeToOpenOnMap = placeToOpenOnMap,
                        onPlaceHandled = { placeToOpenOnMap = null },
                        isNavigating = isNavigating,
                        onNavigatingChange = { isNavigating = it }
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
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable(AppScreens.SETTINGS) {
                    SettingsScreen(
                        language = settings.language,
                        darkTheme = settings.darkTheme,
                        onLanguageChange = { language ->
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(language)
                            )
                            Log.d(
                                "LANGUAGE_DEBUG",
                                "Selected: $language, applied: ${
                                    AppCompatDelegate.getApplicationLocales().toLanguageTags()
                                }"
                            )

                            coroutineScope.launch {
                                preferences.setLanguage(language)
                            }
                        },
                        onThemeChange = { enabled ->
                            coroutineScope.launch {
                                preferences.setDarkTheme(enabled)
                            }
                        }
                    )
                }
            }
        }
    }
}