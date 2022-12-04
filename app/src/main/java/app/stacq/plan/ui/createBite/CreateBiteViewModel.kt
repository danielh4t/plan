package app.stacq.plan.ui.createBite

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.model.asBite
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateBiteViewModel(
    private val biteRepository: BiteRepository,
) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun create(name: String, taskId: String, categoryId: String) {
        viewModelScope.launch {
            try {
                val biteEntity = BiteEntity(name = name, taskId = taskId, categoryId = categoryId)
                val bite = biteEntity.asBite()
                biteRepository.create(bite)
            } catch (e: Error) {
                val params = Bundle()
                params.putString("exception", e.message)
                firebaseAnalytics.logEvent(AnalyticsConstants.Event.CREATE_BITE, params)
            }
        }
    }

}
