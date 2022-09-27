package app.stacq.plan.data.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey


@Keep
@Entity(tableName = "category")
data class Category (

    @PrimaryKey
    var id: Int,
    var name: String,
)