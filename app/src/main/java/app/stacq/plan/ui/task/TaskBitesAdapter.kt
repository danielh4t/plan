package app.stacq.plan.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.databinding.ListItemBiteBinding
import app.stacq.plan.domain.Bite


class BitesAdapter(
    private val biteCompleteListener: BiteCompleteListener,
    private val biteNavigateListener: BiteNavigateListener,
) : ListAdapter<Bite, BitesAdapter.ViewHolder>(BiteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bite = getItem(position)
        holder.bind(bite, biteCompleteListener, biteNavigateListener)
    }

    fun getBite(position: Int): Bite {
        return getBite(position)
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

        fun bind(
            bite: Bite,
            biteCompleteListener: BiteCompleteListener,
            biteNavigateListener: BiteNavigateListener
        ) {
            binding.bite = bite
            binding.biteCompleteListener = biteCompleteListener
            binding.biteNavigateListener = biteNavigateListener
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

class BiteCompleteListener(val biteCompleteListener: (bite: Bite) -> Unit) {
    fun onClick(bite: Bite) = biteCompleteListener(bite)
}

class BiteNavigateListener(val biteNavigateListener: (biteId: String) -> Unit) {
    fun onClick(biteId: String) = biteNavigateListener(biteId)
}