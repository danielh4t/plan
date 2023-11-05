package app.stacq.plan.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.R
import app.stacq.plan.databinding.ListItemTimelineBinding
import app.stacq.plan.domain.Task


class TimelineAdapter(
    private val timelineNavigateListener: TimelineNavigateListener,
) : ListAdapter<Task, TimelineAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, timelineNavigateListener)
    }

    class ViewHolder private constructor(private val binding: ListItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTimelineBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(task: Task, timelineNavigateListener: TimelineNavigateListener) {
            binding.task = task
            binding.timelineNavigateListener = timelineNavigateListener
            ViewCompat.addAccessibilityAction(
                itemView,
                binding.root.context.getString(R.string.task)
            ) { _, _ ->
                timelineNavigateListener.onClick(task.id)
                true
            }
            binding.executePendingBindings()
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {

    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}

class TimelineNavigateListener(val navigateListener: (taskId: String) -> Unit) {
    fun onClick(taskId: String) = navigateListener(taskId)
}
