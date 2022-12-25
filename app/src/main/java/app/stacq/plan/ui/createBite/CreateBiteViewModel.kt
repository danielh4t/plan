package app.stacq.plan.ui.createBite

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.domain.asBite
import app.stacq.plan.data.source.repository.BiteRepository
import app.stacq.plan.util.constants.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class CreateBiteViewModel(
    private val biteRepository: BiteRepository,
) : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun create(name: String, taskId: String) {
        viewModelScope.launch {
            try {
                val biteEntity = BiteEntity(name = name, taskId = taskId)
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
