package app.stacq.plan.ui.categories


import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.model.Category
import app.stacq.plan.data.source.model.asCategory
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.util.AnalyticsConstants
import app.stacq.plan.util.defaultColors
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: LiveData<List<Category>> = liveData {
        emitSource(categoryRepository.getCategories())
    }

    /**
     * On long click of category list item flip enabled
     */
    fun updateEnabled(categoryId: String) {
        viewModelScope.launch {
            categoryRepository.updateEnabledById(categoryId)
        }
    }

}