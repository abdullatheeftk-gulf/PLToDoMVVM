package com.example.pltodomvvm


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pltodomvvm.util.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SplashScreen"
@Composable
fun SplashScreen(
    isAuthenticated: Boolean,
    sharedViewModel: SharedViewModel,
    navigate: (route: String) -> Unit
) {
    var isSubscribed by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        sharedViewModel.isPurchased.collect {
            Log.e(TAG, "SplashScreen: $it", )
            isSubscribed = it
            Log.w(TAG, "SplashScreen: $isSubscribed", )
            delay(3000L)
            if (isAuthenticated && !isSubscribed) {
                Log.i(TAG, "isAuthenticated && !isSubscribed ")
                navigate(Routes.TODO_LIST)
            } else if (isAuthenticated && isSubscribed) {
                navigate(Routes.TODO_LIST)
                Log.i(TAG, "isAuthenticated && isSubscribed ")

            } else if (!isAuthenticated && isSubscribed) {
                navigate(Routes.FIREBASE_LOGIN)
                Log.i(TAG, "!isAuthenticated && isSubscribed ")

            } else if (!isAuthenticated && !isSubscribed) {
                navigate(Routes.TODO_LIST)
                Log.i(TAG, "!isAuthenticated && !isSubscribed")

            } else {
                navigate(Routes.TODO_LIST)
                Log.i(TAG, "nill ")

            }
        }



    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                fontFamily = FontFamily.Cursive,
                text = "ToDo",
                fontWeight = FontWeight.ExtraLight,
                fontStyle = FontStyle.Normal,
                fontSize = 40.sp
            )
        }
    }
}