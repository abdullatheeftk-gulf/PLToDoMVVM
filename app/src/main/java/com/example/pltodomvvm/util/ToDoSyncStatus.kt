package com.example.pltodomvvm.util

sealed class ToDoSyncStatus{
    data class SyncStarted(val id:Int):ToDoSyncStatus()
    data class SyncStopped(val id:Int):ToDoSyncStatus()
    data class SyncError(val id:Int):ToDoSyncStatus()
}
