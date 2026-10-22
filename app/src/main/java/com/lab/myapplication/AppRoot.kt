package com.lab.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lab.myapplication.ui.screens.MainScreen

@Composable
fun AppRoot(modifier: Modifier = Modifier){
    MainScreen(modifier = modifier)
}