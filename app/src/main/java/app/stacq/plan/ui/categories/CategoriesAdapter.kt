package app.stacq.plan.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.R
import app.stacq.plan.databinding.ListItemCategoryBinding
import app.stacq.plan.domain.Category


class CategoriesAdapter(
    private val categoryEnableListener: CategoryEnableListener,
    private val categoryNavigateListener: CategoryNavigateListener,
    private val categoryDeletedListener: CategoryDeleteListener,
) : ListAdapter<Category, CategoriesAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, categoryEnableListener, categoryNavigateListener, categoryDeletedListener)
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

        fun bind(category: Category,
                 categoryEnableListener: CategoryEnableListener,
                 categoryNavigateListener: CategoryNavigateListener,
                 categoryDeletedListener: CategoryDeleteListener) {
            binding.category = category
            binding.categoryEnableListener = categoryEnableListener
            binding.categoryNavigateListener = categoryNavigateListener
            binding.categoryEnabled.contentDescription = "${category.name} is ${category.enabled}"
            binding.categoryName.contentDescription = "${category.name} category"
            ViewCompat.addAccessibilityAction(
                itemView,
                binding.root.context.getString(R.string.delete)
            ) { _, _ ->
                categoryDeletedListener.onClick(category)
                true
            }
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

class CategoryEnableListener(val categoryEnableListener: (categoryId: String) -> Unit) {
    fun onClick(categoryId: String) = categoryEnableListener(categoryId)
}

class CategoryNavigateListener(val categoryNavigateListener: (categoryId: String) -> Unit) {
    fun onClick(categoryId: String) = categoryNavigateListener(categoryId)
}

class CategoryDeleteListener(val categoryDeletedListener: (category: Category) -> Unit) {
    fun onClick(category: Category) = categoryDeletedListener(category)
}