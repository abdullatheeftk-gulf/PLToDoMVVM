package com.example.pltodomvvm


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pltodomvvm.util.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    isAuthenticated:Boolean,
    navigate: (route:String)->Unit
){
    LaunchedEffect(key1 = true ){
            delay(3000L)
            if (isAuthenticated){
                navigate(Routes.TODO_LIST)
            }else{
                navigate(Routes.FIREBASE_LOGIN)
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