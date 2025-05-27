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
import androidx.compose.ui.platform.LocalInspectionMode
import com.example.proyectofinal.librarian.viewmodel.createOrderViewModel
import com.example.proyectofinal.order.data.model.Order
import com.example.proyectofinal.order.presentation.viewmodel.OrderViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun CreateTaskScreen() {


        val viewModel: createOrderViewModel = koinViewModel()

        val name by viewModel.name.collectAsState()
        val description by viewModel.description.collectAsState()
        val limitdate by viewModel.limitDate.collectAsState()


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
                { _, year, month, dayOfMonth ->val calendar = Calendar.getInstance()
                    calendar.set(year, month, dayOfMonth)
                    val it = calendar.time
                    viewModel.onLimitDateChanged(it)
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
                onClick = {  },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color(0xFF4285F4), shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Card(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Center),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .width(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.onNameChanged(it)   },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Adjuntar archivo", modifier = Modifier.align(Alignment.Start))
                    IconButton(onClick = { /* Acción adjuntar */ }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adjuntar archivo",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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

//falta agregar una lista de alumnos