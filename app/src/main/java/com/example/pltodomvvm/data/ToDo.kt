package com.example.pltodomvvm.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ToDo(
    val title: String,
    val description: String?,
    val isDone:Boolean,
    var isSyncFinished:Boolean = false,
    @PrimaryKey
    val id:Int?= null

    )
