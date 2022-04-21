package work.racka.reluct.common.features.tasks.task_details.container

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import work.racka.reluct.common.model.domain.tasks.Task
import work.racka.reluct.common.model.states.tasks.TasksSideEffect
import work.racka.reluct.common.model.states.tasks.TasksState

internal interface TaskDetails {
    val uiState: StateFlow<TasksState>
    val events: Flow<TasksSideEffect>
    fun deleteTask(taskId: String)
    fun toggleDone(task: Task, isDone: Boolean)
    fun editTask(taskId: String)
    fun goBack()
}