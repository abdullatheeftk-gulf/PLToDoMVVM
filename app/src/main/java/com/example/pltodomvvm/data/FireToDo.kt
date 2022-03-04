package com.example.pltodomvvm.data




data class FireToDo(
    val openDate: Long,
    val closeDate: Long?=null,
    val title: String,
    val description: String?,
    @field:JvmField
    val isDone:Boolean,
    @field:JvmField
    var isSyncFinished:Boolean = true,
    val id:Int

)