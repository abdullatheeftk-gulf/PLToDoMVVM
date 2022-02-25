package com.example.pltodomvvm.data

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.pltodomvvm.util.FireStoreInsertState
import com.example.pltodomvvm.util.FirebaseAuthState
import com.example.pltodomvvm.workmanager.MyWork
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ToDoRepositoryImpl"

class ToDoRepositoryImpl(
    private val toDoDao: ToDoDao,
    private val auth: FirebaseAuth,
    private val fdb:FirebaseFirestore,
    private val context:Context
) : ToDoRepository {

    override suspend fun insertToDo(toDo: ToDo,callBack:suspend (itemId:Long)->Unit) {
       val rowId = toDoDao.insertToDo(toDo = toDo)
        callBack(rowId)
    }

    override suspend fun insertIntoFireStore(
        toDo: ToDo,
        callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit
    ) {
        /*val addData = Data.Builder()
            .putString("syncToDo",Gson().toJson(toDo))
            .build()
        val workManager = WorkManager.getInstance(context)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val addRequest = OneTimeWorkRequest.Builder(MyWork::class.java)
            .setConstraints(constraints)
            .setInputData(addData)
            .build()
        workManager.enqueue(addRequest)*/






      //  Log.i(TAG, "insertIntoFireStore: $context")
        auth.currentUser?.let {
            val syncToDo = toDo.copy(isSyncFinished = true)
            callBack(FireStoreInsertState.OnProgress)
            fdb.collection(it.email!!)
                .document(toDo.id.toString())
                .set(syncToDo)
                .addOnSuccessListener {
                    callBack(FireStoreInsertState.OnSuccess(inToDo = syncToDo))
                }
                .addOnFailureListener {exception->
                    callBack(FireStoreInsertState.OnFailure(exception = exception))
                }
        }
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