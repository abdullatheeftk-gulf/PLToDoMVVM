package com.example.pltodomvvm.util

import com.example.pltodomvvm.data.ToDo

sealed class FireStoreInsertState{
    object OnProgress:FireStoreInsertState()
    data class OnSuccess(val inToDo:ToDo):FireStoreInsertState()
    data class OnFailure(val exception: Exception):FireStoreInsertState()
}
