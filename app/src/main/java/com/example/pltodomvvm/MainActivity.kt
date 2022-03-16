package com.example.pltodomvvm

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

private const val TAG = "MainActivity"


@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity(),BillingProcessor.IBillingHandler {


    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var repository: ToDoRepository

    @Inject
    lateinit var fdb: FirebaseFirestore

    //@Inject
    //lateinit var billingClient: BillingClient

    private var isAuthenticated: Boolean = false

    private var isSubscribed:Boolean = false

    private lateinit var bp:BillingProcessor
    private  var purchaseInfo: PurchaseInfo?=null


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

      /*  billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.i(TAG, "onBillingServiceDisconnected: ")
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                Log.e(TAG, "onBillingSetupFinished: $p0")
            }

        })*/


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

                        ToDoListScreen(
                            onNavigate = {
                                navController.navigate(it.route) {

                                    Log.e(TAG, "onStart: ${it.route}")
                                    if (it.route == Routes.FIREBASE_LOGIN) {
                                        popUpTo(route = Routes.TODO_LIST + "?syncToDo={syncToDo}") {
                                            inclusive = true
                                        }
                                    }
                                }
                            },
                            onSubScribeClicked = {
                                subscribe()

                            },
                            isSubscribed = isSubscribed
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



    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        Log.i(TAG, "onProductPurchased: $productId")
        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
    }

    override fun onPurchaseHistoryRestored() {
        Log.i(TAG, "onPurchaseHistoryRestored: ")
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.e(TAG, "onBillingError: $errorCode ",error )
    }

    override fun onBillingInitialized() {
        bp.loadOwnedPurchasesFromGoogleAsync(object:BillingProcessor.IPurchasesResponseListener{
            override fun onPurchasesSuccess() {
                Log.w(TAG, "onPurchasesSuccess: ", )
            }

            override fun onPurchasesError() {
                Log.e(TAG, "onPurchasesError: ")
            }

        })

        purchaseInfo = bp.getSubscriptionPurchaseInfo("testsub")

        purchaseInfo?.let {
            if (it.purchaseData.autoRenewing){
                isSubscribed = true
                Log.e(TAG, "onBillingInitialized:Already subscribed ", )
                Toast.makeText(this, "Already subscribed $isSubscribed", Toast.LENGTH_SHORT).show()
            }else{
                isSubscribed = false
                Log.e(TAG, "onBillingInitialized:Not subscribed ", )
                Toast.makeText(this, "Not subscribed $isSubscribed", Toast.LENGTH_SHORT).show()
            }
        }

        if (purchaseInfo == null){
            isSubscribed=false
            Log.i(TAG, "onBillingInitialized:Expired ")
            Toast.makeText(this, "Expired", Toast.LENGTH_SHORT).show()
        }


    }

    private fun subscribe(){
        Log.i(TAG, "onStart: onSubscribe clicked")
        bp.subscribe(this,"testsub")
    }

    override fun onBackPressed() {
        bp.release()
        super.onBackPressed()
    }


}




