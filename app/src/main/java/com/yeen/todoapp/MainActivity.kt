package com.yeen.todoapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yeen.todoapp.data.TodoData
import com.yeen.todoapp.ui.theme.TodoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TopLevel()
                }
            }
        }
    }
}

@Composable
fun Todo(
    todoData: TodoData,
    onEdit: (key: Int, text: String) -> Unit = {_, _ ->},
    onToggle: (key: Int, checked: Boolean) -> Unit = {_, _ ->},
    onDelete: (key: Int) -> Unit = {}
) {

    var isEditing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(4.dp)
    ) {
        
        Crossfade(targetState = isEditing, label = "") {
            when(it) {
                false -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = todoData.text,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = "완료")
                        Checkbox(
                            checked = todoData.done,
                            onCheckedChange = { checked ->
                                onToggle(todoData.key, checked)
                            }
                        )
                        Button(onClick = {
                            isEditing = true
                        }) {
                            Text(text = "수정")
                        }
                        Spacer(Modifier.size(4.dp))
                        Button(onClick = {
                            onDelete(todoData.key)
                        }) {
                            Text(text = "삭제")
                        }
                    }
                }

                true -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        var (newText, setNewText) =  remember { mutableStateOf(todoData.text) }

                        OutlinedTextField(
                            value = newText,
                            onValueChange = setNewText,
                            Modifier.weight(1f)
                        )
                        Spacer(Modifier.size(4.dp))
                        Button(onClick = {
                            onEdit(todoData.key, newText)
                            isEditing = false
                        }) {
                            Text("완료")
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun TopLevel() {
    val (text, setText) = remember { mutableStateOf("") }
    val todoList = remember { mutableStateListOf<TodoData>() }
    //MutableStateList 추가, 삭제, 변경 -> UI 갱신
    //항목 하나의 값을 바꾸는 것보다 항목 자체를 바꾸는게 더 효율적

    val onSubmit : (String) -> Unit = {text ->
        val key = (todoList.lastOrNull()?.key ?: 0) + 1
        todoList.add(TodoData(key = key, text))
        setText("")
    }

    val onToggle: (Int, Boolean) -> Unit = { key, checked ->
        val i = todoList.indexOfFirst { it.key == key }
        todoList[i] = todoList[i].copy(done = checked)
    }

    val onDelete : (Int) -> Unit = { key ->
        val i = todoList.indexOfFirst { it.key == key }
        todoList.removeAt(i)
    }

    val onEdit: (Int, String) -> Unit = { key, text ->
        val i = todoList.indexOfFirst { it.key == key }
        todoList[i] = todoList[i].copy(text = text)
    }


    Column {
        TodoInput(
            text = text,
            onTextChanged = setText,
            onSubmit = onSubmit
        )

        LazyColumn {
            //렌더링을 위한 key 설정
            items(todoList, key = {it.key}) { todoData ->
                Todo(
                    todoData = todoData,
                    onEdit = onEdit,
                    onToggle = onToggle,
                    onDelete = onDelete)
            }
        }
    }


}

@Composable
fun TodoInput(
    text: String,
    onTextChanged: (String) -> Unit,
    onSubmit: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            value = text, onValueChange = onTextChanged,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(8.dp))
        Button(onClick = {
            onSubmit(text)
        }) {
            Text(text = "입력")
        }
    }


}


@Preview(showBackground = true)
@Composable
fun TodoPreview() {
    TodoAppTheme {
        TopLevel()
    }
}