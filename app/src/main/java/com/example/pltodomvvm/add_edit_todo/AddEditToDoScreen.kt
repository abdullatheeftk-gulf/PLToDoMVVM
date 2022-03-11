package com.example.pltodomvvm.add_edit_todo

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pltodomvvm.util.UiEvent
import kotlinx.coroutines.flow.collect

@Composable
fun AddEditToDoScreen(
    onNavigate: (route:String) -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { value: UiEvent ->
            when (value) {
                is UiEvent.Navigate -> {
                    onNavigate(value.route)
                }
                is UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = value.message!!,
                        actionLabel = value.action
                    )
                }
                else -> Unit
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
                 AddEditTopBar(viewModel = viewModel)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddEditToDoEvent.OnSaveToDoClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = viewModel.title,
                onValueChange = {
                    viewModel.onEvent(AddEditToDoEvent.OnTitleChange(it))
                },
                placeholder = {
                    Text("title")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = viewModel.description,
                onValueChange = {
                    viewModel.onEvent(
                        AddEditToDoEvent.OnDescriptionChange(it)
                    )
                },
                placeholder = {
                    Text("description")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 5
            )

        }
    }

}