package work.racka.reluct.android.screens.tasks.pending

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import work.racka.reluct.android.compose.components.buttons.ReluctFloatingActionButton
import work.racka.reluct.android.compose.components.cards.headers.TaskGroupHeadingHeader
import work.racka.reluct.android.compose.components.cards.task_entry.EntryType
import work.racka.reluct.android.compose.components.cards.task_entry.TaskEntry
import work.racka.reluct.android.compose.components.images.LottieAnimationWithDescription
import work.racka.reluct.android.compose.components.util.rememberScrollContext
import work.racka.reluct.android.compose.theme.Dimens
import work.racka.reluct.android.screens.R
import work.racka.reluct.common.model.domain.tasks.Task
import work.racka.reluct.common.model.states.tasks.PendingTasksState

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class
)
@Composable
internal fun PendingTasksUI(
    modifier: Modifier = Modifier,
    mainScaffoldPadding: PaddingValues,
    scaffoldState: ScaffoldState,
    uiState: PendingTasksState,
    onTaskClicked: (task: Task) -> Unit,
    onAddTaskClicked: (task: Task?) -> Unit,
    onToggleTaskDone: (isDone: Boolean, task: Task) -> Unit,
    fetchMoreData: () -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollContext = rememberScrollContext(listState = listState)

    val buttonExpanded = listState.firstVisibleItemIndex <= 0

    if (scrollContext.isBottom && uiState.shouldUpdateData
        && uiState !is PendingTasksState.Loading
    ) {
        fetchMoreData()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { data ->
                Snackbar(
                    snackbarData = data,
                    backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                )
            }
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ReluctFloatingActionButton(
                modifier = Modifier
                    .padding(bottom = mainScaffoldPadding.calculateBottomPadding()),
                buttonText = stringResource(R.string.new_task_button_text),
                contentDescription = stringResource(R.string.add_icon),
                icon = Icons.Rounded.Add,
                onButtonClicked = {
                    onAddTaskClicked(null)
                },
                expanded = buttonExpanded
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = mainScaffoldPadding.calculateBottomPadding())
                .padding(horizontal = Dimens.MediumPadding.size)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                modifier = Modifier
                    .fillMaxSize(),
                visible = uiState is PendingTasksState.Loading &&
                        uiState.tasksData.isEmpty() && uiState.overdueTasksData.isEmpty(),
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Show Empty Graphic
            if (uiState.overdueTasksData.isEmpty() && uiState.tasksData.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimationWithDescription(
                        lottieResId = R.raw.no_task_animation,
                        imageSize = 200.dp,
                        description = stringResource(R.string.no_tasks_text)
                    )
                }
            } else { // Show Pending Tasks
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement
                        .spacedBy(Dimens.SmallPadding.size)
                ) {

                    if (uiState.overdueTasksData.isNotEmpty()) {
                        stickyHeader {
                            TaskGroupHeadingHeader(text = stringResource(R.string.overdue_tasks_header))
                        }
                        items(uiState.overdueTasksData) { item ->
                            TaskEntry(
                                task = item,
                                entryType = EntryType.TasksWithOverdue,
                                onEntryClick = { onTaskClicked(item) },
                                onCheckedChange = { onToggleTaskDone(it, item) }
                            )
                        }
                    }

                    uiState.tasksData.forEach { taskGroup ->
                        stickyHeader {
                            TaskGroupHeadingHeader(text = taskGroup.key)
                        }
                        items(taskGroup.value) { item ->
                            TaskEntry(
                                task = item,
                                entryType = EntryType.PendingTask,
                                onEntryClick = { onTaskClicked(item) },
                                onCheckedChange = { onToggleTaskDone(it, item) }
                            )
                        }
                    }

                    // Loading when fetching more data
                    item {
                        if (uiState is PendingTasksState.Loading &&
                            uiState.tasksData.isNotEmpty() && uiState.overdueTasksData.isNotEmpty()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                LinearProgressIndicator()
                            }
                        }
                    }

                    // Bottom Space for spaceBy
                    // Needed so that the load more indicator is shown
                    item {
                        Spacer(modifier = Modifier)
                    }
                }
            }
        }
    }
}