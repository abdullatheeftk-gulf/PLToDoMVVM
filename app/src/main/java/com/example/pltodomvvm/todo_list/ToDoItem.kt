package com.example.pltodomvvm.todo_list

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pltodomvvm.R
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.util.ToDoSyncStatus
import kotlinx.coroutines.flow.collect
import java.util.*


//private const val TAG = "ToDoItem"

@Composable
fun ToDoItem(
    modifier: Modifier = Modifier,
    viewModel: ToDoViewModel ,
    toDo: ToDo,
    onEvent: (event: ToDoListEvent) -> Unit,
    onDeleteIconClicked: (toDo: ToDo) -> Unit,
) {

    var progressBarVisibility by remember {
        mutableStateOf(0f)
    }

    var errorVisibility by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(key1 = true) {

        if(!toDo.isSyncFinished){
            errorVisibility = 0f
        }

        viewModel.toDoSyncStatus.collect { value: ToDoSyncStatus ->
            when(value){
                is ToDoSyncStatus.SyncStarted->{
                    if (toDo.openDate == value.openDate){
                        progressBarVisibility = 0f
                        errorVisibility = 0f
                    }
                }
                is ToDoSyncStatus.SyncStopped->{
                    if (toDo.openDate == value.openDate){
                        progressBarVisibility = 0f
                        errorVisibility = 0f
                    }
                }
                is  ToDoSyncStatus.SyncError ->{
                    if (toDo.openDate == value.openDate){
                        progressBarVisibility = 0f
                        errorVisibility = 0f

                    }
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
                Icon(
                    modifier = Modifier
                        .height(20.dp)
                        .width(20.dp)
                        .alpha(errorVisibility),
                    painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
                    contentDescription = "Error"
                )
            }
            toDo.description?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = it)
            }
            Spacer(modifier = modifier.height(2.dp))
            Text(
                text = "Open time: ${toDo.openDate}",
                color = Color.Green
            )
            toDo.closeDate?.let {
                Text(
                    text = "Close time: $it",
                    color = Color.Red
                )
            }
        }
        Checkbox(
            checked = toDo.isDone, onCheckedChange = { isChecked ->
                val mCloseDate:Date? = if (isChecked){
                    Date()
                }else{
                    null
                }
                onEvent(
                    ToDoListEvent.OnDoneChange(
                        toDo = toDo,
                        isDone = isChecked,
                        closeDate = mCloseDate
                    )
                )
            })
    }
}

