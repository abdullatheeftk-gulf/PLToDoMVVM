package com.example.pltodomvvm.todo_list


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.*
import com.example.pltodomvvm.R
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.util.ToDoSyncStatus
import com.example.pltodomvvm.workmanager.MyWork
import com.google.gson.Gson
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

    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    var progressBarVisibility by remember {
        mutableStateOf(0f)
    }

    var errorVisibility by remember {
        mutableStateOf(0f)
    }
    LaunchedEffect(key1 = true) {

        viewModel.toDoSyncStatus.collect { value: ToDoSyncStatus ->
            when(value){
                is ToDoSyncStatus.SyncStarted->{
                    if (toDo.id == value.id){
                        progressBarVisibility = 1f
                        errorVisibility = 0f
                    }
                    /*val addData = Data.Builder()
                        .putString("syncToDo", Gson().toJson(toDo))
                        .build()

                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val addRequest = OneTimeWorkRequest.Builder(MyWork::class.java)
                        .setConstraints(constraints)
                        .setInputData(addData)
                        .build()
                    workManager.enqueue(addRequest)*/

                }
                is ToDoSyncStatus.SyncStopped->{
                    Log.i("test", "ToDoItem: ")
                    if (toDo.id == value.id){
                        progressBarVisibility = 0f
                        errorVisibility = 0f

                    }
                }
                is  ToDoSyncStatus.SyncError ->{
                    if (toDo.id == value.id){
                        progressBarVisibility = 0f
                        errorVisibility = 1f

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
