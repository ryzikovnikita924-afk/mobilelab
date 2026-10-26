package com.lab.myapplication.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.lab.myapplication.search.SearchRes

@Composable
fun PlaceDetailsPanel(
    place: SearchRes,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {



                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Маршрут")
                }

                OutlinedButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (isFavorite) {
                            "В избранном ★"
                        } else {
                            "В избранное ☆"
                        }
                    )
                }
            }

            TextButton(onClick = onClose) {
                Text("Закрыть")
            }
        }
    }
}