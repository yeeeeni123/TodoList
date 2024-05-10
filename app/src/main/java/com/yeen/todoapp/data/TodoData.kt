package com.yeen.todoapp.data

data class TodoData(
    val key: Int,
    val text: String,
    val done: Boolean = false
)

//var 가아닌 val 인이유 -> 객체의 필드 변경 -> ui업데이트가 이루어지지 않음
