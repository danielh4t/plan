package app.stacq.plan.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Category
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
        emitSource(tasksRepository.getTaskCategoryById(taskId))
    }

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    fun editTask(id: String, title: String, categoryId: Int) {
        viewModelScope.launch {
            tasksRepository.updateTaskTitleAndCategoryById(id, title, categoryId)
        }
    }

}