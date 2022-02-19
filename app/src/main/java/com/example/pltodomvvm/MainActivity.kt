package com.example.pltodomvvm

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pltodomvvm.add_edit_todo.AddEditToDoScreen
import com.example.pltodomvvm.firebaseauth.FireBaseAuthLoginScreen
import com.example.pltodomvvm.firebaseauth.FirebaseAuthRegisterScreen
import com.example.pltodomvvm.todo_list.ToDoListScreen
import com.example.pltodomvvm.ui.theme.PLToDoMVVMTheme
import com.example.pltodomvvm.util.Routes
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"


@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var auth:FirebaseAuth
    private var isAuthenticated:Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        isAuthenticated = currentUser != null
        Log.i(TAG, "onStart: $isAuthenticated")
        setContent {
            PLToDoMVVMTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH_SCREEN,
                ) {
                    composable(route = Routes.SPLASH_SCREEN){
                        SplashScreen(isAuthenticated = isAuthenticated){
                            navController.navigate(it){
                                popUpTo(Routes.SPLASH_SCREEN){
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(route = Routes.FIREBASE_LOGIN){
                        FireBaseAuthLoginScreen{
                            navController.navigate(route = it){
                                popUpTo(Routes.FIREBASE_LOGIN){
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(route = Routes.FIREBASE_REGISTER){
                        FirebaseAuthRegisterScreen(onNavigate = {
                            navController.navigate(route = it){
                                popUpTo(Routes.FIREBASE_REGISTER){
                                    inclusive = true
                                }
                            }
                        })
                    }

                    composable(
                        route=Routes.TODO_LIST + "?syncToDoId={syncToDoId}",
                        arguments = listOf(
                            navArgument(name = "syncToDoId"){
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) {
                        ToDoListScreen(onNavigate = {
                            navController.navigate(it.route)
                        })
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

                        AddEditToDoScreen(onNavigate = {
                            navController.navigate(route = it){
                                popUpTo(Routes.ADD_EDIT_TODO + "?todoId={todoId}"){
                                    inclusive = true
                                }

                            }
                        })
                    }
                }

            }
        }

    }


}




