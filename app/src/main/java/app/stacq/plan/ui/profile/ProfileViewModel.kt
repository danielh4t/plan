package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.util.CalendarUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel(
    taskRepository: TaskRepository,
    goalRepository: GoalRepository,
    categoryRepository: CategoryRepository,
) : ViewModel() {

    val taskCount: LiveData<Int> = taskRepository.getCount()

    val goalCount: LiveData<Int> = goalRepository.getCount()

    val categoryCount: LiveData<Int> = categoryRepository.getCount()

    val taskCompletedToday: LiveData<Int> = taskRepository.getCompletedToday()

    val taskGoalCompletedToday: LiveData<Int> = taskRepository.getCompletedTaskGoalToday()

    private val _dayProgress = MutableLiveData<Int>()
    val dayProgress: LiveData<Int>
        get() = _dayProgress

    init {
        updateDayProgress()
    }

    private fun updateDayProgress() {
        viewModelScope.launch {
            while (true) {
                val totalMinutesInDay = 24 * 60 // 1440
                val elapsedMinutes = CalendarUtil().getDayElapsedMinutes()
                // Calculate the progress value as a percentage of the total number of minutes in a day
                val progress = ((elapsedMinutes.toFloat() / totalMinutesInDay) * 100).toInt()
                _dayProgress.postValue(progress)
                delay(1000)
            }
        }
    }
}