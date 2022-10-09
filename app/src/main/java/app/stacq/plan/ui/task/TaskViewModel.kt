package app.stacq.plan.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch


class TaskViewModel(
    private val tasksRepository: TasksRepository,
    private val categoryRepository: CategoryRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<TaskCategory> = liveData {
        emitSource(tasksRepository.readTaskCategoryById(taskId))
    }

    fun clone() {
        val title = task.value?.title
        val category = task.value?.categoryName

        viewModelScope.launch {
            val categoryId = category?.let { categoryRepository.getCategoryIdByName(it) }
            if (title != null && categoryId != null) {
                val task = Task(title = title, categoryId = categoryId)
                tasksRepository.createTask(task)
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            task.value?.let { tasksRepository.deleteById(it.id) }
        }
    }

}
