package com.example.pltodomvvm.add_edit_todo

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AddEditTopBar(viewModel: AddEditViewModel) {
    var title by remember {
        mutableStateOf("Add")
    }

    if (viewModel.title.isNotEmpty()){
        title = "Edit"
    }
    TopAppBar(
       title = {
           Text(text = title)
       }

    ) 
}