package app.stacq.plan.ui.editTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import kotlinx.coroutines.launch
import java.time.Instant

class EditTaskViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository,
    taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun edit(name: String, categoryId: String) {
        viewModelScope.launch {
            val task = task.value
            task?.let {
                if (task.categoryId == categoryId) {
                    task.name = name
                    taskRepository.update(task)
                } else {
                    // update category
                    val previousCategoryId = task.categoryId
                    task.categoryId = categoryId
                    taskRepository.updateCategory(task, previousCategoryId)
                }
            }
        }
    }

    @JvmOverloads
    fun updateCompletion(completed: Boolean, completedAt: Long = Instant.now().epochSecond) {
        val task = task.value
        task?.let {
            // Avoids infinite loop
            if (it.completed != completed) {
                task.completed = completed
                task.completedAt = completedAt
                viewModelScope.launch {
                    taskRepository.updateCompletion(task)
                }
            }
        }
    }
}