package com.lab.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lab.myapplication.FoundWord.runsort


@Composable
fun FoundWord(modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }
    var wordlist by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = input,
            onValueChange = { newValue ->
                input = newValue

            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Введите текст") },
        )

        OutlinedTextField(
            value = wordlist,
            onValueChange = { newValue ->
                wordlist = newValue

            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Введите список слов") },
        )

        Text(
            text = output,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                output = runsort(input, wordlist)

            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Отсортировать")
        }


    }
}


