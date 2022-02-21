package com.example.pltodomvvm.data


import com.example.pltodomvvm.util.FireStoreInsertState
import com.example.pltodomvvm.util.FirebaseAuthState
import kotlinx.coroutines.flow.Flow

interface ToDoRepository {
    suspend fun insertToDo(toDo: ToDo,callBack:suspend (itemId:Long)->Unit)

    suspend fun insertIntoFireStore(toDo: ToDo,callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit)

    suspend fun deleteToDo(toDo: ToDo)

    suspend fun getToDoById(id:Int):ToDo?

    fun getTodos(): Flow<List<ToDo>>


    //firebase auth

    fun createUserWithEmailAndPassword(email:String,password:String,callBack:(authState: FirebaseAuthState)->Unit)

    fun signInWithEmailAndPassword(email:String,password:String,callBack:(authState:FirebaseAuthState)->Unit)
}