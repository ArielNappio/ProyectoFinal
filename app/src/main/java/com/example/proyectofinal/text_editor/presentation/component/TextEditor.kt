package com.example.proyectofinal.text_editor.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.text_editor.presentation.viewmodel.TextEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TextEditor() {
    val viewModel = koinViewModel<TextEditorViewModel>()
    val isLoadingText by viewModel.isLoadingText.collectAsState()
    val textState by viewModel.textState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (isLoadingText) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Cargando...")
        }
    } else {
        TextField(
            value = textState,
            onValueChange = { string: String ->
                viewModel.updateString(string)
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable { focusRequester.requestFocus() }
                .focusRequester(focusRequester)
                .focusable(),
            keyboardOptions = KeyboardOptions.Default,
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusRequester.freeFocus()
                }
            )
        )
    }
}