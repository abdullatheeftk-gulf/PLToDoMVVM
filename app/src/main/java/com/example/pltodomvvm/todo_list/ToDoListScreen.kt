package com.example.pltodomvvm.todo_list

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pltodomvvm.components.ShowAlertDialog
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.util.RequestState
import com.example.pltodomvvm.util.UiEvent
import kotlinx.coroutines.flow.collect

private const val TAG = "ToDoListScreen"


@ExperimentalFoundationApi
@Composable
fun ToDoListScreen(
    onNavigate: (uiEvent: UiEvent.Navigate) -> Unit,
    viewModel: ToDoViewModel = hiltViewModel()
) {
    val lazyColumnState = LazyListState()


    val toDoForDelete: ToDo? by viewModel.toDoForDelete

    val openDialogFlag by viewModel.openFlag

    var openDialog by remember {
        mutableStateOf(openDialogFlag)
    }

    LaunchedEffect(key1 = openDialog) {
        viewModel.openFlag.value = openDialog
    }
    val allToDos by viewModel.allToDos.collectAsState()

    val scaffoldState = rememberScaffoldState()
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

    ShowAlertDialog(
        title = "Do you want to delete ${toDoForDelete?.title}",
        message = "I will erase from your database",
        openDialog = openDialog,
        onCloseClicked = {
            openDialog = false
        }) {
        toDoForDelete?.let {
            viewModel.onEvent(ToDoListEvent.DeleteToDo(it))
        }
        openDialog = false
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(ToDoListEvent.OnAddToDoClick)

            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add ToDo"
                )
            }
        }
    ) {
        if (allToDos is RequestState.Success) {
            val todos = (allToDos as RequestState.Success<List<ToDo>>).data
            if (todos.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyColumnState
                ) {

                    items(items = todos, key = {
                        it.id!!
                    }) { toDo ->
                        ToDoItem(
                            toDo = toDo,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onEvent(ToDoListEvent.OnToDoClick(toDo = toDo))
                                }
                                .padding(16.dp)
                                .animateItemPlacement(animationSpec = tween(300)),
                        ) { mToDo ->
                            viewModel.toDoForDelete.value = mToDo
                            openDialog = true
                            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()

                        }
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Empty list")
                }
            }
        } else if (allToDos is RequestState.Loading) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Loading....")
            }
        }

    }


}