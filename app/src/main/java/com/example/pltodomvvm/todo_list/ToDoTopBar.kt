package com.example.pltodomvvm.todo_list

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pltodomvvm.R
import com.example.pltodomvvm.SharedViewModel
import com.example.pltodomvvm.components.ShowAlertDialog
import com.example.pltodomvvm.ui.theme.topAppBarBackgroundColor
import com.example.pltodomvvm.util.SearchAppBarState
import kotlinx.coroutines.flow.collect

private const val TAG = "ToDoTopBar"
@Composable
fun ToDoTopBar(
    searchAppBarState: SearchAppBarState,
    searchActionEvent: (searchAppBarState: SearchAppBarState) -> Unit,
    searchTextValue: String,
    sharedViewModel: SharedViewModel,
    onSubscribeClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    onSignOutClicked: () -> Unit,
    setSearchTextValue: (value: String) -> Unit
) {


    var openDialog by remember {
        mutableStateOf(false)
    }

    var openSignOutDialog by remember {
        mutableStateOf(false)
    }

    ShowAlertDialog(
        title = "Do you want to Sign out?",
        message = "You wil sign out from your account and your data from this mobile deleted",
        openDialog = openSignOutDialog,
        onCloseClicked = {
            openSignOutDialog = false
        }
    ) {
        onSignOutClicked()
        openSignOutDialog = false
    }

    ShowAlertDialog(
        title = "Do you want to delete all Tasks",
        message = "It will delete all data from the device and backend",
        openDialog = openDialog,
        onCloseClicked = {
            openDialog = false
        }
    ) {
        onDeleteAllClicked()
        openDialog = false
    }

    if (searchAppBarState == SearchAppBarState.CLOSED) {
        DefaultAppBar(
            searchActionEvent = searchActionEvent,
            onSubscribeClicked = onSubscribeClicked,
            sharedViewModel = sharedViewModel,
            onSignOutClicked = {
                openSignOutDialog = true
            }

        ) {
            openDialog = true

        }
    } else {
        SearchAppBar(
            searchActionEvent = searchActionEvent,
            searchText = searchTextValue,
            onSearchValueChanged = {
                setSearchTextValue(it)
            }
        )
    }

}

@Composable
fun DefaultAppBar(
    searchActionEvent: (searchAppBarState: SearchAppBarState) -> Unit,
    onSignOutClicked: () -> Unit,
    sharedViewModel: SharedViewModel,
    onSubscribeClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,

    ) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
            )
        },
        actions = {
            DefaultAppBarActions(
                sharedViewModel = sharedViewModel,
                onDeleteAllClicked = onDeleteAllClicked,
                onSignOutClicked = onSignOutClicked,
                onSubscribeClicked = onSubscribeClicked
            ) {
                searchActionEvent(SearchAppBarState.OPENED)
            }
        }
    )
}

@Composable
fun DefaultAppBarActions(
    onSignOutClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit,
    sharedViewModel: SharedViewModel,
    onSubscribeClicked: () -> Unit,
    onSearchButtonClick: () -> Unit,
) {
    SearchAction {
        onSearchButtonClick()
    }
    DropDownMenuActions(
        onDeleteAllClicked = onDeleteAllClicked,
        onSubscribeClicked = onSubscribeClicked,
        sharedViewModel = sharedViewModel,
        onSignOutClicked = onSignOutClicked
    )
}

@Composable
fun DropDownMenuActions(
    onDeleteAllClicked: () -> Unit,
    onSubscribeClicked: () -> Unit,
    sharedViewModel: SharedViewModel,
    onSignOutClicked: () -> Unit,
) {
    var isSubscribed by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = true,){
        sharedViewModel.isPurchased.collect {
            isSubscribed = it
        }
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    IconButton(onClick = { expanded = true }) {
        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Drop Down menu")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(onClick = {
            onDeleteAllClicked()
            expanded = false
        }
        ) {

            Text(text = "Delete All")
        }

        if (!isSubscribed) {
            DropdownMenuItem(onClick = {
                onSignOutClicked()
                expanded = false

            }
            ) {

                Text(text = "Sign Out")
            }
        }
        DropdownMenuItem(onClick = {
            onSubscribeClicked()
            expanded = false
        }) {
            if (isSubscribed) {
                Text(text = "Subscribed", color = Color.Green)
            } else {
                Text(text = "Subscribe")
            }
        }

        DropdownMenuItem(onClick = {
            expanded = false
        }
        ) {

            Text(text = "About")
        }


    }
}

@Composable
fun SearchAction(onSearchButtonClick: () -> Unit) {
    IconButton(onClick = {
        onSearchButtonClick()
    }) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "search"
        )
    }
}

@Composable
fun SearchAppBar(
    searchActionEvent: (searchAppBarState: SearchAppBarState) -> Unit,
    searchText: String,
    onSearchValueChanged: (value: String) -> Unit
) {
    TopAppBar() {


        BackHandler() {
            searchActionEvent(SearchAppBarState.CLOSED)
            onSearchValueChanged("")
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.topAppBarBackgroundColor
        ) {

            TextField(
                singleLine = true,
                maxLines = 1,
                value = searchText,
                placeholder = {
                    Text(text = "Search", color = Color.White.copy(alpha = 0.3f))
                },
                textStyle = TextStyle(Color.White),
                onValueChange = {
                    onSearchValueChanged(it)
                })
        }

    }
}