package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.data.repository.task.TaskRepository

class ProfileViewModel(
    taskRepository: TaskRepository,
    goalRepository: GoalRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {

    val taskCount: LiveData<Int> = taskRepository.getCount()

    val goalCount: LiveData<Int> = goalRepository.getCount()

    val categoryCount: LiveData<Int> = categoryRepository.getCount()
}