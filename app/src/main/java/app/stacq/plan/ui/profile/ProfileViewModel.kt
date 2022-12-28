package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.domain.Category


class ProfileViewModel(
    taskRepository: TaskRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: LiveData<List<Category>> = categoryRepository.getCategories()


}