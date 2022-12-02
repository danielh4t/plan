package app.stacq.plan.ui.editTask

import androidx.lifecycle.*
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.local.category.toCategory
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import kotlinx.coroutines.launch

class EditTaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val categories: LiveData<List<Category>> = liveData {
        emitSource(
            categoryRepository.getCategories()
                .map { categoryEntities -> categoryEntities.map { categoryEntity -> categoryEntity.toCategory() } })
    }

    fun editTask(task: Task, name: String, categoryId: String) {
        viewModelScope.launch {
            task.name = name
            task.categoryId = categoryId
            taskRepository.update(task)
        }
    }

}