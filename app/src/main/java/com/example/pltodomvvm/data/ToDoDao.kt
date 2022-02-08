package com.example.pltodomvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(toDo: ToDo)

    @Delete
    suspend fun deleteToDo(toDo: ToDo)

    @Query("SELECT * FROM ToDo WHERE id = :id")
    suspend fun getToDo(id:Int):ToDo?

    @Query("SELECT * FROM ToDo")
    fun getTodos():Flow<List<ToDo>>
}