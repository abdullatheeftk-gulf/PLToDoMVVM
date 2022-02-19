package com.example.pltodomvvm.data

import android.util.Log
import com.example.pltodomvvm.util.FireStoreInsertState
import com.example.pltodomvvm.util.FirebaseAuthState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "ToDoRepositoryImpl"

class ToDoRepositoryImpl(
    private val toDoDao: ToDoDao,
    private val auth: FirebaseAuth,
    private val fdb:FirebaseFirestore
) : ToDoRepository {
    override suspend fun insertToDo(toDo: ToDo,callBack:(fireStoreInsertState:FireStoreInsertState)->Unit) {
        Log.i(TAG, "insertToDo: ${auth.currentUser?.email}")
        auth.currentUser?.let {
            insertToDoToRoom(toDo = toDo){
                val data = toDo.copy(id=it.toInt())
                fdb.collection("ToDo").document(it.toString())
                    .set(data)
                    .addOnSuccessListener {
                        callBack(FireStoreInsertState.OnSuccess(true))
                    }
                    .addOnFailureListener {exception->
                        callBack(FireStoreInsertState.OnFailure(exception = exception))
                    }
            }
        }

    }

   private suspend fun insertToDoToRoom(toDo: ToDo,callBack:(id:Long)->Unit){
       val id =  toDoDao.insertToDo(toDo = toDo)
       callBack(id)

    }
    override suspend fun deleteToDo(toDo: ToDo) {
        deleteToDoFromRoom(toDo = toDo){
            Log.i(TAG, "deleteToDo: $it")
           // fdb.
        }
    }
    
    private suspend fun deleteToDoFromRoom(toDo: ToDo,callBack: (id: Int) -> Unit){
        val id = toDo.id!!
        Log.i(TAG, "deleteToDoFromRoom: $id")
        toDoDao.deleteToDo(toDo = toDo)
        callBack(id)
        
    }

    override suspend fun getToDoById(id: Int): ToDo? {
        return toDoDao.getToDoById(id = id)
    }

    override fun getTodos(): Flow<List<ToDo>> {
        return toDoDao.getTodos()
    }

    override fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        callBack: (authState: FirebaseAuthState) -> Unit
    ) {
        callBack(FirebaseAuthState.OnAuthLoading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callBack(FirebaseAuthState.OnAuthSuccess(task.result))
                    Log.d(TAG, "createUserWithEmailAndPassword: ${task.result.user?.email} ")
                }

            }
            .addOnFailureListener {

                callBack(FirebaseAuthState.OnAuthFailure(it))
                Log.e(TAG, "createUserWithEmailAndPassword: ${it}")
            }

    }

    override fun signInWithEmailAndPassword(
        email: String,
        password: String,
        callBack: (authState: FirebaseAuthState) -> Unit
    ) {
        callBack(FirebaseAuthState.OnAuthLoading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callBack(FirebaseAuthState.OnAuthSuccess(task.result))
                    Log.d(TAG, "signInUserWithEmailAndPassword: ${task.result.user?.email} ")
                }

            }
            .addOnFailureListener {

                callBack(FirebaseAuthState.OnAuthFailure(it))
                Log.e(TAG, "signInUserWithEmailAndPassword: ${it.message}")
            }
    }
}