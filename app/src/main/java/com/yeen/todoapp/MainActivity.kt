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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopLevel(viewModel: TodoViewModel = viewModel()) {
    //MutableStateList 추가, 삭제, 변경 -> UI 갱신
    //항목 하나의 값을 바꾸는 것보다 항목 자체를 바꾸는게 더 효율적
    Scaffold {
        Column {
            TodoInput(
                text = viewModel.text.observeAsState("").value,
                onTextChanged = viewModel.setText,
                onSubmit = viewModel.onSubmit
            )
            val items = viewModel.todoList.observeAsState(emptyList()).value
            LazyColumn {
                //렌더링을 위한 key 설정
                items(
                    items = items,
                    key = { it.key }
                ) { todoData ->
                    Todo(
                        todoData = todoData,
                        onEdit = viewModel.onEdit,
                        onToggle = viewModel.onToggle,
                        onDelete = viewModel.onDelete)
                }
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

@Composable
fun MyNav(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, "HOME",  modifier = modifier) {
        composable("HOME") {
            Column {
                Text("HOME")
                Button(onClick = {
                    navController.navigate("Office") {
                        popUpTo("HOME") {
                            inclusive = true
                        }
                    }
                }) {
                    Text("Office로 이동")
                }
                Button(onClick = {
                    navController.navigate("Playground")
                }) {
                    Text("Playground로 이동")
                }
                Button(onClick = {
                    navController.navigate("HOME") {
                        launchSingleTop = true
                    }
                }) {
                    Text("Home으로 이동")
                }

                Button(onClick = {
                    navController.navigate("Argument/fastcampus") {
                        launchSingleTop = true
                    }
                }) {
                    Text("fastcampus 아이디로 연결")
                }
            }
        }

        composable("Argument/{userId}") {
            val userId = it.arguments?.get("userId")
            Text("userId: $userId")
        }


        composable("Office") {
            Column {
                Text(text = "Office")
                Button(onClick = {
                    navController.navigate("HOME") {
                        popUpTo("HOME") {
                            inclusive = true
                        }
                    }
                }) {
                    Text("HOME으로 이동")
                }

                Button(onClick = {
                    navController.navigate("Playground") {
                        popUpTo("HOME") {
                            inclusive = true
                        }
                    }
                }) {
                    Text("Playground로 이동")
                }
            }
        }

        composable("Playground") {
            Column {
                Text(text = "Playground")
                Button(onClick = {
                    navController.navigate("HOME") {
                        popUpTo("HOME") {
                            inclusive = true
                        }
                    }
                }) {
                    Text("HOME으로 이동")
                }
                Button(onClick = {
                    navController.navigate("Office")
                }) {
                    Text("Office로 이동")
                }
            }
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