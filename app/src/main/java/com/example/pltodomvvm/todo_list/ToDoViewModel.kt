package com.example.pltodomvvm.todo_list

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.util.FireStoreInsertState
import com.example.pltodomvvm.util.RequestState
import com.example.pltodomvvm.util.Routes
import com.example.pltodomvvm.util.UiEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ToDoViewModel"

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val repository: ToDoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _openFag = mutableStateOf(false)
    val openFlag = _openFag

    private val _toDoForDelete: MutableState<ToDo?> = mutableStateOf(null)
    val toDoForDelete = _toDoForDelete

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deleteToDo: ToDo? = null

    private var _allToDos = MutableStateFlow<RequestState<List<ToDo>>>(RequestState.Idle)
    val allToDos: StateFlow<RequestState<List<ToDo>>> = _allToDos

    init {
        getAllToDos()
        val toDoInJson = savedStateHandle.get<String>("syncToDo")
        if (!toDoInJson?.isEmpty()!!) {
            val mToDo = Gson().fromJson(toDoInJson,ToDo::class.java)
            viewModelScope.launch { 
                repository.insertToDo(toDo = mToDo){id->
                    repository.insertIntoFireStore(toDo = mToDo.copy(id = id.toInt())){fireStoreState->
                        when(fireStoreState){
                            is FireStoreInsertState.OnSuccess->{
                                Log.d(TAG, "success: ${fireStoreState.isSuccess}")
                            }
                            is FireStoreInsertState.OnFailure->{
                                Log.e(TAG, "failure: ${fireStoreState.exception} ", )
                            }
                            is FireStoreInsertState.OnProgress->{
                                Log.d(TAG, "onProgress:onProgress ")
                            }
                        }
                    }
                }
            }
        }
    }


    private fun getAllToDos() {
        _allToDos.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.getTodos().collect {
                    _allToDos.value = RequestState.Success(it)
                }
            }
        } catch (e: Exception) {
            _allToDos.value = RequestState.Error<java.lang.Exception>(e)
        }

    }

    fun onEvent(toDoListEvent: ToDoListEvent) {
        when (toDoListEvent) {

            is ToDoListEvent.DeleteToDo -> {
                viewModelScope.launch {
                    deleteToDo = toDoListEvent.toDo
                    repository.deleteToDo(toDoListEvent.toDo)
                    sendUiEvent(
                        UiEvent.ShowSnackBar(
                            message = "ToDo deleted: ${toDoListEvent.toDo.title}",
                            action = "Undo"
                        )
                    )
                }
            }
            is ToDoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    repository.insertToDo(
                        toDoListEvent.toDo.copy(
                            isDone = toDoListEvent.isDone
                        )
                    ){

                    }
                }
            }
            is ToDoListEvent.OnToDoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=${toDoListEvent.toDo.id}"))
            }
            is ToDoListEvent.OnUndoClick -> {
                viewModelScope.launch {
                    deleteToDo?.let {
                        repository.insertToDo(it){

                        }
                    }
                }
            }
            is ToDoListEvent.OnAddToDoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO+"?todoId=-1"))
            }
            is ToDoListEvent.SyncInProgress->{

            }
            is ToDoListEvent.StopProgress->{

            }
        }
    }


    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "ToDo viewModel onCleared: ")
    }

}