package com.example.pltodomvvm.util

sealed class UiEvent{
    object PopBackStack:UiEvent()
    object ShowProgressBar:UiEvent()
    object CloseProgressBar:UiEvent()
    data class Navigate(val route:String):UiEvent()
    data class ShowSnackBar(
    val message:String,
    val action:String? = null
    ):UiEvent()

}
