package com.example.pltodomvvm.util

sealed class FireStoreInsertState{
    object OnProgress:FireStoreInsertState()
    data class OnSuccess(val isSuccess:Boolean = false):FireStoreInsertState()
    data class OnFailure(val exception: Exception):FireStoreInsertState()
}
