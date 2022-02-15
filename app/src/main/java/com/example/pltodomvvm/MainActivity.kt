package com.example.pltodomvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pltodomvvm.add_edit_todo.AddEditToDoScreen
import com.example.pltodomvvm.todo_list.ToDoListScreen
import com.example.pltodomvvm.ui.theme.PLToDoMVVMTheme
import com.example.pltodomvvm.util.Routes
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PLToDoMVVMTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.TODO_LIST,
                ) {
                    composable(Routes.TODO_LIST) {
                        ToDoListScreen(onNavigate = {
                            navController.navigate(it.route)
                        }
                        )
                    }
                    composable(
                        route = Routes.ADD_EDIT_TODO+ "?todoId={todoId}",
                        arguments = listOf(
                            navArgument(name = "todoId"){
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) {
                        AddEditToDoScreen(onPopStack = {
                            navController.popBackStack()
                        })
                    }
                }

            }
        }
    }
}




