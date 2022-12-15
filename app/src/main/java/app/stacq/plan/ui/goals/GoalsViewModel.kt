package app.stacq.plan.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import app.stacq.plan.data.source.model.Goal
import app.stacq.plan.data.source.repository.GoalRepository


class GoalsViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    val goals: LiveData<List<Goal>> = liveData {
        emitSource(goalRepository.getGoals())
    }
}