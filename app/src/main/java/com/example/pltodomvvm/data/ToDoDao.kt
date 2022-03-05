package com.example.pltodomvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(toDo: ToDo):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllToDos(toDos:List<ToDo>)

    @Delete
    suspend fun deleteToDo(toDo: ToDo)

    @Query("SELECT * FROM ToDo WHERE id = :id")
    suspend fun getToDoById(id:Int):ToDo?

    @Query("SELECT * FROM ToDo ORDER BY  isDone ASC,id DESC")
    fun getTodos():Flow<List<ToDo>>
}