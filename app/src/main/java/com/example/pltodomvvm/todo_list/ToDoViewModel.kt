package com.example.pltodomvvm.todo_list


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pltodomvvm.data.Converters
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

    private val _toDoSyncStatus = Channel<ToDoSyncStatus>()
    val toDoSyncStatus = _toDoSyncStatus.receiveAsFlow()

    private var deleteToDo: ToDo? = null

    private var _allToDos = MutableStateFlow<RequestState<List<ToDo>>>(RequestState.Idle)
    val allToDos: StateFlow<RequestState<List<ToDo>>> = _allToDos
    
    private var operationCounter = 0

    private var i = 0;


    init {
        setOperationCounter()
        setCounter()
        getAllToDos()
        val toDoInJson = savedStateHandle.get<String>("syncToDo")
        if (toDoInJson != null) {
            if (toDoInJson.isNotEmpty()) {
                val mToDo = Gson().fromJson(toDoInJson, ToDo::class.java)
                viewModelScope.launch {

                    repository.insertToDo(toDo = mToDo) {
                        repository.insertToDoFireStore(
                            syncToDo = FireToDo(
                                openDate = mToDo.openDate,
                                title = mToDo.title,
                                description = mToDo.description,
                                isDone = mToDo.isDone,
                                isSyncFinished = true,
                                closeDate = mToDo.closeDate
                            )
                        ) { fsi ->
                            when (fsi) {
                                is FireStoreInsertState.OnProgress -> {
                                    //change
                                    onEvent(ToDoListEvent.SyncInProgress(openDate = mToDo.openDate))
                                }
                                is FireStoreInsertState.OnSuccess -> {
                                    //change
                                    onEvent(ToDoListEvent.SyncInStopped(openDate = mToDo.openDate))
                                    viewModelScope.launch {
                                        repository.insertToDo(
                                            ToDo(
                                                title = fsi.inToDo.title,
                                                description = fsi.inToDo.description,
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
                                    //change
                                    onEvent(ToDoListEvent.SyncFailed(openDate = mToDo.openDate, fsi.exception))
                                }
                            }
                        }

                    }
                }
            }
        }

    }

    private fun setOperationCounter() {
        viewModelScope.launch {
            repository.incrementCounter()
        }
    }
    
   private fun setCounter(){
        viewModelScope.launch { 
            repository.getOperationCounterFlow().collect {
                operationCounter = it

            }
        }
    }


    private fun getAllToDos() {

        _allToDos.value = RequestState.Loading
        try {
            viewModelScope.launch {
                repository.getTodos().collect { listOfToDo ->
                    _allToDos.value = RequestState.Success(listOfToDo)
                     i++
                    if(operationCounter<1 && i<=1){
                         repository.getAllToDoesFromFireStore { fireToDoList ->
                                val toDos = mutableListOf<ToDo>()
                                fireToDoList.forEach { fToDo ->
                                    val mToDo = ToDo(
                                        title = fToDo.title,
                                        description = fToDo.description,
                                        isDone = fToDo.isDone,
                                        isSyncFinished = fToDo.isSyncFinished,
                                        openDate = fToDo.openDate,
                                        closeDate = fToDo.closeDate
                                    )
                                    toDos.add(mToDo)
                                }
                                viewModelScope.launch {
                                    repository.insertAllToDos(toDos = toDos)
                                }
                                _allToDos.value = RequestState.Success(fireToDoList)
                            }
                    }

                        
                    

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
                    repository.deleteToDo(toDoListEvent.toDo) {
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
                                isDone = toDoListEvent.toDo.isDone,
                                isSyncFinished = toDoListEvent.toDo.isSyncFinished,
                                openDate = toDoListEvent.toDo.openDate,
                                closeDate = toDoListEvent.toDo.closeDate
                            )
                        ) {

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
                    ) {
                        repository.insertToDoFireStore(
                            syncToDo = FireToDo(
                                title = toDoListEvent.toDo.title,
                                description = toDoListEvent.toDo.description,
                                isDone = toDoListEvent.isDone,
                                isSyncFinished = toDoListEvent.toDo.isSyncFinished,
                                openDate = toDoListEvent.toDo.openDate,
                                closeDate = toDoListEvent.closeDate
                            )
                        ) { fsi ->
                            when (fsi) {
                                is FireStoreInsertState.OnProgress -> {

                                    onEvent(ToDoListEvent.SyncInProgress(openDate = toDoListEvent.toDo.openDate))
                                }
                                is FireStoreInsertState.OnSuccess -> {
                                    onEvent(ToDoListEvent.SyncInStopped(openDate = toDoListEvent.toDo.openDate))
                                }
                                is FireStoreInsertState.OnFailure -> {
                                    onEvent(ToDoListEvent.SyncFailed(openDate = toDoListEvent.toDo.openDate, fsi.exception))
                                }
                            }

                        }
                    }
                }
            }
            is ToDoListEvent.OnToDoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=${Converters().dateToTimestamp(toDoListEvent.toDo.openDate)}"))
            }
            is ToDoListEvent.OnUndoClick -> {
                viewModelScope.launch {
                    deleteToDo?.let { insert ->
                        repository.insertToDo(insert) {
                            repository.insertToDoFireStore(
                                syncToDo = FireToDo(
                                    title = insert.title,
                                    description = insert.description,
                                    isDone = insert.isDone,
                                    isSyncFinished = insert.isSyncFinished,
                                    openDate = insert.openDate,
                                    closeDate = insert.closeDate
                                )
                            ) {

                            }
                        }
                    }
                }
            }
            is ToDoListEvent.OnAddToDoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO + "?todoId=-1"))
            }
            is ToDoListEvent.SyncInProgress -> {
                sendToDoSyncStatus(ToDoSyncStatus.SyncStarted(toDoListEvent.openDate))
            }
            is ToDoListEvent.SyncInStopped -> {
                sendToDoSyncStatus(ToDoSyncStatus.SyncStopped(toDoListEvent.openDate))
               /* sendUiEvent(
                    UiEvent.ShowSnackBar(
                        message = "${toDoListEvent.id} is synced to firestore"
                    )
                )*/

            }
            is ToDoListEvent.SyncFailed -> {
                //change
                sendToDoSyncStatus(ToDoSyncStatus.SyncError(openDate = toDoListEvent.openDate))
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