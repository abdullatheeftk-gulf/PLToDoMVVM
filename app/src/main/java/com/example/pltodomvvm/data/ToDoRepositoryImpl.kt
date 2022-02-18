package com.example.pltodomvvm.data

import android.util.Log
import com.example.pltodomvvm.util.FirebaseAuthState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

private const val TAG = "ToDoRepositoryImpl"

class ToDoRepositoryImpl(
    private val toDoDao: ToDoDao,
    private val auth: FirebaseAuth
) : ToDoRepository {
    override suspend fun insertToDo(toDo: ToDo) {
        toDoDao.insertToDo(toDo = toDo)
    }

    override suspend fun deleteToDo(toDo: ToDo) {
        toDoDao.deleteToDo(toDo = toDo)
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