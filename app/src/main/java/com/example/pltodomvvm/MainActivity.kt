package com.example.pltodomvvm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pltodomvvm.todo_list.ToDoViewModel
import com.example.pltodomvvm.ui.theme.PLToDoMVVMTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel:ToDoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



       /* lifecycleScope.launchWhenStarted {
            Log.w(TAG, "thread name: ${Thread.currentThread().name}", )
            viewModel.toDos.collect {list->
                Log.e(TAG, "-----------------------------", )
                list.forEach { toDo ->
                    Log.d(TAG, "toDos: $toDo")

                }
            }
        }*/


        setContent {
            PLToDoMVVMTheme {

            }
        }
    }
}




