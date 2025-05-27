package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.R
import com.example.proyectofinal.core.theme.LocalTheme
import com.example.proyectofinal.core.ui.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StudentProfileScreen(modifier: Modifier = Modifier) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    var expanded by remember { mutableStateOf(false) }
    val isCustomFontFamilySelected by themeViewModel.fontFamilySelected.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Image(
                painter = painterResource(id = R.drawable.student_placeholder_profile_photo),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Placeholder for user info
            Column {
                Text("Información de usuario", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Nombre y Apellido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Carrera: Abogacia", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Modo Oscuro",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { contentDescription = "Toggle de modo oscuro" }
            )
            Switch(
                checked = LocalTheme.current.isDark,
                onCheckedChange = { themeViewModel.toggleTheme() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (isCustomFontFamilySelected) "Atkinson Hyperlegible" else "System Default",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                label = {
                    Text(
                        text = "Tipografia:",
                        fontFamily = LocalTheme.current.font ?: FontFamily.SansSerif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.semantics { contentDescription = "Selector de tipografía" }
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Abrir selector",
                        modifier = Modifier
                            .rotate(if (expanded) 180f else 0f)
                    )
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("System Default", "Atkinson Hyperlegible").forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = !expanded
                            themeViewModel.setFontFamilySelected(option == "Atkinson Hyperlegible")
                        },
                        text = { Text(option) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Blue buttons
        val buttonLabels = listOf("Aviso legal", "Sección Tutorial")
        buttonLabels.forEach { label ->
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A66FF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = label)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout button (Red)
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun PreviewStudentProfileScreen() {
    StudentProfileScreen(koinViewModel())
}