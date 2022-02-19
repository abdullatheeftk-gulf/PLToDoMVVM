package com.example.pltodomvvm.di

import android.app.Application
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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideToDoRepository(toDoDatabase: ToDoDatabase,auth:FirebaseAuth):ToDoRepository =
        ToDoRepositoryImpl(toDoDao = toDoDatabase.toDoDao, auth = auth)

    @Provides
    @Singleton
    fun provideFirebaseAuth():FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFireStore():FirebaseFirestore = Firebase.firestore

}