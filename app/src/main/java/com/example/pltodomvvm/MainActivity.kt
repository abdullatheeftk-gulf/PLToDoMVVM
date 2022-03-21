package com.example.pltodomvvm

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
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
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.thread


@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity(), BillingProcessor.IBillingHandler {


    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var repository: ToDoRepository

    @Inject
    lateinit var fdb: FirebaseFirestore


    private val sharedViewModel: SharedViewModel by viewModels()

    private var isAuthenticated: Boolean = false
   // private var isSubscribed = false


    private lateinit var bp: BillingProcessor
    private var purchaseInfo: PurchaseInfo? = null


    override fun onStart() {
        lifecycleScope.launch {
            repository.incrementCounter()

        }



        super.onStart()

        bp = BillingProcessor(
            this,
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo32Dc4mF5qrX8ovN4jGS/hfeSpVuzOei8Tvf7Lp/6VEMimUmiazbz8FTkKtBpluyU3iWZ0LUtX4QiNPAzn/GcXoKZEuHisJo1ZUXD7yqetXX/adHxXUwSFdxPzdKM7e85T7/wbxtJCoNV0KPAMd89rkLxs/UkVllq0tQnGpbqJlzESKO8CmDNB5+HLsdNzHTi5fRQzMeqBSS1EA/oME+vBo+iEcS8BC+RbgsyFWfSomH5fmiVlXpF+m5t4ToRODEgUmX0shAsKCy+slQk+4A5pvzZZg3RNQiT3VGwO8mER18hOpHEl6TwX5TC4eABbwz0V3HKgm7K3JTrQdCl2LtbwIDAQAB",
            this
        )
        bp.initialize()


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

                        SplashScreen(
                            isAuthenticated = isAuthenticated,
                            sharedViewModel = sharedViewModel
                        ) {
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
                        route = Routes.TODO_LIST + "?syncToDo={syncToDo}&isSubscribed={status}",
                        arguments = listOf(
                            navArgument(name = "syncToDo") {
                                type = NavType.StringType
                                defaultValue = ""
                            },
                            navArgument(name = "status"){
                                type = NavType.BoolType
                                defaultValue = false
                            }
                        )
                    ) {


                        ToDoListScreen(
                            onNavigate = {
                                navController.navigate(it.route) {

                                    if (it.route == Routes.FIREBASE_LOGIN) {
                                        popUpTo(route = Routes.TODO_LIST + "?syncToDo={syncToDo}&isSubscribed={status}") {
                                            inclusive = true
                                        }
                                    }
                                }
                            },
                            onSubScribeClicked = {
                                subscribe()

                            },
                            sharedViewModel = sharedViewModel

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
                                popUpTo(route = Routes.TODO_LIST + "?syncToDo={syncToDo}&isSubscribed={status}") {
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


    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        if (details?.purchaseData?.autoRenewing!!) {
            sharedViewModel.setIsPurchased(true)
        } else {
            sharedViewModel.setIsPurchased(false)
        }
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
    }

    override fun onBillingInitialized() {
        bp.loadOwnedPurchasesFromGoogleAsync(object : BillingProcessor.IPurchasesResponseListener {
            override fun onPurchasesSuccess() {
            }

            override fun onPurchasesError() {
            }

        })

        purchaseInfo = bp.getSubscriptionPurchaseInfo("testsub")

        purchaseInfo?.let {
            if (it.purchaseData.autoRenewing) {
                sharedViewModel.setIsPurchased(true)
            } else {
                sharedViewModel.setIsPurchased(false)
            }
        }

        if (purchaseInfo == null) {
            sharedViewModel.setIsPurchased(false)
        }


    }

    private fun subscribe() {
       // bp.subscribe(this, "testsub")
        thread {
            Thread.sleep(1000)
            sharedViewModel.setIsPurchased(true)
        }
    }

    override fun onBackPressed() {
        bp.release()
        super.onBackPressed()
    }


}




