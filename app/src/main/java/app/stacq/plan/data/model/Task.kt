package app.stacq.plan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "task")
data class Task(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis() / 1000,

    var title: String = "",

    @ColumnInfo(name = "category_id")
    var categoryId: Int,

    var completed: Boolean = false,

    @ColumnInfo(name = "completed_at")
    var completedAt: Long = 0,

    @ColumnInfo(name = "timer_finish_at")
    var timerFinishAt: Long = 0

)

