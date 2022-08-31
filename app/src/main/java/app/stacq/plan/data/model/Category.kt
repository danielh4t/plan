package app.stacq.plan.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class Category (

    @PrimaryKey
    var id: Int,
    var name: String,
)