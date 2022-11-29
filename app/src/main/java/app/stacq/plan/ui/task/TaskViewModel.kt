package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import kotlinx.coroutines.launch


class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<Task> = liveData {
        emitSource(taskRepository.getTaskCategoryById(taskId))
    }

    fun clone() {
        val name = task.value?.name
        val categoryId = task.value?.categoryId
        viewModelScope.launch {
            if (name != null && categoryId != null)
                taskRepository.create(TaskEntity(name = name, categoryId = categoryId))
        }
    }

    fun delete() {
        viewModelScope.launch {
            task.value?.let { taskRepository.deleteById(it.id) }
        }
    }

}
