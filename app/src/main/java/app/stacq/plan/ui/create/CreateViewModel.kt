package app.stacq.plan.ui.create

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.remote.network.onError
import app.stacq.plan.data.source.remote.network.onException
import app.stacq.plan.data.source.remote.network.onSuccess
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

    private val _taskCreated = MutableLiveData<Boolean?>()
    val taskCreated: LiveData<Boolean?> = _taskCreated

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.createTask(task)
                .onSuccess { _taskCreated.value = true
                }.onError { code, message ->
                    Log.d("", "$code $message")
                }.onException {
                    val params = Bundle()
                    params.putString("exception", it.message)
                    firebaseAnalytics.logEvent(AnalyticsConstants.CREATE_TASK, params)

                }
        }
    }

}