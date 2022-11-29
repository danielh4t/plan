package app.stacq.plan.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Category(
    var id: String,
    var createdAt: Long,
    var name: String,
    var color: String,
    var enabled: Boolean,
) : Parcelable