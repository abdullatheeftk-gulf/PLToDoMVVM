package com.example.pltodomvvm.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ToDo(
    val openDate:Date,
    val closeDate: Date?=null,
    val title: String,
    val description: String?,
    @field:JvmField
    val isDone:Boolean,
    @field:JvmField
    var isSyncFinished:Boolean = true,
    @PrimaryKey
    val id:Int?= null

    )
