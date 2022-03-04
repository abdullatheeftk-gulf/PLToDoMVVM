package com.example.pltodomvvm.data

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.pltodomvvm.util.FireStoreInsertState
import com.example.pltodomvvm.util.FirebaseAuthState
import com.example.pltodomvvm.workmanager.MyWork
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*
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


    override suspend fun deleteToDo(deleteToDo: ToDo, callBack: suspend () -> Unit) {
        toDoDao.deleteToDo(toDo = deleteToDo)
        callBack()
    }

    override suspend fun deleteFromFireStore(
        deleteFireToDo: FireToDo,
        callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit
    ) {
        auth.currentUser?.let { fsu ->
            fdb.collection(fsu.email!!)
                .document(deleteFireToDo.openDate.toString())
                .delete()
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                }
        }
    }


    override suspend fun getToDoById(id: Int): ToDo? {
        return toDoDao.getToDoById(id = id)
    }

    override fun getTodos(): Flow<List<ToDo>> {
        return toDoDao.getTodos()
    }

    override fun getAllToDoesFromFireStore(callBack: (listOfToDo: List<ToDo>) -> Unit) {
        auth.currentUser?.let { fsu ->
            val toDos = mutableListOf<ToDo>()
            fdb.collection(fsu.email!!)
                .orderBy("isDone",Query.Direction.ASCENDING)
                .orderBy("id",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapShot ->
                    querySnapShot.documents.forEach { documentSnapshot ->
                        val fireMap = documentSnapshot.data
                        try {
                            val openTimeStamp = fireMap?.get("openDate") as Timestamp
                            val closeTimestamp = fireMap["closeDate"] as Timestamp
                            val title = fireMap["title"].toString()
                            val description = fireMap["description"].toString()
                            val isDone = fireMap["isDone"] as Boolean
                            val isSyncFinished = fireMap["isSyncFinished"] as Boolean
                            val id = fireMap["id"].toString().toInt()

                            val openDate = openTimeStamp.toDate()
                            var closeDate: Date? = closeTimestamp.toDate()

                            if (!isDone) {
                                closeDate = null
                            }

                            val toDo = ToDo(
                                title = title,
                                description = description,
                                id = id,
                                isDone = isDone,
                                isSyncFinished = isSyncFinished,
                                openDate = openDate,
                                closeDate = closeDate
                            )
                            toDos.add(toDo)
                        } catch (e: Exception) {
                        }
                    }

                    callBack(toDos)
                }
                .addOnFailureListener {e->
                    Log.e("TAG", "getAllToDoesFromFireStore: ${e.message}", )
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


    fun reOrderToDoList(list:List<ToDo>):List<ToDo>{

        return emptyList<ToDo>()
    }
}