package com.example.pltodomvvm.todo_list

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pltodomvvm.components.ShowAlertDialog
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.util.UiEvent
import kotlinx.coroutines.flow.collect


@Composable
fun ToDoItem(
    toDo: ToDo,
    onEvent: (event: ToDoListEvent) -> Unit,
    modifier: Modifier = Modifier,

) {
    var openDialog by remember {
        mutableStateOf(false)
    }



    ShowAlertDialog(
        title = "Do You want to delete ${toDo.title}",
        message = "You are going to remove this",
        openDialog = openDialog,
        onCloseClicked = { openDialog = false  }
    ) {
        onEvent(ToDoListEvent.DeleteToDo(toDo = toDo))
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
                    openDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete button"
                    )
                }
            }
            toDo.description?.let{
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it,)
            }
            

        }
        Checkbox(
            checked = toDo.isDone, onCheckedChange ={isChecked->
            onEvent(ToDoListEvent.OnDoneChange(
                toDo = toDo,
                isDone = isChecked
            ))
        } )

    }

}

@Preview
@Composable
private fun ToDoItemPreview() {
    ToDoItem(
        toDo = ToDo(title = "Go to school", description = "Today", isDone = true, id = 1),
        onEvent = {

        }
    )
}