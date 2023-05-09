package app.stacq.plan.data.source.local.task

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.stacq.plan.util.time.TimeUtil
import java.util.UUID

@Keep
@Entity(tableName = "task")
data class TaskEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = TimeUtil().nowInSeconds(),

    var name: String,

    @ColumnInfo(name = "category_id")
    var categoryId: String,

    var completed: Boolean = false,

    @ColumnInfo(name = "completed_at")
    var completedAt: Long = 0,

    @ColumnInfo(name = "timer_finish_at")
    var timerFinishAt: Long = 0,

    @ColumnInfo(name = "timer_alarm")
    var timerAlarm: Boolean = true,

    @ColumnInfo(name = "priority")
    var priority: Int = 0,

    @ColumnInfo(name = "goal_id")
    var goalId: String? = null,

    var archived: Boolean = false,

    var notes: String? = null,
)