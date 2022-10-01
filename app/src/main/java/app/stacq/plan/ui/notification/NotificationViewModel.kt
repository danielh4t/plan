package app.stacq.plan.ui.notification

import android.os.Bundle
import androidx.lifecycle.ViewModel
import app.stacq.plan.util.AnalyticsConstants
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class NotificationViewModel : ViewModel() {

    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    fun logPermission(isGranted: Boolean) {
        val access = if (isGranted) "granted" else "denied"
        val params = Bundle()
        params.putString(AnalyticsConstants.Param.POST_NOTIFICATIONS, access)
        firebaseAnalytics.logEvent(AnalyticsConstants.Event.APP_PERMISSION, params)
    }

}