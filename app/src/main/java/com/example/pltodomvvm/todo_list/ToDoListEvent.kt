package com.example.pltodomvvm.todo_list

import com.example.pltodomvvm.data.ToDo
import java.util.*

sealed class ToDoListEvent{
    data class DeleteToDo(val toDo: ToDo):ToDoListEvent()
    data class OnDoneChange(val toDo: ToDo, val isDone:Boolean, val closeDate:Date?):ToDoListEvent()
    data class OnToDoClick(val toDo: ToDo):ToDoListEvent()
    data class SyncInProgress(val openDate:Date):ToDoListEvent()
    data class SyncInStopped(val openDate: Date):ToDoListEvent()
    data class SyncFailed(val openDate: Date,val exception: Exception):ToDoListEvent()
    object OnUndoClick:ToDoListEvent()
    object OnAddToDoClick:ToDoListEvent()
}
