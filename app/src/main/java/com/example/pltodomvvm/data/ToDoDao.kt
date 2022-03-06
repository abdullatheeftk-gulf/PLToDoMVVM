package com.example.pltodomvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface ToDoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(toDo: ToDo):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllToDos(toDos:List<ToDo>)

    @Delete
    suspend fun deleteToDo(toDo: ToDo)

    @Query("SELECT * FROM ToDo WHERE openDate = :date")
    suspend fun getToDoById(date:Date):ToDo?

    @Query("SELECT * FROM ToDo ORDER BY  isDone ASC,openDate DESC")
    fun getTodos():Flow<List<ToDo>>
}