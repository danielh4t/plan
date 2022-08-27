package app.stacq.plan.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    var title: String = "",
    var category: Category = Category.Code,

    var isCompleted: Boolean = false,
    @ColumnInfo(name = "completed_at")
    val completedAt: Long = 0
)