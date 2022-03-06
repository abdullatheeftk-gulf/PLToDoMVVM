package com.example.pltodomvvm.util

import java.util.*

sealed class ToDoSyncStatus{
    data class SyncStarted(val openDate:Date):ToDoSyncStatus()
    data class SyncStopped(val openDate:Date):ToDoSyncStatus()
    data class SyncError(val openDate:Date):ToDoSyncStatus()
}
