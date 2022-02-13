package com.example.pltodomvvm.di

import android.app.Application
import androidx.room.Room
import com.example.pltodomvvm.data.ToDoDatabase
import com.example.pltodomvvm.data.ToDoRepository
import com.example.pltodomvvm.data.ToDoRepositoryImpl
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
    fun provideToDoRepository(toDoDatabase: ToDoDatabase):ToDoRepository =
        ToDoRepositoryImpl(toDoDao = toDoDatabase.toDoDao)

}