package app.stacq.plan.ui.create

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateViewModel(
    private val tasksRepository: TasksRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getEnabledCategories())
    }

    fun createTask(title: String, categoryId: String) {
        viewModelScope.launch {
            try {
                tasksRepository.createTask(Task(title = title, categoryId = categoryId))
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_TASK, params)
            }
        }
    }

}
