package com.example.proyectofinal.text_edit.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.proyectofinal.text_edit.presentation.viewmodel.TextEditorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TextEditor() {
    val viewModel = koinViewModel<TextEditorViewModel>()
    val textState by viewModel.textState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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