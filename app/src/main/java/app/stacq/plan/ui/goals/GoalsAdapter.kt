package app.stacq.plan.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.databinding.ListItemGoalBinding
import app.stacq.plan.domain.Goal


class GoalsAdapter(
    private val goalNavigateListener: GoalNavigateListener,
    private val goalCompletedListener: GoalCompletedListener
) : ListAdapter<Goal, GoalsAdapter.ViewHolder>(GoalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = getItem(position)
        holder.bind(goal, goalNavigateListener,goalCompletedListener)
    }

    class ViewHolder private constructor(private val binding: ListItemGoalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemGoalBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            goal: Goal,
            goalNavigateListener: GoalNavigateListener,
            goalCompletedListener: GoalCompletedListener
        ) {
            binding.goal = goal
            binding.goalNavigateListener = goalNavigateListener
            binding.goalCompletedListener = goalCompletedListener
            binding.goalCategoryNameText.contentDescription = "${goal.name} text"
            binding.goalCompletedTodayCheckbox.contentDescription = "${goal.name} today"
            binding.executePendingBindings()
        }
    }
}

class GoalDiffCallback : DiffUtil.ItemCallback<Goal>() {

    override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
        return oldItem == newItem
    }
}


class GoalNavigateListener(val goalNavigateListener: (goalId: String) -> Unit) {
    fun onClick(goalId: String) = goalNavigateListener(goalId)
}

class GoalCompletedListener(val goalCompletedListener: (goal: Goal) -> Unit) {
    fun onClick(goal: Goal) = goalCompletedListener(goal)
}