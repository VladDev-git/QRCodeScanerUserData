package com.example.socialratingdatadase.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.socialratingdatadase.ui.theme.DialogDarkGray

@Composable
fun InputNameDialog(inputDialogState: MutableState<Boolean> ,onSubmit: (String) -> Unit) {
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(
        modifier = Modifier
            .size(300.dp),
        containerColor = DialogDarkGray,
        tonalElevation = 0.dp,
        onDismissRequest = {
            inputDialogState.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                inputDialogState.value = false
            },
                modifier = Modifier
                    .background(Color.Red)
            ) {
                Text(
                    text = "OK",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                inputDialogState.value = false
            },
                modifier = Modifier
                    .background(Color.Red)
                ) {
                Text(
                    text = "Cancel",
                    color = Color.White
                )
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DialogDarkGray)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Enter name",
                        color = Color.White
                    )
                    TextField(value = dialogText.value, onValueChange = {
                        dialogText.value = it
                    })
                }
            }
        },
    )
}

@Composable
fun GetRatingScoreDialog(ratingDialogState: MutableState<Boolean> ,onSubmit: (String) -> Unit) {
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(
        modifier = Modifier
            .size(300.dp),
        containerColor = DialogDarkGray,
        tonalElevation = 0.dp,
        onDismissRequest = {
            ratingDialogState.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                ratingDialogState.value = false
            },
                modifier = Modifier
                    .background(Color.Red)
            ) {
                Text(
                    text = "OK",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                ratingDialogState.value = false
            },
                modifier = Modifier
                    .background(Color.Red)
            ) {
                Text(
                    text = "Cancel",
                    color = Color.White
                )
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DialogDarkGray)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Enter rating score",
                        color = Color.White
                    )
                    TextField(value = dialogText.value, onValueChange = {
                        dialogText.value = it
                    })
                }
            }
        },
    )
}