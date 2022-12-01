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
    private val taskId: String
) : ViewModel() {

    val task: LiveData<Task> = liveData {
        emitSource(taskRepository.getTaskCategoryById(taskId))
    }

    val categories: LiveData<List<Category>> = liveData {
        emitSource(
            categoryRepository.getCategories()
                .map { categoryEntities -> categoryEntities.map { categoryEntity -> categoryEntity.toCategory() } })
    }

    fun editTask(name: String, categoryId: String) {
        viewModelScope.launch {
//            task.value?.let { it ->
//                val update = Task(taskId, it.createdAt, name, categoryId, it.completed, it.completedAt)
//                taskRepository.update(updateTaskEntity)
//            }
        }
    }

}