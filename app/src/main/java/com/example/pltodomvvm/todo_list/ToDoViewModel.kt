package com.example.pltodomvvm.todo_list


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pltodomvvm.data.FireToDo
import com.example.pltodomvvm.data.ToDo
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.util.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

//private const val TAG = "ToDoViewModel"

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

    private val _toDoSyncStatus = Channel<ToDoSyncStatus>()
    val toDoSyncStatus = _toDoSyncStatus.receiveAsFlow()

    private var deleteToDo: ToDo? = null

    private var _allToDos = MutableStateFlow<RequestState<List<ToDo>>>(RequestState.Idle)
    val allToDos: StateFlow<RequestState<List<ToDo>>> = _allToDos

    init {
        getAllToDos()
        val toDoInJson = savedStateHandle.get<String>("syncToDo")
        if (toDoInJson != null) {
            if (toDoInJson.isNotEmpty()) {
                val mToDo = Gson().fromJson(toDoInJson, ToDo::class.java)
                viewModelScope.launch {

                    repository.insertToDo(toDo = mToDo) { idLong ->
                        repository.insertToDoFireStore(
                            syncToDo = FireToDo(
                                id = idLong.toInt(),
                                openDate = mToDo.openDate.toString(),
                                title = mToDo.title,
                                description = mToDo.description,
                                isDone = mToDo.isDone,
                                isSyncFinished = true,
                                closeDate = mToDo.closeDate.toString()
                            )
                        ) { fsi ->
                            when (fsi) {
                                is FireStoreInsertState.OnProgress -> {
                                    onEvent(ToDoListEvent.SyncInProgress(idLong.toInt()))
                                }
                                is FireStoreInsertState.OnSuccess -> {
                                    onEvent(ToDoListEvent.SyncInStopped(idLong.toInt()))
                                    viewModelScope.launch {
                                        repository.insertToDo(
                                            ToDo(
                                                title = fsi.inToDo.title,
                                                description = fsi.inToDo.description,
                                                id = fsi.inToDo.id,
                                                openDate = mToDo.openDate,
                                                isSyncFinished = true,
                                                isDone = fsi.inToDo.isDone,
                                                closeDate = mToDo.closeDate
                                            )
                                        ) {

                                        }
                                    }
                                }
                                is FireStoreInsertState.OnFailure -> {
                                    onEvent(ToDoListEvent.SyncFailed(idLong.toInt(), fsi.exception))
                                }
                            }
                        }

                    }
                }
            }
        }

    }


    private fun getAllToDos() {
        /*repository.getAllToDoesFromFireStore {
            _allToDos.value = RequestState.Success(it)
        }*/
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
                    repository.deleteToDo(toDoListEvent.toDo){
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "ToDo deleted: ${toDoListEvent.toDo.title}",
                                action = "Undo"
                            )
                        )
                        repository.deleteFromFireStore(
                            deleteFireToDo = FireToDo(
                                title = toDoListEvent.toDo.title,
                                description = toDoListEvent.toDo.description,
                                id = toDoListEvent.toDo.id!!,
                                isDone = toDoListEvent.toDo.isDone,
                                isSyncFinished = toDoListEvent.toDo.isSyncFinished,
                                openDate = toDoListEvent.toDo.openDate.toString(),
                                closeDate = toDoListEvent.toDo.closeDate.toString()
                            )
                        ){

                        }
                    }

                }
            }
            is ToDoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    repository.insertToDo(
                        toDoListEvent.toDo.copy(
                            isDone = toDoListEvent.isDone,
                            closeDate = toDoListEvent.closeDate,
                        )
                    ) {id ->
                        repository.insertToDoFireStore(
                            syncToDo = FireToDo(
                                id = id.toInt(),
                                title = toDoListEvent.toDo.title,
                                description = toDoListEvent.toDo.description,
                                isDone = toDoListEvent.isDone,
                                isSyncFinished = toDoListEvent.toDo.isSyncFinished,
                                openDate = toDoListEvent.toDo.openDate.toString(),
                                closeDate = toDoListEvent.closeDate.toString()
                            )
                        ){fsi->
                            when(fsi){
                                is FireStoreInsertState.OnProgress->{
                                    onEvent(ToDoListEvent.SyncInProgress(id.toInt()))
                                }
                                is FireStoreInsertState.OnSuccess->{
                                    onEvent(ToDoListEvent.SyncInStopped(id.toInt()))
                                }
                                is FireStoreInsertState.OnFailure->{
                                    onEvent(ToDoListEvent.SyncFailed(id.toInt(), fsi.exception))
                                }
                            }

                        }
                    }
                }
            }
            is ToDoListEvent.OnToDoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=${toDoListEvent.toDo.id}"))
            }
            is ToDoListEvent.OnUndoClick -> {
                viewModelScope.launch {
                    deleteToDo?.let {insert->
                        repository.insertToDo(insert) {idLong->
                            repository.insertToDoFireStore(
                                syncToDo = FireToDo(
                                    title = insert.title,
                                    description = insert.description,
                                    id = insert.id!!,
                                    isDone = insert.isDone,
                                    isSyncFinished = insert.isSyncFinished,
                                    openDate = insert.openDate.toString(),
                                    closeDate = insert.closeDate.toString()
                                )
                            ){

                            }
                        }
                    }
                }
            }
            is ToDoListEvent.OnAddToDoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=-1"))
            }
            is ToDoListEvent.SyncInProgress -> {
                sendToDoSyncStatus(ToDoSyncStatus.SyncStarted(toDoListEvent.id))
            }
            is ToDoListEvent.SyncInStopped -> {
                sendToDoSyncStatus(ToDoSyncStatus.SyncStopped(toDoListEvent.id))
                sendUiEvent(
                    UiEvent.ShowSnackBar(
                        message = "${toDoListEvent.id} is synced to firestore"
                    )
                )

            }
            is ToDoListEvent.SyncFailed -> {
                sendToDoSyncStatus(ToDoSyncStatus.SyncError(id = toDoListEvent.id))
                sendUiEvent(UiEvent.ShowSnackBar(message = toDoListEvent.exception.message))
            }
        }
    }


    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun sendToDoSyncStatus(toDoSyncStatus: ToDoSyncStatus) {
        viewModelScope.launch {
            _toDoSyncStatus.send(toDoSyncStatus)
        }
    }


    override fun onCleared() {
        super.onCleared()
    }

}