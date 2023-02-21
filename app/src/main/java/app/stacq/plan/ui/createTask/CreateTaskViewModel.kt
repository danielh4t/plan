package app.stacq.plan.ui.createTask

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.repository.category.CategoryRepositoryImpl
import app.stacq.plan.data.repository.task.TaskRepositoryImpl
import app.stacq.plan.domain.Category
import app.stacq.plan.domain.asTask
import app.stacq.plan.util.constants.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val taskRepositoryImpl: TaskRepositoryImpl,
    categoryRepositoryImpl: CategoryRepositoryImpl
) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    val categories: LiveData<List<Category>> = categoryRepositoryImpl.getEnabledCategories()

    fun create(name: String, categoryId: String): String {
        val taskEntity = TaskEntity(name = name, categoryId = categoryId)
        viewModelScope.launch {
            try {
                val task = taskEntity.asTask()
                taskRepositoryImpl.create(task)
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_TASK, params)
            }
        }
        return taskEntity.id
    }
}