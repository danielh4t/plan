package app.stacq.plan.ui.editTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.model.Category
import app.stacq.plan.data.source.model.Task
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import kotlinx.coroutines.launch

class EditTaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    fun editTask(task: Task, name: String, categoryId: String) {
        viewModelScope.launch {
            task.name = name
            task.categoryId = categoryId
            taskRepository.update(task)
        }
    }

}