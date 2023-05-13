package app.stacq.plan.util.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import app.stacq.plan.R
import app.stacq.plan.databinding.ListItemCategoryDropdownBinding.*
import app.stacq.plan.domain.Category

class CategoryMenuAdapter(context: Context, categories: List<Category>) :
    ArrayAdapter<Category>(context, R.layout.list_item_category_dropdown, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = inflate(layoutInflater, parent, false)
        binding.category = getItem(position)
        return binding.root
    }
}
