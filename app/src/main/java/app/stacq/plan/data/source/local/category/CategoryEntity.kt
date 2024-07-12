package app.stacq.plan.data.source.local.category

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.stacq.plan.util.TimeUtil
import java.util.UUID

@Keep
@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = TimeUtil().nowInSeconds(),

    var name: String,

    var color: String,

    var enabled: Boolean = true,

    var archived: Boolean = false
)