package app.stacq.plan.ui.input

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.model.Task
import app.stacq.plan.data.source.repository.TasksRepository
import kotlinx.coroutines.launch

class InputViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    val input = ObservableField<String>()
    private val _saved = MutableLiveData<Boolean>().apply {
        value = false
    }
    val saved: LiveData<Boolean> = _saved

    fun save() {
        viewModelScope.launch {
            val task = Task(title = input.get().toString(), category = Category.CODE)
            tasksRepository.insert(task)
            _saved.postValue(true)
        }
    }

}