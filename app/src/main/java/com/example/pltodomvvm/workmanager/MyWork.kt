package com.example.pltodomvvm.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.pltodomvvm.data.ToDo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class MyWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workParams: WorkerParameters,
    private val fdb:FirebaseFirestore,
    private val auth: FirebaseAuth,
    ) : CoroutineWorker(appContext, workParams) {

    private val dataBuilder = Data.Builder()

    override suspend fun doWork(): Result {
        val receivedData:String = inputData.getString("syncToDo")!!
        val mToDo = Gson().fromJson(receivedData,ToDo::class.java)
        val syncToDo = mToDo.copy(isSyncFinished = true)

        auth.currentUser?.let {
            fdb.collection(it.email!!)
                .document(syncToDo.id!!.toString())
                .set(syncToDo)
                .addOnSuccessListener {
                   dataBuilder.putString("toDo",Gson().toJson(syncToDo))


                }
                .addOnFailureListener {
                    dataBuilder.putString("toDo",Gson().toJson(mToDo))

                }

        }


        return Result.success(dataBuilder.build())
    }
}