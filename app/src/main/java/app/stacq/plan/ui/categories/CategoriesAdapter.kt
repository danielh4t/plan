package app.stacq.plan.ui.categories


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.R
import app.stacq.plan.data.source.model.Category
import app.stacq.plan.databinding.ListItemCategoryBinding
import com.google.android.material.snackbar.Snackbar


class CategoriesAdapter(private val viewModel: CategoriesViewModel) :
    ListAdapter<Category, CategoriesAdapter.ViewHolder>(CategoryDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, viewModel)
        holder.itemView.setOnClickListener { _ ->
            viewModel.updateEnabled(category.id)
        }
    }

    class ViewHolder private constructor(private val binding: ListItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCategoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(category: Category, viewModel: CategoriesViewModel) {
            binding.category = category
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

}

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {

    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }

}