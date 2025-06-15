package com.example.proyectofinal.users.presentation.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ModificarVoluntarioPreview() {
    MaterialTheme {
        UpdateUserScreen(userId = "1")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateUserScreen(
    userId: String,
    viewModel: UserViewModel = koinViewModel()
) {
    val user by viewModel.selectedUser.collectAsState()
    val users by viewModel.users.collectAsState()

    var email by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var roles by remember { mutableStateOf("") }

    LaunchedEffect(userId, users) {
        if (users.isNotEmpty()) {
            viewModel.getUserById(userId)
        }
    }

    LaunchedEffect(user) {
        user?.let {
            email = it.email
            userName = it.userName
            password = it.password.toString()
            fullName = it.fullName
            phoneNumber = it.phoneNumber
            roles = it.roles.joinToString(", ")
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Blue,
        unfocusedBorderColor = Color.Blue
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
            text = "Modificar usuario",
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
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Email", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Nombre de usuario", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Nombre de usuario", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Nombre completo", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre completo", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Contraseña", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Teléfono", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Teléfono", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))


                Button(
                    onClick = {
                        val updatedUser = User(
                            id = userId,
                            email = email,
                            userName = userName,
                            password = password ,
                            fullName = fullName,
                            phoneNumber = phoneNumber,
                            roles = roles.split(",").map { it.trim() }
                            )
                        viewModel.changedUser(userId, updatedUser)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF007AFF),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.LightGray
                    )
                ) {
                    Text("Actualizar")
                }
            }
        }
    }
}

