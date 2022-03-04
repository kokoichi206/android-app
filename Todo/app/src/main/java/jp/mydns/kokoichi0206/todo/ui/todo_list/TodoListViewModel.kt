package jp.mydns.kokoichi0206.todo.ui.todo_list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.mydns.kokoichi0206.todo.data.TodoRepository
import jp.mydns.kokoichi0206.todo.ui.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository,
) : ViewModel() {

    val todos = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: TodoListEvent) {
        when (event) {
            is TodoListEvent.OnTodoClick -> {

            }
            is TodoListEvent.OnAddTodoClick -> {

            }
            is TodoListEvent.OnUndoDeleteClick -> {

            }
            is TodoListEvent.OnDeleteTodoClick -> {

            }
            is TodoListEvent.OnDoneChange -> {

            }
        }
    }
}