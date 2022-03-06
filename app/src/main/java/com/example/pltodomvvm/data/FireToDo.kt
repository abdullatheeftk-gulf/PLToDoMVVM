package com.example.pltodomvvm.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.*


data class FireToDo(
    @ServerTimestamp
    val openDate: Date,

    @ServerTimestamp
    val closeDate: Date?=null,

    val title: String,

    val description: String?,

    @field:JvmField
    val isDone:Boolean,

    @field:JvmField
    var isSyncFinished:Boolean = true,



)