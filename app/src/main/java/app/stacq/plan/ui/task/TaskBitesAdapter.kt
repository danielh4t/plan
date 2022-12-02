package app.stacq.plan.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.data.source.model.Bite
import app.stacq.plan.databinding.ListItemBiteBinding


class BitesAdapter(private val viewModel: TaskViewModel) :
    ListAdapter<Bite, BitesAdapter.ViewHolder>(BiteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, viewModel)
    }

    class ViewHolder private constructor(private val binding: ListItemBiteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBiteBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(bite: Bite, viewModel: TaskViewModel) {
            binding.bite = bite
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

}

class BiteDiffCallback : DiffUtil.ItemCallback<Bite>() {

    override fun areItemsTheSame(oldItem: Bite, newItem: Bite): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Bite, newItem: Bite): Boolean {
        return oldItem == newItem
    }

}

