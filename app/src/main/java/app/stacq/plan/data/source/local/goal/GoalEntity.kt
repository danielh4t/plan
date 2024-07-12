package app.stacq.plan.data.source.local.goal

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.stacq.plan.util.TimeUtil
import java.util.UUID

@Keep
@Entity(tableName = "goal")
data class GoalEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = TimeUtil().nowInSeconds(),

    var name: String,

    var measure: String,

    var result: String,

    @ColumnInfo(name = "category_id")
    var categoryId: String,

    @ColumnInfo(name = "days")
    var days: Int,

    var progress: Int = 0,

    @ColumnInfo(name = "completed_at")
    var completedAt: Long? = null,

    var generate: Boolean = false,

    var archived: Boolean = false,
)
