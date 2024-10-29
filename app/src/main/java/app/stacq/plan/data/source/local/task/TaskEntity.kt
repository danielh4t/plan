package app.stacq.plan.data.source.local.task

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID


@Keep
@Entity(tableName = "task")
data class TaskEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis,

    var name: String,

    @ColumnInfo(name = "completed_at")
    var completedAt: Long? = null,

    var notes: String? = null,
)
