package com.example.pltodomvvm.todo_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.util.RequestState
import com.example.pltodomvvm.util.Routes
import com.example.pltodomvvm.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ToDoViewModel"
@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: ToDoRepository
):ViewModel() {

    private val _openFag = mutableStateOf(false)
    val openFlag = _openFag

    private val _openFlag = MutableStateFlow(false)
    val openFlags = _openFlag.asStateFlow()

    private val _toDoForDelete:MutableState<ToDo?> = mutableStateOf(null)
    val toDoForDelete = _toDoForDelete


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deleteToDo:ToDo? = null

    private var _allToDos = MutableStateFlow<RequestState<List<ToDo>>>(RequestState.Idle)
    val allToDos:StateFlow<RequestState<List<ToDo>>> = _allToDos

    init {
        getAllToDos()
    }


    private fun getAllToDos(){
        _allToDos.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.getTodos().collect {
                    _allToDos.value = RequestState.Success(it)
                }

            }
        }catch (e:Exception){
            _allToDos.value = RequestState.Error<java.lang.Exception>(e)
        }

    }

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
                    ){

                    }
                }
            }
            is ToDoListEvent.OnToDoClick ->{
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO+"?todoId=${toDoListEvent.toDo.id}"))
            }
            is ToDoListEvent.OnUndoClick ->{
                viewModelScope.launch {
                    deleteToDo?.let {
                        repository.insertToDo(it){

                        }
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