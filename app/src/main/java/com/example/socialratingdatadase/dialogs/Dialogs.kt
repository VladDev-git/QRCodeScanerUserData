package com.example.socialratingdatadase.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.socialratingdatadase.ui.theme.DialogDarkGray

@Composable
fun InputNameDialog(insertDialogState: MutableState<Boolean> ,onSubmit: @Composable (String) -> Unit) {
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
            insertDialogState.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                insertDialogState.value = false
            }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                insertDialogState.value = false
            }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DialogDarkGray)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Enter name")
                    TextField(value = dialogText.value, onValueChange = {
                        dialogText.value = it
                    })
                }
            }

        },
    )
}