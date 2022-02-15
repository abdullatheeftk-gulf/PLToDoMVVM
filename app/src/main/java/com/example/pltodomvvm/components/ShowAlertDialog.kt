package com.example.pltodomvvm.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight


@Composable
fun ShowAlertDialog(
    title: String,
    message: String,
    openDialog: Boolean,
    onCloseClicked: () -> Unit,
    onYesClicked: () -> Unit
) {
    if (openDialog) {
      AlertDialog(
          title = {
                  Text(
                      text = title,
                      fontSize = MaterialTheme.typography.h5.fontSize,
                      fontWeight = FontWeight.Bold
                  )
          },
          text = {
                 Text(
                     text = message,
                     fontSize = MaterialTheme.typography.subtitle1.fontSize,
                     fontWeight = FontWeight.Normal
                 )
          },
          confirmButton = {
                 Button(
                     content = {
                               Text(text = "Yes")
                     },

                     onClick = {
                         onYesClicked()
                         onCloseClicked()
                     })
          },
          dismissButton = {
                  OutlinedButton(
                      onClick = {
                      onCloseClicked()
                  }
                  ) {
                      Text(text = "No")
                  }
          },
          onDismissRequest = {
              onCloseClicked()
          }
      )
    }
}