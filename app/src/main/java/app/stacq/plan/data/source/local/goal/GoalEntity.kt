package app.stacq.plan.data.source.local.goal

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID


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

    @ColumnInfo(name = "days")
    var days: Int,

    var completed: Boolean = false,

    var completedAt: Long = 0,

    var generate: Boolean = false,
)
