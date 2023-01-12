package app.stacq.plan.ui.createTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.asTask
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: LiveData<List<Category>> = categoryRepository.getEnabledCategories()

    fun create(name: String, categoryId: String) {
        viewModelScope.launch {
            val taskEntity = TaskEntity(name = name, categoryId = categoryId)
            val task = taskEntity.asTask()
            taskRepository.create(task)
        }
    }
}