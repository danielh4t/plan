package app.stacq.plan.ui.create

import androidx.lifecycle.*
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.data.source.repository.TasksRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateViewModel(
    private val tasksRepository: TasksRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    private var firebaseAuth: FirebaseAuth = Firebase.auth

    private val _taskCreated = MutableLiveData<Boolean?>()
    val taskCreated: LiveData<Boolean?> = _taskCreated

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    fun createTask(task: Task) {


        firebaseAuth.currentUser?.getIdToken(false)
            ?.addOnCompleteListener {

                if (it.isSuccessful) {
                    val idToken = it.result.token
                    viewModelScope.launch {
                        if (idToken != null) {
                            tasksRepository.createTask(task, idToken)
                        }
                    }

                    // Send token to your backend via HTTPS
                    // ...
                } else {
                    // Handle error -> task.getException();
                }
            }
//        viewModelScope.launch {
//            tasksRepository.createTask(task, tokenId)
//                .onSuccess { _taskCreated.value = true
//                }.onError { code, message ->
//                    Log.d("", "$code $message")
//                }.onException {
//                    val params = Bundle()
//                    params.putString("exception", it.message)
//                    firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_TASK, params)
//                }
//        }
    }

}