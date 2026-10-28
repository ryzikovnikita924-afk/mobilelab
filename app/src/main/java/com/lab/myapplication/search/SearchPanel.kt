package com.lab.myapplication.search

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SearchPanel(
    modifier: Modifier = Modifier,
    onResultSelected: (SearchRes) -> Unit
) {
    var query by remember { mutableStateOf("") }

    var results by remember {
        mutableStateOf<List<SearchRes>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(query) {
        results = emptyList()
        error = null

        if (query.trim().length < 3) {
            loading = false
            return@LaunchedEffect
        }

        delay(500)

        loading = true

        try {
            results = SearchService.search(query.trim())
            Log.d(
                "SearchAPI",
                "Запрос: $query, результатов: ${results.size}"
            )
            if (results.isEmpty()) {
                error = "Ничего не найдено"
            }
        } catch (e: Exception) {
            Log.e("SearchAPI", "Ошибка поиска", e)
            error = "Ошибка поиска. Проверьте подключение к интернету."
        } finally {
            loading = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Куда поедем?") },
                placeholder = { Text("Введите адрес или место") },
                singleLine = true
            )

            if (loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 250.dp)
            ) {
                items(results) { result ->
                    Text(
                        text = result.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                query = result.name
                                results = emptyList()
                                onResultSelected(result)
                            }
                            .padding(12.dp)
                    )

                    HorizontalDivider()
                }
            }
        }
    }
}