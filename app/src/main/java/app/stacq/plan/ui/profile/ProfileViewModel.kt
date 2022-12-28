package app.stacq.plan.ui.profile

import androidx.lifecycle.ViewModel
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository


class ProfileViewModel(taskRepository: TaskRepository, categoryRepository: CategoryRepository) :
    ViewModel()