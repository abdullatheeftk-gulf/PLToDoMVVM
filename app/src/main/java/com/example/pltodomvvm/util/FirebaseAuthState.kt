package com.example.pltodomvvm.util

import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult

sealed class FirebaseAuthState{
    object OnAuthLoading:FirebaseAuthState()
    data class OnAuthSuccess(val authResult: AuthResult):FirebaseAuthState()
    data class OnAuthFailure(val authException: Exception):FirebaseAuthState()

}
