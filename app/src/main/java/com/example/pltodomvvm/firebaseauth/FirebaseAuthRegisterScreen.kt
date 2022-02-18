package com.example.pltodomvvm.firebaseauth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pltodomvvm.util.UiEvent
import kotlinx.coroutines.flow.collect

@Composable
fun FirebaseAuthRegisterScreen(
    onNavigate: (route:String)->Unit,
    viewModel: FirebaseAuthViewModel = hiltViewModel()
){
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                is UiEvent.Navigate->{
                    onNavigate(event.route)
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = viewModel.email,
                onValueChange = {
                    viewModel.onEvent(FirebaseAuthToDoEvent.OnEmailTextChange(it))
                },
                placeholder = {
                    Text(text = "email")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = viewModel.password,
                onValueChange = {
                    viewModel.onEvent(FirebaseAuthToDoEvent.OnPasswordTextChange(it))
                },
                placeholder = {
                    Text(text = "Password")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = viewModel.confirmPassword,
                onValueChange = {
                    viewModel.onEvent(FirebaseAuthToDoEvent.OnConfirmPasswordChange(it))
                },
                placeholder = {
                    Text(text = "Confirm Password")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.onEvent(FirebaseAuthToDoEvent.OnRegisterButtonClicked)
                },
            ) {
                Text(text = "Register")
            }
            Text(
                modifier = Modifier.clickable {
                    viewModel.onEvent(FirebaseAuthToDoEvent.OnNavigateToLoginScreen)
                },
                text = "Already login?"
            )
        }
    }

}