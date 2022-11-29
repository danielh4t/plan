package app.stacq.plan.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Task(
    var id: String,
    var createdAt: Long,
    var name: String,
    var completed: Boolean,
    var completedAt: Long,
    var categoryId: String,
    var categoryName: String,
    var categoryColor: String,
    var timerFinishAt: Long,
    var timerAlarm: Boolean,
    var positionAt: Long
): Parcelable