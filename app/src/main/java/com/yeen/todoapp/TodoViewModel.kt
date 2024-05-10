package com.yeen.todoapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yeen.todoapp.data.TodoData

class TodoViewModel : ViewModel() {

    private val _rawtodoList = mutableListOf<TodoData>()
    private val _todoList = MutableLiveData<List<TodoData>>(_rawtodoList)
    val todoList : LiveData<List<TodoData>> = _todoList

    private val _text = MutableLiveData("")
    val text : LiveData<String> = _text

    //    val text: LiveData<String> = _text
    val setText: (String) -> Unit = {
        _text.value = it
    }

    val onSubmit: (String) -> Unit = {
        val key = (_rawtodoList.lastOrNull()?.key ?: 0) + 1
        _rawtodoList.add(TodoData(key = key, it))
        _todoList.value = mutableListOf<TodoData>().also {
            it.addAll(_rawtodoList)
        }
        _text.value = ""
    }

    val onToggle: (Int, Boolean) -> Unit = { key, checked ->
        val i = _rawtodoList.indexOfFirst { it.key == key }
        _rawtodoList[i] = _rawtodoList[i].copy(done = checked)
        _todoList.value = mutableListOf<TodoData>().also {
            it.addAll(_rawtodoList)
        }
    }

    val onDelete: (Int) -> Unit = { key ->
        val i = _rawtodoList.indexOfFirst { it.key == key }
        _rawtodoList.removeAt(i)
        _todoList.value = mutableListOf<TodoData>().also {
            it.addAll(_rawtodoList)
        }
    }

    val onEdit: (Int, String) -> Unit = { key, newText ->
        val i = _rawtodoList.indexOfFirst { it.key == key }
        _rawtodoList[i] = _rawtodoList[i].copy(text = newText)
        _todoList.value = mutableListOf<TodoData>().also {
            it.addAll(_rawtodoList)
        }
    }

}