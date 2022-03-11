package com.example.pltodomvvm.todo_list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.example.pltodomvvm.R
import com.example.pltodomvvm.ui.theme.topAppBarBackgroundColor
import com.example.pltodomvvm.util.SearchAppBarState

@Composable
fun ToDoTopBar(
    searchAppBarState: SearchAppBarState,
    searchActionEvent: (searchAppBarState: SearchAppBarState) -> Unit,
    searchTextValue: String,
    setSearchTextValue: (value: String) -> Unit
) {

    if (searchAppBarState == SearchAppBarState.CLOSED) {
        DefaultAppBar(searchActionEvent = searchActionEvent)
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
    searchActionEvent: (searchAppBarState: SearchAppBarState) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
            )
        },
        actions = {
            DefaultAppBarActions() {
                searchActionEvent(SearchAppBarState.OPENED)
            }
        }
    )
}

@Composable
fun DefaultAppBarActions(
    onSearchButtonClick: () -> Unit,
) {
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