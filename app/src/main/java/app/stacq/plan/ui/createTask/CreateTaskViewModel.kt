package app.stacq.plan.ui.createTask

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.model.Category
import app.stacq.plan.data.source.model.asTask
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TaskRepository
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    fun create(name: String, categoryId: String) {
        viewModelScope.launch {
            try {
                val taskEntity = TaskEntity(name = name, categoryId = categoryId)
                val task = taskEntity.asTask()
                taskRepository.create(task)
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_TASK, params)
            }
        }
    }

}
