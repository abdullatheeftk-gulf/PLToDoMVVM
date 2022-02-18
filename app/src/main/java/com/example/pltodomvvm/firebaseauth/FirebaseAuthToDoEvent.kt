package com.example.pltodomvvm.firebaseauth

sealed class FirebaseAuthToDoEvent{
    data class OnEmailTextChange(val email:String):FirebaseAuthToDoEvent()
    data class OnPasswordTextChange(val password:String):FirebaseAuthToDoEvent()
    data class OnConfirmPasswordChange(val confirmPassword:String):FirebaseAuthToDoEvent()
    object OnNavigateToRegisterScreen:FirebaseAuthToDoEvent()
    object OnLoginButtonClicked:FirebaseAuthToDoEvent()
    object OnNavigateToLoginScreen:FirebaseAuthToDoEvent()
    object OnRegisterButtonClicked:FirebaseAuthToDoEvent()


}
