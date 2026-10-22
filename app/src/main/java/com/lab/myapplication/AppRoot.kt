package com.lab.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lab.myapplication.ui.screens.FoundWord

@Composable
fun AppRoot(modifier: Modifier = Modifier){
    FoundWord(modifier = modifier)
}