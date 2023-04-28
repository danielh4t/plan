package app.stacq.plan.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.stacq.plan.databinding.ListItemCategoryBinding
import app.stacq.plan.domain.Category


class CategoriesAdapter(
    private val categoryEnableListener: CategoryEnableListener,
    private val categoryNavigateListener: CategoryNavigateListener,
) : ListAdapter<Category, CategoriesAdapter.ViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category, categoryEnableListener, categoryNavigateListener)
    }

    fun getCategory(position: Int): Category {
        return getItem(position)
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

        fun bind(category: Category, categoryEnableListener: CategoryEnableListener, categoryNavigateListener: CategoryNavigateListener,) {
            binding.category = category
            binding.categoryEnableListener = categoryEnableListener
            binding.categoryNavigateListener = categoryNavigateListener
            binding.categoryEnabled.contentDescription = "${category.name} is ${category.enabled}"
            binding.categoryName.contentDescription = "${category.name} category"
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