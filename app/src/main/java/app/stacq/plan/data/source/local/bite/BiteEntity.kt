package app.stacq.plan.data.source.local.bite

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.*

@Keep
@Entity(tableName = "bite")
data class BiteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = Instant.now().epochSecond,

    var name: String,

    @ColumnInfo(name = "task_id")
    var taskId: String,

    var completed: Boolean = false,

    @ColumnInfo(name = "completed_at")
    var completedAt: Long = 0,
)

