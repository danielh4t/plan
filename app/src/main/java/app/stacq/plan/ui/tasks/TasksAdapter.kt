package app.stacq.plan.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.domain.Task
import app.stacq.plan.databinding.ListItemTaskBinding


class TasksAdapter(
    private val taskNavigateListener: TaskNavigateListener,
    private val taskCompleteListener: TaskCompleteListener
) : ListAdapter<Task, TasksAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, taskNavigateListener, taskCompleteListener)
    }

    fun getTask(position: Int): Task {
        return getItem(position)
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
            taskCompleteListener: TaskCompleteListener
        ) {
            binding.task = task
            binding.taskNavigateListener = taskNavigateListener
            binding.taskCompleteListener = taskCompleteListener
            binding.taskName.contentDescription = "${task.name} name"
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

class TaskNavigateListener(val navigateListener: (taskId: String) -> Unit) {
    fun onClick(taskId: String) = navigateListener(taskId)
}

class TaskCompleteListener(val completeListener: (task: Task) -> Unit) {
    fun onClick(task: Task) = completeListener(task)
}

class TaskArchiveListener(val taskArchiveListener: (task: Task) -> Unit) {
    fun onClick(task: Task) = taskArchiveListener(task)
}