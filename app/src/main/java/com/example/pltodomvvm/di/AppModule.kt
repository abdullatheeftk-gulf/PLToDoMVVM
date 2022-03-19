package com.example.pltodomvvm.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.pltodomvvm.data.ToDoDatabase
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.data.ToDoRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val TAG = "AppModule"
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*private val purchasesUpdatedListener =
        PurchasesUpdatedListener{billingResult, mutableList ->
            Log.i(TAG, ":$billingResult $mutableList ")
        }*/

    private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("operation_counter")

    @Provides
    @Singleton
    fun provideToDoDatabase(app:Application)=
        Room.databaseBuilder(
            app,
            ToDoDatabase::class.java,
            "toDo_db"
        ).build()

    @Provides
    @Singleton
    fun provideToDoRepository(toDoDatabase: ToDoDatabase,auth:FirebaseAuth,db:FirebaseFirestore,dataStore: DataStore<Preferences>):ToDoRepository =
        ToDoRepositoryImpl(toDoDao = toDoDatabase.toDoDao, auth = auth, fdb = db,dataStore = dataStore )

    @Provides
    @Singleton
    fun provideFirebaseAuth():FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFireStore():FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideDataStore(app:Application) = app.dataStore


    /*@Provides
    @Singleton
    fun provideBillingClient(app:Application) = BillingClient.newBuilder(app.applicationContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()*/

}