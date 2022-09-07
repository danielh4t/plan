package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit


private const val TIMER_TIME_IN_MINUTES: Long = 90L

class TaskViewModel(
    private val tasksRepository: TasksRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<TaskCategory> = liveData {
        emitSource(tasksRepository.readTaskCategoryById(taskId))
    }

    fun completed() {
        viewModelScope.launch {
            tasksRepository.updateTaskCompletionById(taskId)
        }
    }

    fun delete() {
        viewModelScope.launch {
            task.value?.let { tasksRepository.deleteById(it.id) }
        }
    }

    fun timer(): Long {
        val instant = Instant.now().plus(TIMER_TIME_IN_MINUTES, ChronoUnit.MINUTES)
        var finishAt: Long = instant.epochSecond
        viewModelScope.launch {
            task.value?.let {
                // timer has not be set
                if (it.timerFinishAt == 0L) {
                    tasksRepository.updateTaskTimerById(taskId, finishAt)
                } else {
                    finishAt = it.timerFinishAt
                }
            }
        }
        return finishAt
    }

}