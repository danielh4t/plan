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
                val category = Category(name = categoryName, color = categoryColor)
                categoryRepository.create(category)
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_CATEGORY, params)
            }
        }
    }

    private fun defaultColors(color: String): String {
        return when (color) {
            "Code" -> "#FF7F50"
            "Hack" -> "#2ED573"
            "Life" -> "#FDCD21"
            "Work" -> "#1E90FF"
            else -> colorPalette()
        }
    }

    private fun colorPalette(): String {
        val palette = listOf(
            "FF74B1",
            "FFB200",
            "B2A4FF",
            "FF00E4",
            "FF4848",
            "00EAD3",
            "FC5404",
            "F637EC",
            "4D77FF",
            "93FFD8"
        )

        return palette.random()
    }

}