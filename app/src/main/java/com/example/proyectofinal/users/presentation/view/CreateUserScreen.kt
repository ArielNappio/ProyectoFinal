package com.example.proyectofinal.users.presentation.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
 import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextField
 import androidx.compose.material3.Text
import androidx.compose.runtime.*
 import androidx.compose.ui.graphics.Color
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CrearVoluntarioPreview() {
    MaterialTheme {
        CreateUserScreen()
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateUserScreen() {

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }




    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor =  Color.Blue,
        unfocusedBorderColor =  Color.Blue
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Crear un nuevo voluntario",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )


        Divider()

        Spacer(modifier = Modifier.height(100.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Blue),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nombre y Apellido", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("value" , color = Color.Gray)  },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))


                Text("DNI", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text("value", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors


                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Email", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("value", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors

                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Edad", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("value", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors

                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {


                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Text("Crear")
                }
            }
        }
    }
}
