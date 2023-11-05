package app.stacq.plan.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.R
import app.stacq.plan.databinding.ListItemTimelineBinding
import app.stacq.plan.databinding.ListItemTimelineHeaderBinding
import app.stacq.plan.domain.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TimelineAdapter(
    private val timelineNavigateListener: TimelineNavigateListener,
) : ListAdapter<Timeline, RecyclerView.ViewHolder>(TimelineDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Timeline.TimelineHeader -> VIEW_TYPE_HEADER
            is Timeline.TimelineTask -> VIEW_TYPE_TASK
            else -> VIEW_TYPE_TASK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            VIEW_TYPE_TASK -> TaskViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is TaskViewHolder -> {
                val task = item as Timeline.TimelineTask
                holder.bind(task, timelineNavigateListener)
            }

            is HeaderViewHolder -> {
                val header = item as Timeline.TimelineHeader
                holder.bind(header)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_TASK = 1
    }

    class TaskViewHolder private constructor(private val binding: ListItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TaskViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTimelineBinding.inflate(layoutInflater, parent, false)
                return TaskViewHolder(binding)
            }
        }

        fun bind(task: Timeline.TimelineTask, timelineNavigateListener: TimelineNavigateListener) {
            binding.task = task.task
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

    class HeaderViewHolder constructor(private val binding: ListItemTimelineHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTimelineHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }

        fun bind(header: Timeline.TimelineHeader) {
            binding.header = header.header
            binding.executePendingBindings()
        }
    }

    private fun Long.toSimpleDate(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = Date(this * 1000)
        return dateFormat.format(date)
    }
    private fun groupTasksByDate(tasks: List<Task>): List<Timeline> {
        val groupedItems = mutableListOf<Timeline>()
        val groupedTasks = tasks.groupBy { it.completedAt.toSimpleDate() }

        for ((date, taskList) in groupedTasks) {
            groupedItems.add(Timeline.TimelineHeader(date))
            taskList.forEach {task->
                groupedItems.add(Timeline.TimelineTask(task))
            }
        }

        return groupedItems
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<Task>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(Timeline.EmptyHeader)
                else -> groupTasksByDate(list)
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
}

class TimelineDiffCallback : DiffUtil.ItemCallback<Timeline>() {

    override fun areItemsTheSame(oldItem: Timeline, newItem: Timeline): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Timeline, newItem: Timeline): Boolean {
        return oldItem == newItem
    }
}

class TimelineNavigateListener(val navigateListener: (taskId: String) -> Unit) {
    fun onClick(taskId: String) = navigateListener(taskId)
}


sealed class Timeline {
    data class TimelineTask(val task: Task) : Timeline() {
        override val id = task.id
    }

    data class TimelineHeader(val header: String) : Timeline() {
        override val id = header
    }

    data object EmptyHeader : Timeline() {
        override val id = ""
    }

    abstract val id: String
}