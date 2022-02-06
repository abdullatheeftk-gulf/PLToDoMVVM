package com.example.pltodomvvm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pltodomvvm.ui.MyViewModel
import com.example.pltodomvvm.ui.theme.PLToDoMVVMTheme

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    private val myViewModel:MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PLToDoMVVMTheme {

            }
        }
    }
}

@Composable
fun Greeting(name: String, myViewModel:MyViewModel,onClickButton:(Int)->Unit) {
    var onButtonClick by remember {
      mutableStateOf(0)
    }
    val count by myViewModel.count
    onButtonClick = count
    Column(modifier = Modifier
        .fillMaxSize(.9f)
        .padding(
            100.dp
        ),

    ) {
        Button(onClick = {

            onButtonClick++
            onClickButton(onButtonClick)

        }) {
            Text(text = "Button $onButtonClick")

        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp))

        Text(text = "Hello $name! $count")
    }

}


