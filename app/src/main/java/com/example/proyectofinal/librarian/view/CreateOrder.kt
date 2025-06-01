package com.example.proyectofinal.librarian.view

import android.app.DatePickerDialog
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.Color
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectofinal.librarian.viewmodel.CreateOrderViewModel
import com.example.proyectofinal.navigation.ScreensRoute
import com.example.proyectofinal.order.data.model.Order
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTaskScreen(
    navController: NavController
) {
    val viewModel: CreateOrderViewModel = koinViewModel()

    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val limitdate by viewModel.limitDate.collectAsState()

    val alumnos = listOf("Juan Pérez", "María Gómez", "Carlos Ruiz")
    var expanded by remember { mutableStateOf(false) }
    var selectedAlumno by remember { mutableStateOf<String?>(null) }

    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaFormateada = limitdate?.let { simpleDateFormat.format(it) } ?: ""
    var showDatePicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val currentDate = LocalDate.now().format(formatter)

    val context = LocalContext.current
    if (showDatePicker && !LocalInspectionMode.current) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                viewModel.onLimitDateChanged(cal.time)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setTitle("Seleccionar fecha")
            datePicker.minDate = calendar.timeInMillis
            show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color(0xFF4285F4), shape = CircleShape)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Crea una tarea",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 27.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.onNameChanged(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = fechaFormateada,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha de entrega") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha",
                                    modifier = Modifier.clickable { showDatePicker = true }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { viewModel.onDescriptionChanged(it) },
                            label = { Text("Descripción") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = selectedAlumno ?: "Alumno",
                                onValueChange = {},
                                label = {},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White) // ← Fondo blanco aquí

                            ) {
                                alumnos.forEach { alumno ->
                                    DropdownMenuItem(
                                        text = { Text(alumno) },
                                        onClick = {
                                            selectedAlumno = alumno
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Adjuntar archivo", modifier = Modifier.align(Alignment.Start))

                        IconButton(onClick = {
                            navController.navigate(ScreensRoute.Uploadfile.route)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Adjuntar archivo",
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val orderNew = Order(
                                    name = name,
                                    description = description,
                                    status = "PENDIENTE",
                                    limitdate = limitdate,
                                    filePath = "",
                                    file = TODO(),
                                    id = TODO(),
                                    creationdate = TODO(),
                                    createdbyuserid = TODO(),
                                    assigneduserid = TODO(),
                                    isFavorite = TODO(),
                                    lastRead = TODO(),
                                    pageCount = TODO(),
                                    hasComments = TODO()
                                )
                                viewModel.createOrder(orderNew)

                                      },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4285F4),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Crear")
                        }
                    }
                }
            }
        }
    }
}
