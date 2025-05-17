package com.example.proyectofinal.student.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Modo Oscuro",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { contentDescription = "Toggle de modo oscuro" }
            )
            Switch(
                checked = LocalTheme.current.isDark,
                onCheckedChange = { themeViewModel.toggleTheme() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                label = { Text(
                    text = "Tipografia:",
                    fontFamily = LocalTheme.current.font ?: FontFamily.SansSerif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { contentDescription = "Selector de tipografía" }
                ) }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("System Default", "Atkinson Hyperlegible").forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            themeViewModel.setFontFamilySelected(option == "Atkinson Hyperlegible")
                        },
                        text = { Text(option) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewStudentProfileScreen() {
    StudentProfileScreen(koinViewModel())
}