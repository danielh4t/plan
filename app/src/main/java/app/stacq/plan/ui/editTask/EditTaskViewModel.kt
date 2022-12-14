package app.stacq.plan.ui.editTask

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
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

    fun updateCompletion(completion: Boolean) {
        val task = task.value
        task?.let {
            // Avoids infinite loop
            if (it.completed != completion) {
                task.completed = completion
                task.completedAt = Instant.now().epochSecond
                viewModelScope.launch {
                    taskRepository.updateCompletion(task)
                }
            }
        }
    }
}