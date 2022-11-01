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
import kotlin.random.Random

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
            "Code" -> "#ffff7f50"
            "Hack" -> "#ff2ed573"
            "Life" -> "#fffdcd21"
            "Work" -> "#ff1e90ff"
            else ->  String.format("#ff%06x", Random.nextInt(0xffffff + 1))
        }
    }

}