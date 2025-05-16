package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteScreen(
    navController: NavController
) {
    val viewModel = koinViewModel<HomeScreenViewModel>()
    val notasVm by  viewModel.tasks.collectAsState()
    val favoriteNotes = notasVm.filter { it.isFavorite }


    Scaffold(
        containerColor = Color.Black,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Header_fav()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(favoriteNotes) { nota ->
                    TaskCard(
                        task = nota,
                        onToggleFavorite = {  },
                        navController = navController
                    )
                 }
            }
        }
    }
}


@Composable
fun Header_fav() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { }) {
            Text("Tus favoritos", color = Color.White)
        }

        Column {
            Image(
                painter = painterResource(id = R.drawable.wirin_logo),
                contentDescription = "Logo de Wirin",
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = "WIRIN",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }

}


//@Composable
//fun Body_fav(titulo: String, descripcion: String) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.Black),
//        border = BorderStroke(1.dp, Color(0xFF00BFFF)),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = titulo,
//                color = Color.White,
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = descripcion,
//                color = Color.LightGray,
//                style = MaterialTheme.typography.bodyMedium,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//}



//@Composable
//fun NavBar() {
//    var selectedItem by remember { mutableStateOf(1) }
//
//    NavigationBar(
//        containerColor = Color.Black,
//        tonalElevation = 4.dp
//    ) {
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Article, contentDescription = "Apuntes") },
//            label = { Text("Apuntes") },
//            selected = selectedItem == 0,
//            onClick = { selectedItem = 0 },
//            colors = NavigationBarItemDefaults.colors(
//                selectedIconColor = Color.Yellow,
//                selectedTextColor = Color.Yellow,
//                unselectedIconColor = Color.White,
//                unselectedTextColor = Color.White,
//                indicatorColor = Color.Transparent
//            )
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.StarBorder, contentDescription = "Favoritos") },
//            label = { Text("Favoritos") },
//            selected = selectedItem == 1,
//            onClick = { selectedItem = 1 },
//            colors = NavigationBarItemDefaults.colors(
//                selectedIconColor = Color.Yellow,
//                selectedTextColor = Color.Yellow,
//                unselectedIconColor = Color.White,
//                unselectedTextColor = Color.White,
//                indicatorColor = Color.Transparent
//            )
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
//            label = { Text("Perfil") },
//            selected = selectedItem == 2,
//            onClick = { selectedItem = 2 },
//            colors = NavigationBarItemDefaults.colors(
//                selectedIconColor = Color.Yellow,
//                selectedTextColor = Color.Yellow,
//                unselectedIconColor = Color.White,
//                unselectedTextColor = Color.White,
//                indicatorColor = Color.Transparent
//            )
//        )
//    }
//}
