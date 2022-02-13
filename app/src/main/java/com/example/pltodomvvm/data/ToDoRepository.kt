package com.example.pltodomvvm.data


import kotlinx.coroutines.flow.Flow

interface ToDoRepository {
    suspend fun insertToDo(toDo: ToDo)

    suspend fun deleteToDo(toDo: ToDo)

    suspend fun getToDo(id:Int):ToDo?

    fun getTodos(): Flow<List<ToDo>>
}