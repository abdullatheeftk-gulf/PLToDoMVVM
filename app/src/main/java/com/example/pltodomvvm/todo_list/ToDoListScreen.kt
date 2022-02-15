package com.example.pltodomvvm.todo_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pltodomvvm.components.ShowAlertDialog
import com.example.pltodomvvm.util.UiEvent
import kotlinx.coroutines.flow.collect

@Composable
fun ToDoListScreen(
    onNavigate: (uiEvent: UiEvent.Navigate) -> Unit,
    viewModel: ToDoViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val todos = viewModel.toDos.collectAsState(initial = emptyList())
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { value: UiEvent ->
            when (value) {
                is UiEvent.ShowSnackBar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = value.message,
                        actionLabel = value.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(toDoListEvent = ToDoListEvent.OnUndoClick)
                    }
                }
                is UiEvent.Navigate -> {
                    onNavigate(value)
                }

                else -> Unit
            }
        }
    }



    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(ToDoListEvent.OnAddToDoClick)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add ToDo")
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(todos.value) { toDo ->
                ToDoItem(
                    toDo = toDo,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(ToDoListEvent.OnToDoClick(toDo = toDo))
                        }
                        .padding(16.dp)
                )
            }
        }

    }


}