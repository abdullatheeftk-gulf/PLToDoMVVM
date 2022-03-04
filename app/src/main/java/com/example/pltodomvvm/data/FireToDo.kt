package com.example.pltodomvvm.data




data class FireToDo(
    val openDate: String,
    val closeDate: String?=null,
    val title: String,
    val description: String?,
    @field:JvmField
    val isDone:Boolean,
    @field:JvmField
    var isSyncFinished:Boolean = true,
    val id:Int

)