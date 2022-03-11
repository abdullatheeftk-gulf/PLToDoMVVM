package com.example.pltodomvvm.data


import com.example.pltodomvvm.util.FireStoreInsertState
import com.example.pltodomvvm.util.FirebaseAuthState
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ToDoRepository {
    suspend fun insertToDo(toDo: ToDo,callBack: suspend (id:Long)->Unit)

    suspend fun insertAllToDos(toDos:List<ToDo>)

    suspend fun insertToDoFireStore(syncToDo: FireToDo,callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit)

    suspend fun deleteToDo(deleteToDo: ToDo,callBack: suspend () -> Unit)

    suspend fun deleteFromFireStore(deleteFireToDo:FireToDo,callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit)

    suspend fun getToDoByDate(date:Date):ToDo?

    suspend fun deleteAllToDos(callBack:suspend ()->Unit)

    fun deleteAllFromFireStore()


    fun searchForToDos(text:String):Flow<List<ToDo>>



    suspend fun incrementCounter()

    fun getOperationCounterFlow():Flow<Int>



    fun getTodos(): Flow<List<ToDo>>


    fun getAllToDoesFromFireStore(callBack:(listOfToDo:List<ToDo>)->Unit)


    //firebase auth

    fun createUserWithEmailAndPassword(email:String,password:String,callBack:(authState: FirebaseAuthState)->Unit)

    fun signInWithEmailAndPassword(email:String,password:String,callBack:(authState:FirebaseAuthState)->Unit)
}