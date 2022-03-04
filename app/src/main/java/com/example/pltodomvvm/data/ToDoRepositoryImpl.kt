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
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

private const val TAG = "ToDoRepositoryImpl"

class ToDoRepositoryImpl(
    private val toDoDao: ToDoDao,
    private val auth: FirebaseAuth,
    private val fdb: FirebaseFirestore,
    private val context: Context
) : ToDoRepository {


    override suspend fun insertToDo(
        toDo: ToDo,
        callBack: suspend (id: Long) -> Unit
    ) {
        val id = toDoDao.insertToDo(toDo = toDo)
        callBack(id)

    }

    override suspend fun insertToDoFireStore(
        syncToDo: FireToDo,
        callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit
    ) {
        callBack(FireStoreInsertState.OnProgress)
        auth.currentUser?.let { firebaseUser ->
            fdb.collection(firebaseUser.email!!)
                .document(syncToDo.openDate.toString())
                .set(syncToDo)
                .addOnSuccessListener {
                    callBack(FireStoreInsertState.OnSuccess(syncToDo))
                }
                .addOnFailureListener { e ->
                    callBack(FireStoreInsertState.OnFailure(e))
                }
        }
    }

    override suspend fun getItemExistsInFDB(queryToDo: ToDo, callBack: (status: Boolean) -> Unit) {
        auth.currentUser?.let {
            val documentRef = fdb.collection(it.email!!).document(queryToDo.id.toString())
            documentRef.get()
                .addOnSuccessListener { ds ->
                    if (ds != null) {
                        callBack(true)
                    } else {
                        callBack(false)
                    }
                }.addOnFailureListener {
                }
        }
    }


    override suspend fun deleteToDo(deleteToDo: ToDo,callBack: suspend () -> Unit) {
        toDoDao.deleteToDo(toDo = deleteToDo)
        callBack()
    }

    override suspend fun deleteFromFireStore(
        deleteFireToDo: FireToDo,
        callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit
    ) {

    }



    override suspend fun getToDoById(id: Int): ToDo? {
        return toDoDao.getToDoById(id = id)
    }

    override fun getTodos(): Flow<List<ToDo>> {
        return toDoDao.getTodos()
    }

    override fun getAllToDoesFromFireStore(callBack: (listOfToDo: List<ToDo>) -> Unit) {
        auth.currentUser?.let { firebaseUser ->
            val toDos = mutableListOf<ToDo>()
            fdb.collection(firebaseUser.email!!)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.forEach { documentSnapShot ->
                        val title = documentSnapShot.data?.get("title").toString()
                        val description = documentSnapShot.data?.get("description").toString()
                        val isDone = documentSnapShot.data?.get("isDone").toString().toBoolean()
                        val isSyncFinished =
                            documentSnapShot.data?.get("isSyncFinished").toString().toBoolean()
                        val id = documentSnapShot.data?.get("id").toString().toInt()
                        val openDate = documentSnapShot.data?.get("openDate").toString().toLong()
                        val todo = ToDo(
                            title = title,
                            description = description,
                            isDone = isDone,
                            isSyncFinished = isSyncFinished,
                            id = id,
                            openDate = Converters().fromTimestamp(openDate)!!
                        )
                        toDos.add(todo)
                    }
                    callBack(toDos)
                }
                .addOnFailureListener {
                }
        }
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
                }
            }
            .addOnFailureListener {
                callBack(FirebaseAuthState.OnAuthFailure(it))
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
                }
            }
            .addOnFailureListener {
                callBack(FirebaseAuthState.OnAuthFailure(it))
            }
    }
}