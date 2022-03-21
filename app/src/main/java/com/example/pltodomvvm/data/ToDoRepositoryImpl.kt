package com.example.pltodomvvm.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.pltodomvvm.util.Constants.OPERATION_COUNTER
import com.example.pltodomvvm.util.FireStoreInsertState
import com.example.pltodomvvm.util.FirebaseAuthState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.*

private const val TAG = "ToDoRepositoryImpl"

class ToDoRepositoryImpl(
    private val toDoDao: ToDoDao,
    private val auth: FirebaseAuth,
    private val fdb: FirebaseFirestore,
    private val dataStore: DataStore<Preferences>,
) : ToDoRepository {


    override suspend fun insertToDo(
        toDo: ToDo,
        callBack: suspend (id: Long) -> Unit
    ) {
        val id = toDoDao.insertToDo(toDo = toDo)
        callBack(id)

    }

    override suspend fun insertAllToDos(toDos: List<ToDo>) {
        toDoDao.insertAllToDos(toDos = toDos)
    }


    override suspend fun insertToDoFireStore(
        syncToDo: FireToDo,
        isSubscribed: Boolean,
        callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit
    ) {
        callBack(FireStoreInsertState.OnProgress)
        if (isSubscribed) {
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

    }


    override suspend fun deleteToDo(
        deleteToDo: ToDo,
        callBack: suspend () -> Unit
    ) {
        toDoDao.deleteToDo(toDo = deleteToDo)
        callBack()
    }

    override suspend fun deleteFromFireStore(
        deleteFireToDo: FireToDo,
        isSubscribed: Boolean,
        callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit
    ) {
        if (isSubscribed) {
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
    }


    override suspend fun getToDoByDate(date: Date): ToDo? {
        return toDoDao.getToDoByDate(date = date)
    }

    override suspend fun deleteAllToDos(callBack: suspend () -> Unit) {
        toDoDao.deleteAll()
        callBack()
    }

    override fun deleteAllFromFireStore(isSubscribed: Boolean) {
        if (isSubscribed) {
            auth.currentUser?.let { fsu ->
                fdb.collection(fsu.email!!).get().addOnSuccessListener { querySnapShot ->
                    querySnapShot.documents.forEach { documentSnapshot ->
                        val id = documentSnapshot.id
                        try {
                            fdb.collection(fsu.email!!).document(id).delete()
                        } catch (e: Exception) {
                            Log.e(TAG, "deleteAllFromFireStore: ${e.message}")
                        }

                    }
                }
            }
        }
    }

    override fun searchForToDos(text: String): Flow<List<ToDo>> {
        return toDoDao.searchForToDos(text = text)
    }

    override suspend fun incrementCounter() {
        dataStore.edit { settings ->
            val currentCounterValue = settings[OPERATION_COUNTER] ?: 0
            settings[OPERATION_COUNTER] = currentCounterValue + 1
        }
    }

    override suspend fun resetCounter() {
        dataStore.edit { settings ->
            settings[OPERATION_COUNTER] = 1
        }
    }

    override fun getOperationCounterFlow(): Flow<Int> {
        val operationCounterFlow: Flow<Int> = dataStore.data
            .map { preferences ->
                preferences[OPERATION_COUNTER] ?: 0
            }
        return operationCounterFlow
    }

    override fun getTodos(): Flow<List<ToDo>> {
        return toDoDao.getTodos()
    }

    override fun getAllToDoesFromFireStore(
        isSubscribed: Boolean,
        callBack: (listOfToDo: List<ToDo>) -> Unit
    ) {
        if (isSubscribed) {
            auth.currentUser?.let { fsu ->
                val toDos = mutableListOf<ToDo>()
                fdb.collection(fsu.email!!)
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

                                val openDate = openTimeStamp.toDate()
                                var closeDate: Date? = closeTimestamp.toDate()

                                if (!isDone) {
                                    closeDate = null
                                }

                                val toDo = ToDo(
                                    title = title,
                                    description = description,
                                    isDone = isDone,
                                    isSyncFinished = isSyncFinished,
                                    openDate = openDate,
                                    closeDate = closeDate
                                )
                                toDos.add(toDo)
                            } catch (e: Exception) {
                            }
                        }
                        callBack(reOrderToDoList(toDos.asReversed()))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "getAllToDoesFromFireStore: ${e.message}")
                    }

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

    override fun signOutFromFireStore(callBack: () -> Unit) {
        auth.currentUser?.let {
            auth.signOut()
        }

        callBack()
    }

    override suspend fun subscribedInsertFireStore(
        isSubscribed: Boolean,
        fireToDo: FireToDo,
        callBack: (fireStoreInsertState: FireStoreInsertState) -> Unit
    ) {
        if (isSubscribed) {
            auth.currentUser?.let { fsu ->
                fdb.collection(fsu.email!!)
                    .document(fireToDo.openDate.toString())
                    .set(fireToDo)
                    .addOnSuccessListener {
                        callBack(FireStoreInsertState.OnSuccess(inToDo = fireToDo))
                    }
                    .addOnFailureListener {
                        callBack(FireStoreInsertState.OnFailure(it))
                    }
            }
        }
    }


    private fun reOrderToDoList(list: List<ToDo>): List<ToDo> {
        val reListWithIsDone = mutableListOf<ToDo>()
        val reListWithoutIsDone = mutableListOf<ToDo>()
        val reArrangedList = mutableListOf<ToDo>()

        list.forEach { toDo ->
            if (toDo.isDone) {
                reListWithIsDone.add(toDo)
            } else {
                reListWithoutIsDone.add(toDo)
            }
        }
        reListWithoutIsDone.forEach {
            reArrangedList.add(it)
        }
        reListWithIsDone.forEach {
            reArrangedList.add(it)
        }

        return reArrangedList
    }
}