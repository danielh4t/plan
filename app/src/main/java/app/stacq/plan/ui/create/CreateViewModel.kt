package app.stacq.plan.ui.create

import android.os.Bundle
import androidx.lifecycle.*
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
        emitSource(categoryRepository.getCategories())
    }

    fun createTask(title: String, checkedId: Int) {
        viewModelScope.launch {
            try {
                val categories = categories.value
                if (categories != null) {
                    val index = checkedId - 1
                    if(index <= categories.size) {
                        val categoryId: String = categories[checkedId - 1].id
                        val task = Task(title = title, categoryId = categoryId)
                        tasksRepository.createTask(task)
                    }
                }
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_TASK, params)
            }
        }
    }

}
