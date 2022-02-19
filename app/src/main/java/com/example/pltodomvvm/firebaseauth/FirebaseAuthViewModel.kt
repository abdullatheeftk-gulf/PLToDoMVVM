package com.example.pltodomvvm.firebaseauth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.util.FirebaseAuthState
import com.example.pltodomvvm.util.Routes
import com.example.pltodomvvm.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.annotation.meta.When
import javax.inject.Inject

private const val TAG = "FirebaseAuthViewModel"

@HiltViewModel
class FirebaseAuthViewModel @Inject constructor(
    private val repository: ToDoRepository
) : ViewModel() {

    var email by mutableStateOf<String>("")
        private set

    var password by mutableStateOf<String>("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var isAuthenticated by mutableStateOf(false)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(firebaseAuthToDoEvent: FirebaseAuthToDoEvent) {
        when (firebaseAuthToDoEvent) {
            is FirebaseAuthToDoEvent.OnEmailTextChange -> {
                email = firebaseAuthToDoEvent.email
            }
            is FirebaseAuthToDoEvent.OnPasswordTextChange -> {
                password = firebaseAuthToDoEvent.password
            }
            is FirebaseAuthToDoEvent.OnLoginButtonClicked -> {
                viewModelScope.launch {
                    if (email.isBlank()) {
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "email should be entered"
                            )
                        )
                        return@launch
                    }
                    if (password.isBlank()) {
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "password should be entered"
                            )
                        )
                        return@launch
                    }
                    repository.signInWithEmailAndPassword(
                        email = email,
                        password = password
                    ) { authState: FirebaseAuthState ->
                        when (authState) {
                            is FirebaseAuthState.OnAuthSuccess -> {
                                sendUiEvent(UiEvent.CloseProgressBar)

                                Log.i(
                                    TAG,
                                    "sign in onAuthSuccess: ${authState.authResult.user?.email}"
                                )
                                sendUiEvent(UiEvent.Navigate(Routes.TODO_LIST))
                            }
                            is FirebaseAuthState.OnAuthFailure -> {
                                sendUiEvent(UiEvent.CloseProgressBar)
                                Log.e(TAG, "sign in onAuthFailure: ${authState.authException}")
                                sendUiEvent(
                                    UiEvent.ShowSnackBar(
                                        message = "sign in There have some error when authenticating, Error code is ${authState.authException.message}"
                                    )
                                )
                            }
                            else -> {

                                sendUiEvent(UiEvent.ShowProgressBar)
                                Log.i(TAG, "sign in AuthState: Loading---")
                            }
                        }
                    }

                }
            }
            is FirebaseAuthToDoEvent.OnNavigateToRegisterScreen -> {
                sendUiEvent(UiEvent.Navigate(Routes.FIREBASE_REGISTER))
            }
            is FirebaseAuthToDoEvent.OnConfirmPasswordChange -> {
                confirmPassword = firebaseAuthToDoEvent.confirmPassword
            }

            is FirebaseAuthToDoEvent.OnRegisterButtonClicked -> {
                viewModelScope.launch {
                    if (email.isBlank()) {
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "email should be entered"
                            )
                        )
                        return@launch
                    }
                    if (password.isBlank()) {
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "password should be entered"
                            )
                        )
                        return@launch
                    }
                    if (confirmPassword.isBlank()) {
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "confirm password should be entered"
                            )
                        )
                        return@launch
                    }
                    repository.createUserWithEmailAndPassword(
                        email = email,
                        password = password
                    ) { authState ->
                        when (authState) {
                            is FirebaseAuthState.OnAuthSuccess -> {
                                sendUiEvent(UiEvent.CloseProgressBar)
                                Log.i(
                                    TAG,
                                    "register onAuthSuccess: ${authState.authResult.user?.email}"
                                )
                                sendUiEvent(UiEvent.Navigate(Routes.TODO_LIST))
                            }
                            is FirebaseAuthState.OnAuthFailure -> {
                                sendUiEvent(UiEvent.CloseProgressBar)
                                Log.e(TAG, "register onAuthFailure:${authState.authException} ")
                                sendUiEvent(
                                    UiEvent.ShowSnackBar(
                                        message = "register There have some error when authenticating, Error code is ${authState.authException.message}"
                                    )
                                )
                            }
                            else -> {
                                sendUiEvent(UiEvent.ShowProgressBar)
                                Log.i(TAG, "register AuthState: Loading---")
                            }
                        }
                    }

                }
            }
            is FirebaseAuthToDoEvent.OnNavigateToLoginScreen -> {
                sendUiEvent(UiEvent.Navigate(Routes.FIREBASE_LOGIN))
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}