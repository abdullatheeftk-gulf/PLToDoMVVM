package com.example.pltodomvvm

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.example.pltodomvvm.add_edit_todo.AddEditToDoScreen
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.firebaseauth.FireBaseAuthLoginScreen
import com.example.pltodomvvm.firebaseauth.FirebaseAuthRegisterScreen
import com.example.pltodomvvm.todo_list.ToDoListScreen
import com.example.pltodomvvm.ui.theme.PLToDoMVVMTheme
import com.example.pltodomvvm.util.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"


@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var repository: ToDoRepository

    @Inject
    lateinit var fdb:FirebaseFirestore

    @Inject
    lateinit var billingClient:BillingClient

    private var isAuthenticated: Boolean = false


    override fun onStart() {
        super.onStart()
        
        billingClient.startConnection(object:BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                Log.i(TAG, "onBillingServiceDisconnected: ")
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                Log.e(TAG, "onBillingSetupFinished: $p0", )
            }

        })



        val currentUser = auth.currentUser
        isAuthenticated = currentUser != null
        setContent {
            PLToDoMVVMTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH_SCREEN,
                ) {
                    composable(route = Routes.SPLASH_SCREEN) {

                        SplashScreen(isAuthenticated = isAuthenticated) {
                            navController.navigate(it) {
                                popUpTo(Routes.SPLASH_SCREEN) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(route = Routes.FIREBASE_LOGIN) {
                        FireBaseAuthLoginScreen {
                            navController.navigate(route = it) {
                                popUpTo(Routes.FIREBASE_LOGIN) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(route = Routes.FIREBASE_REGISTER) {
                        FirebaseAuthRegisterScreen(onNavigate = {
                            navController.navigate(route = it) {
                                popUpTo(Routes.FIREBASE_REGISTER) {
                                    inclusive = true
                                }
                            }
                        })
                    }

                    composable(
                        route = Routes.TODO_LIST + "?syncToDo={syncToDo}",
                        arguments = listOf(
                            navArgument(name = "syncToDo") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) {

                        ToDoListScreen(onNavigate = {
                            navController.navigate(it.route)
                        },
                        )
                    }
                    composable(
                        route = Routes.ADD_EDIT_TODO + "?todoId={todoId}",
                        arguments = listOf(
                            navArgument(name = "todoId") {
                                type = NavType.LongType
                                defaultValue = -1
                            }
                        )
                    ) {

                        AddEditToDoScreen(onNavigate = {
                            navController.navigate(it) {
                                popUpTo(route = Routes.TODO_LIST + "?syncToDo={syncToDo}") {
                                    inclusive = true
                                }

                            }
                        }
                        )
                    }
                }

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}




