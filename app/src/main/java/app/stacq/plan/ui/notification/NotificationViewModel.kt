package app.stacq.plan.ui.notification

import androidx.lifecycle.ViewModel
import app.stacq.plan.util.constants.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase


class NotificationViewModel : ViewModel() {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logPermission(isGranted: Boolean) {
        firebaseAnalytics.logEvent(AnalyticsConstants.Event.APP_PERMISSION) {
            param("notifications", if (isGranted) "true" else "false")
        }
    }
}