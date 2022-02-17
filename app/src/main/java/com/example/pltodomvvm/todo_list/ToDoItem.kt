package com.example.pltodomvvm.todo_list

import android.util.Log
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

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.pltodomvvm.data.ToDo




@Composable
fun ToDoItem(
    toDo: ToDo,
    onEvent: (event: ToDoListEvent) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteIconClicked:(toDo:ToDo)->Unit
) {



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

/*
@Preview
@Composable
private fun ToDoItemPreview() {
    ToDoItem(
        toDo = ToDo(title = "Go to school", description = "Today", isDone = true, id = 1),
        onEvent = {

        }
    )
}*/
