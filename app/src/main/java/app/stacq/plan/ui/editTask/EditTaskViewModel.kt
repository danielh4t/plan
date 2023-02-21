package app.stacq.plan.ui.editTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import kotlinx.coroutines.launch
import java.time.Instant

class EditTaskViewModel(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    categoryRepositoryImpl: CategoryRepositoryImpl,
    taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepositoryImpl.getTask(taskId)

    val categories: LiveData<List<Category>> = categoryRepositoryImpl.getEnabledCategories()

    fun edit(name: String, categoryId: String) {
        viewModelScope.launch {
            val task = task.value
            task?.let {
                if (task.categoryId == categoryId) {
                    task.name = name
                    taskRepositoryImpl.update(task)
                } else {
                    // update category
                    val previousCategoryId = task.categoryId
                    task.categoryId = categoryId
                    taskRepositoryImpl.updateCategory(task, previousCategoryId)
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
                    taskRepositoryImpl.updateCompletion(task)
                }
            }
        }
    }
}