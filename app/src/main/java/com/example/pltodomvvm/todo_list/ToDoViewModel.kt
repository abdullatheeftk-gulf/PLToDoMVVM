package com.example.pltodomvvm.todo_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.util.Routes
import com.example.pltodomvvm.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ToDoViewModel"
@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: ToDoRepository
):ViewModel() {

    val toDos:Flow<List<ToDo>> = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deleteToDo:ToDo? = null

    fun onEvent(event:ToDoListEvent){
        when(event){
            is ToDoListEvent.DeleteToDo ->{
                viewModelScope.launch {
                    deleteToDo=event.toDo
                    repository.deleteToDo(event.toDo)
                    sendUiEvent(UiEvent.ShowSnackBar(
                        message = "ToDo deleted: ${event.toDo.title}",
                        action = "Undo"
                    ))
                }
            }
            is ToDoListEvent.OnDoneChange ->{
                viewModelScope.launch {
                    repository.insertToDo(
                        event.toDo.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }
            is ToDoListEvent.OnToDoClick ->{
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO+"?todoid=${event.toDo.id}"))
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

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, " viewModel onCleared: ")
    }

}