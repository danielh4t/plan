package app.stacq.plan.ui.category


import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.repository.CategoryRepository
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun create(categoryName: String) {
        val categoryColor: String = defaultColors(categoryName)
        viewModelScope.launch {
            try {
                val category = Category(categoryName, categoryColor)
                categoryRepository.insert(category)
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_CATEGORY, params)
            }
        }
    }

    private fun defaultColors(color: String): String {
        return when (color) {
            "Code" -> "#FFFF7F50"
            "Hack" -> "#FF2ED573"
            "Life" -> "#FFFDCD21"
            "Work" -> "#FF1E90FF"
            else -> "#FFBB86FC"
        }
    }

}