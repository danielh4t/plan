package app.stacq.plan.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch

class EditViewModel(
    private val tasksRepository: TasksRepository,
    private val categoryRepository: CategoryRepository,
    private val taskId: String
) : ViewModel() {

    val task: LiveData<TaskCategory> = liveData {
        emitSource(tasksRepository.readTaskCategoryById(taskId))
    }

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    fun editTask(title: String, categoryId: Int) {
        viewModelScope.launch {
            task.value?.let { it ->
                val updateTask =
                    Task(taskId, it.createdAt, title, categoryId, it.completed, it.completedAt)
                tasksRepository.updateTask(updateTask)
            }

        }
    }

}