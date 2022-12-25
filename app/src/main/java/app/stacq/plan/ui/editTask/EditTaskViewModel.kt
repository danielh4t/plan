package app.stacq.plan.ui.editTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import kotlinx.coroutines.launch

class EditTaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    taskId: String
) : ViewModel() {

    val task: LiveData<Task> = taskRepository.getTask(taskId)

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

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
}