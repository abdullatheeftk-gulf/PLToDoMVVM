package com.example.pltodomvvm.util

import com.example.pltodomvvm.data.ToDo

sealed class RequestState1{
    object Idle:RequestState1()
    object Loading:RequestState1()
    data class Success(val toDo: ToDo):RequestState1()
    data class Error(val exception: Exception):RequestState1()
}
