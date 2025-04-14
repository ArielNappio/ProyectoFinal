package com.example.proyectofinal.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.proyectofinal.presentation.viewmodel.TestViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.proyectofinal.util.NetworkResponse
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp


@Composable
fun TestScreen(
)
    {
        val viewmodel = koinViewModel<TestViewModel>()
        val state by viewmodel.getData.collectAsStateWithLifecycle(initialValue = null)

        LaunchedEffect(Unit) {
            viewmodel.gettinData()
        }

        when (val result = state) {
            is NetworkResponse.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is NetworkResponse.Success -> {
                Column {
                    Button(
                        onClick = { viewmodel.gettinData() }
                    ) {
                        Text("Retry")
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(result.data ?: emptyList()) { item ->
                            Text(
                                text = "${item.date}: ${item.temperatureC}°C - ${item.summary}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
            is NetworkResponse.Failure -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${result.error}")
                }
            }
            else -> {
                Text("Error: wau")
            }
        }

}