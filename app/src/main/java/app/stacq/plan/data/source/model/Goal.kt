package app.stacq.plan.data.source.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Goal(
    var id: String,
    var createdAt: Long,
    var name: String,
    var achieved: Boolean,
    var achievedBy: Long,
    var achievedAt: Long,
    var categoryId: String,
    var categoryName: String,
    var categoryColor: String,
) : Parcelable
