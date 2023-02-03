package app.stacq.plan

import android.app.Application
import com.google.android.material.color.DynamicColors

class PlanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}