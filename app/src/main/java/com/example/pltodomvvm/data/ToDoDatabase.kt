package com.example.pltodomvvm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ToDo::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ToDoDatabase : RoomDatabase() {
    abstract val toDoDao: ToDoDao
}