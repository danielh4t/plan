package app.stacq.plan.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Keep
@Entity(tableName = "category")
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = UUID.randomUUID().toString(),
    var name: String,
    var color: String,
    var enabled: Boolean = true
)