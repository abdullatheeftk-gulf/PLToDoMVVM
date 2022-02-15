package com.example.pltodomvvm.todo_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.util.Routes
import com.example.pltodomvvm.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ToDoViewModel"
@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: ToDoRepository
):ViewModel() {

    private val _openFag = mutableStateOf(false)
    val openFlag = _openFag

    private val _toDoForDelete:MutableState<ToDo?> = mutableStateOf(null)
    val toDoForDelete = _toDoForDelete

    val toDos:Flow<List<ToDo>> = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deleteToDo:ToDo? = null

    fun onEvent(toDoListEvent:ToDoListEvent){
        when(toDoListEvent){

            is ToDoListEvent.DeleteToDo ->{
                viewModelScope.launch {
                    deleteToDo=toDoListEvent.toDo
                    repository.deleteToDo(toDoListEvent.toDo)
                    sendUiEvent(UiEvent.ShowSnackBar(
                        message = "ToDo deleted: ${toDoListEvent.toDo.title}",
                        action = "Undo"
                    ))
                }
            }
            is ToDoListEvent.OnDoneChange ->{
                viewModelScope.launch {
                    repository.insertToDo(
                        toDoListEvent.toDo.copy(
                            isDone = toDoListEvent.isDone
                        )
                    )
                }
            }
            is ToDoListEvent.OnToDoClick ->{
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO+"?todoId=${toDoListEvent.toDo.id}"))
            }
            is ToDoListEvent.OnUndoClick ->{
                viewModelScope.launch {
                    deleteToDo?.let {
                        repository.insertToDo(it)
                    }

                }
            }
            is ToDoListEvent.OnAddToDoClick ->{
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO))
            }
        }
    }

    init {
        Log.d(TAG, ":todoViewmodel init ")
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "ToDo viewModel onCleared: ")
    }

}