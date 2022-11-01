package app.stacq.plan.ui.tasks


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.data.model.TaskCategory
import app.stacq.plan.databinding.TaskListItemBinding
import java.util.*


class TasksAdapter(private val viewModel: TasksViewModel) :
    ListAdapter<TaskCategory, TasksAdapter.ViewHolder>(TaskDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
    }

    class ViewHolder private constructor(private val binding: TaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TaskListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: TaskCategory, viewModel: TasksViewModel) {
            binding.task = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

}

class TaskDiffCallback : DiffUtil.ItemCallback<TaskCategory>() {

    override fun areItemsTheSame(oldItem: TaskCategory, newItem: TaskCategory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskCategory, newItem: TaskCategory): Boolean {
        return oldItem == newItem
    }

}

val taskItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    0
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        val adapter = recyclerView.adapter as TasksAdapter
        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition

        val tasks = adapter.currentList.toMutableList()
        Collections.swap(tasks, fromPos, toPos)
        adapter.submitList(tasks)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }


}