package com.example.pltodomvvm.todo_list

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.util.ToDoSyncStatus
import kotlinx.coroutines.flow.collect


private const val TAG = "ToDoItem"

@Composable
fun ToDoItem(
    modifier: Modifier = Modifier,
    viewModel: ToDoViewModel = hiltViewModel(),
    toDo: ToDo,
    onEvent: (event: ToDoListEvent) -> Unit,
    onDeleteIconClicked: (toDo: ToDo) -> Unit,
) {

    var progressBarVisibility by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(key1 = true) {
        viewModel.toDoSyncStatus.collect { value: ToDoSyncStatus ->
            when(value){
                is ToDoSyncStatus.SyncStarted->{
                    if (toDo.id == value.id){
                        progressBarVisibility = 1f
                    }
                }
                is ToDoSyncStatus.SyncStopped->{
                    if (toDo.id == value.id){
                        progressBarVisibility = 0f
                    }
                }
                is  ToDoSyncStatus.SyncError ->{

                }
            }
        }

    }



    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = toDo.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    onDeleteIconClicked(toDo)
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete button"
                    )
                }
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp)
                        .alpha(progressBarVisibility),
                    strokeWidth = 1.dp
                )
            }
            toDo.description?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it)
            }


        }
        Checkbox(
            checked = toDo.isDone, onCheckedChange = { isChecked ->
                onEvent(
                    ToDoListEvent.OnDoneChange(
                        toDo = toDo,
                        isDone = isChecked
                    )
                )
            })

    }

}

/*@Preview
@Composable
private fun ToDoItemPreview() {
    ToDoItem(
        toDo = ToDo(title = "Go to school", description = "Today", isDone = true, id = 1),
        onEvent = {

        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onDeleteIconClicked = {}

    )
}*/
