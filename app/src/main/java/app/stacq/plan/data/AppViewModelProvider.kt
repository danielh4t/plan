package app.stacq.plan.data

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.stacq.plan.PlanApplication
import app.stacq.plan.ui.task.TaskViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskViewModel(planApplication().container.taskRepository)
        }
    }
}

fun CreationExtras.planApplication(): PlanApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PlanApplication)
