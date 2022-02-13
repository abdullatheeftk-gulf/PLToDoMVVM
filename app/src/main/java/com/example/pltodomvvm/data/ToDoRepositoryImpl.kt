package com.example.pltodomvvm.data

import kotlinx.coroutines.flow.Flow

class ToDoRepositoryImpl(
    private val toDoDao: ToDoDao
):ToDoRepository {
    override suspend fun insertToDo(toDo: ToDo) {
        toDoDao.insertToDo(toDo = toDo)
    }

    override suspend fun deleteToDo(toDo: ToDo) {
        toDoDao.deleteToDo(toDo=toDo)
    }

    override suspend fun getToDo(id: Int): ToDo? {
        return toDoDao.getToDo(id=id)
    }

    override fun getTodos(): Flow<List<ToDo>> {
        return toDoDao.getTodos()
    }
}