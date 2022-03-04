package com.example.pltodomvvm.add_edit_todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.util.Routes
import com.example.pltodomvvm.util.UiEvent
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

//private const val TAG = "AddEditViewModel"

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: ToDoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var todo by mutableStateOf<ToDo?>(null)
        private set

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val toDoId = savedStateHandle.get<Int>("todoId")!!
        if (toDoId != -1) {
            viewModelScope.launch {
                repository.getToDoById(id = toDoId)?.let { toDo ->
                    title = toDo.title
                    description = toDo.description ?: ""
                    this@AddEditViewModel.todo = toDo
                }
            }

        }

    }

    fun onEvent(addEditToDoEvent: AddEditToDoEvent) {
        when (addEditToDoEvent) {

            is AddEditToDoEvent.OnTitleChange -> {
                title = addEditToDoEvent.title
            }

            is AddEditToDoEvent.OnDescriptionChange -> {
                description = addEditToDoEvent.description
            }

            is AddEditToDoEvent.OnSaveToDoClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (title.isBlank()) {
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "The title can't empty"
                            )
                        )
                        return@launch
                    }


                    val toDoToSend = ToDo(
                        title = title,
                        description = description,
                        isDone = todo?.isDone ?: false,
                        id = todo?.id,
                        openDate = todo?.openDate ?: Date(),
                        closeDate = todo?.closeDate,
                        isSyncFinished = todo?.isSyncFinished ?: true
                    )
                    val gson = Gson()
                    val jsonToDo = gson.toJson(toDoToSend)

                    sendUiEvent(UiEvent.Navigate(route = Routes.TODO_LIST + "?syncToDo=${jsonToDo}"))


                }
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
    }
}