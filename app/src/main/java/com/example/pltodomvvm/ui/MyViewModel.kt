package com.example.pltodomvvm.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MyViewModel:ViewModel() {

    val count:MutableState<Int> = mutableStateOf(0)

}