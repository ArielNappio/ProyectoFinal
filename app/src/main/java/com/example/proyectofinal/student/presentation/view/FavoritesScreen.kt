package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel

@Preview(showBackground = true)
@Composable
fun FavoritesScreen(
    viewModel: HomeScreenViewModel = viewModel()
) {
    val notasVm by viewModel.notes.collectAsState()
    val favoriteNotes = notasVm.filter { it.isFavorite }

    Scaffold(
        containerColor = Color.Black,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(favoriteNotes) { nota ->
                    NoteCard(note = nota)
                 }
            }
        }
    }
}
