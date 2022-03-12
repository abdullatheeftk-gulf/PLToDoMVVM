package com.example.pltodomvvm.add_edit_todo

import android.util.Log
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

private const val TAG = "AddEditTopBar"
@Composable
fun AddEditTopBar(
    viewModel: AddEditViewModel,
) {

    val topAppBarTitle by viewModel.topBarTitle.collectAsState()



    Log.w(TAG, "start $topAppBarTitle", )


    
    TopAppBar(
       title = {
           Text(text = topAppBarTitle)
       }

    ) 
}