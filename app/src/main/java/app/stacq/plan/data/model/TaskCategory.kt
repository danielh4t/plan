package app.stacq.plan.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class TaskCategory(
    var id: String,
    var createdAt: Long,
    var title: String,
    var completed: Boolean,
    var completedAt: Long,
    var categoryName: String,
    var timerFinishAt: Long,
    var timerAlarm: Boolean,
    var positionAt: Long
): Parcelable