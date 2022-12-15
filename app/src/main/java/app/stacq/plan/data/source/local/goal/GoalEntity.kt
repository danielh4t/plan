package app.stacq.plan.data.source.local.goal

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*


@Keep
@Entity(tableName = "goal")
data class GoalEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().epochSecond,

    var name: String,

    @ColumnInfo(name = "category_id")
    var categoryId: String,

    var achieved: Boolean = false,

    @ColumnInfo(name = "achieved_at")
    var achievedAt: Long = 0,

    @ColumnInfo(name = "achieved_by")
    var achievedBy: Long,
)
