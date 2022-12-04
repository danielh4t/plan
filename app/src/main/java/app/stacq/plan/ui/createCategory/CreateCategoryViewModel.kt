package app.stacq.plan.ui.createCategory

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.model.asCategory
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.util.AnalyticsConstants
import app.stacq.plan.util.defaultColors
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateCategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun create(name: String) {
        viewModelScope.launch {
            try {
                val color: String = defaultColors(name)
                val categoryEntity = CategoryEntity(name = name, color = color)
                val category = categoryEntity.asCategory()
                categoryRepository.create(category)
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_CATEGORY, params)
            }
        }
    }

}