package app.stacq.plan.ui.tasks


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.Task
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import kotlinx.coroutines.launch
import java.time.Instant

class TasksViewModel(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    categoryRepositoryImpl: CategoryRepositoryImpl
) : ViewModel() {

    val tasksCategory: LiveData<List<Task>> = taskRepositoryImpl.getTasks()

    val categories: LiveData<List<Category>> = categoryRepositoryImpl.getEnabledCategories()

    fun complete(task: Task) {
        task.completed = !task.completed
        task.completedAt = Instant.now().epochSecond
        viewModelScope.launch {
            taskRepositoryImpl.updateCompletion(task)
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            taskRepositoryImpl.delete(task)
        }
    }

}