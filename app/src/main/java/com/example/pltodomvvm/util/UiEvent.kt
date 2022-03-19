package com.example.pltodomvvm.util

sealed class UiEvent{
    object ShowProgressBar:UiEvent()
    object CloseProgressBar:UiEvent()
    object ShowAlertDialog:UiEvent()
    data class Navigate(val route:String):UiEvent()
    data class ShowSnackBar(
    val message:String?,
    val action:String? = null
    ):UiEvent()

}
