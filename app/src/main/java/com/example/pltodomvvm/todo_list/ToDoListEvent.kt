package com.example.pltodomvvm.todo_list

import com.example.pltodomvvm.data.ToDo

sealed class ToDoListEvent{
    data class DeleteToDo(val toDo: ToDo):ToDoListEvent()
    data class OnDoneChange(val toDo: ToDo, val isDone:Boolean):ToDoListEvent()
    data class OnToDoClick(val toDo: ToDo):ToDoListEvent()
    data class SyncInProgress(val id:Int):ToDoListEvent()
    data class SyncInStopped(val id:Int):ToDoListEvent()
    data class SyncFailed(val id:Int,val exception: Exception):ToDoListEvent()
    object OnUndoClick:ToDoListEvent()
    object OnAddToDoClick:ToDoListEvent()
}
