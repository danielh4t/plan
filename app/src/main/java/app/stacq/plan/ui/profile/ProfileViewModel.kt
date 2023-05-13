package app.stacq.plan.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.stacq.plan.data.repository.category.CategoryRepository
import app.stacq.plan.data.repository.goal.GoalRepository
import app.stacq.plan.data.repository.task.TaskRepository
import app.stacq.plan.util.CalendarUtil

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

    fun getDayProgress(): Int {
        val totalMinutesInDay = 24 * 60 // 1440
        val minutes = CalendarUtil().getDayElapsedMinutes()
        // Calculate the progress value as a percentage of the total number of minutes in a day
        val progress = minutes.toFloat() / totalMinutesInDay
        return (progress * 100).toInt()
    }
}