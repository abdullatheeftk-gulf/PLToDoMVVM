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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pltodomvvm.SharedViewModel
import com.example.pltodomvvm.components.ShowAlertDialog
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.util.RequestState
import com.example.pltodomvvm.util.Routes
import com.example.pltodomvvm.util.UiEvent
import kotlinx.coroutines.flow.collect


@ExperimentalFoundationApi
@Composable
fun ToDoListScreen(
    onNavigate: (uiEvent: UiEvent.Navigate) -> Unit,
    onSubScribeClicked: () -> Unit,
    sharedViewModel: SharedViewModel,
    viewModel: ToDoViewModel = hiltViewModel()
) {

    var openSubscribeAlertDialog by remember {
        mutableStateOf(false)
    }


    LaunchedEffect(key1 = true){
        sharedViewModel.isPurchased.collect {
            viewModel.setIsPurchased(it)
        }
    }



    val lazyColumnState = remember {
        mutableStateOf(LazyListState())
    }

    val searchAppBarState by viewModel.searchAppBarState
    val searchTextValue by viewModel.searchTextValue

    val toDoForDelete: ToDo? by viewModel.toDoForDelete

    val openDeleteDialogFlag by viewModel.openDeleteDialogFlag

    var openDeleteDialog by remember {
        mutableStateOf(openDeleteDialogFlag)
    }

    LaunchedEffect(key1 = openDeleteDialog) {
        viewModel.openDeleteDialogFlag.value = openDeleteDialog
    }
    val allToDos by viewModel.allToDos.collectAsState()

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { value: UiEvent ->
            when (value) {
                is UiEvent.ShowSnackBar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = value.message!!,
                        actionLabel = value.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(toDoListEvent = ToDoListEvent.OnUndoClick)
                    }
                }
                is UiEvent.Navigate -> {
                    onNavigate(value)
                }
                is UiEvent.ShowAlertDialog ->{
                   openSubscribeAlertDialog = true
                }

                else -> Unit
            }
        }


    }

    LaunchedEffect(key1 = true){
        sharedViewModel.uiEvent.collect {value: UiEvent ->
            when(value){
                is UiEvent.ShowAlertDialog ->{
                    openSubscribeAlertDialog = true
                }

                else -> Unit
            }
        }
    }


    ShowAlertDialog(
        title = "Congratulation!. You have purchased",
        message ="You should be Sign in or Register to back up your data",
        openDialog =openSubscribeAlertDialog,
        onCloseClicked = {
            openSubscribeAlertDialog=false
        }
    ) {
        viewModel.onEvent(ToDoListEvent.Subscribed)
        openSubscribeAlertDialog=false
    }

    ShowAlertDialog(
        title = "Do you want to delete ${toDoForDelete?.title}",
        message = "It will erase from your database & FireStore",
        openDialog = openDeleteDialog,
        onCloseClicked = {
            openDeleteDialog = false
        }) {
        toDoForDelete?.let {
            viewModel.onEvent(ToDoListEvent.DeleteToDo(it))
        }
        openDeleteDialog = false
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ToDoTopBar(
                searchAppBarState = searchAppBarState,
                searchActionEvent = viewModel::searchActionEvent,
                searchTextValue = searchTextValue,
                setSearchTextValue = viewModel::setSearchTextValue,
                onDeleteAllClicked = viewModel::onDeleteAllClicked,
                onSignOutClicked = viewModel::signOut,
                onSubscribeClicked = onSubScribeClicked,
                sharedViewModel = sharedViewModel
            )
        },
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
                    state = lazyColumnState.value
                ) {

                    items(items = todos, key = {
                        it.openDate
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
                            viewModel = viewModel
                        ) { mToDo ->
                            viewModel.toDoForDelete.value = mToDo
                            openDeleteDialog = true
                            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()

                        }
                        Divider(color = Color.DarkGray)
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