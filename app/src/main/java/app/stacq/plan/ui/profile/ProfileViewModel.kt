package app.stacq.plan.ui.profile

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics


    fun logAuthentication(errorCode: Int) {
        val params = Bundle()
        params.putInt("login_error_code", errorCode)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, params)
    }

}