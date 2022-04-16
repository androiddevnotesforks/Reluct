package work.racka.reluct.common.features.tasks.add_edit_task.container

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import work.racka.reluct.common.features.tasks.add_edit_task.repository.AddEditTaskRepository
import work.racka.reluct.common.features.tasks.util.Constants
import work.racka.reluct.common.model.domain.tasks.EditTask
import work.racka.reluct.common.model.states.tasks.TasksSideEffect
import work.racka.reluct.common.model.states.tasks.TasksState

internal class AddEditTaskImpl(
    private val addEditTask: AddEditTaskRepository,
    private val taskId: String?,
    private val scope: CoroutineScope,
) : AddEditTask {
    private val _uiState: MutableStateFlow<TasksState> = MutableStateFlow(TasksState.Loading)
    private val _events: Channel<TasksSideEffect> = Channel()

    override val uiState: StateFlow<TasksState>
        get() = _uiState
    override val events: Flow<TasksSideEffect>
        get() = _events.receiveAsFlow()

    init {
        getTask()
    }

    override fun getTask() {
        scope.launch {
            when (taskId) {
                null -> {
                    _uiState.update { TasksState.EmptyAddEditTask }
                }
                else -> addEditTask.getTaskToEdit(taskId).collectLatest { task ->
                    when (task) {
                        null -> _uiState.update { TasksState.EmptyAddEditTask }
                        else -> _uiState.update { TasksState.AddEditTask(task) }
                    }
                }
            }
        }
    }

    override fun saveTask(task: EditTask) {
        scope.launch {
            addEditTask.addTask(task)
            _events.send(TasksSideEffect.ShowSnackbar(Constants.TASK_SAVED))
        }
    }

    override fun goBack() {
        _events.trySend(TasksSideEffect.Navigation.GoBack)
    }
}