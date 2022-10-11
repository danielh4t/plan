package app.stacq.plan.data.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Keep
@Entity(tableName = "category")
data class Category (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    var name: String,
)