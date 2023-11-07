package app.stacq.plan.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.R
import app.stacq.plan.domain.Task
import app.stacq.plan.databinding.ListItemTaskBinding


class TasksAdapter(
    private val taskNavigateListener: TaskNavigateListener,
    private val taskStartCompleteListener: TaskStartCompleteListener,
    private val taskArchiveListener: TaskArchiveListener,
) : ListAdapter<Task, TasksAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, taskNavigateListener, taskStartCompleteListener, taskArchiveListener)
    }

    class ViewHolder constructor(private val binding: ListItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTaskBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            task: Task,
            taskNavigateListener: TaskNavigateListener,
            taskStartCompleteListener: TaskStartCompleteListener,
            taskArchiveListener: TaskArchiveListener,
        ) {
            binding.task = task
            binding.taskNavigateListener = taskNavigateListener
            binding.taskStartCompleteListener = taskStartCompleteListener
            binding.taskName.contentDescription = "${task.name} name"
            ViewCompat.addAccessibilityAction(
                itemView,
                binding.root.context.getString(R.string.archive)
            ) { _, _ ->
                taskArchiveListener.onClick(task)
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

class TaskNavigateListener(val taskNavigateListener: (taskId: String) -> Unit) {
    fun onClick(taskId: String) = taskNavigateListener(taskId)
}

class TaskStartCompleteListener(val taskStartCompleteListener: (task: Task) -> Unit) {
    fun onClick(task: Task) = taskStartCompleteListener(task)
}

class TaskArchiveListener(val taskArchiveListener: (task: Task) -> Unit) {
    fun onClick(task: Task) = taskArchiveListener(task)
}