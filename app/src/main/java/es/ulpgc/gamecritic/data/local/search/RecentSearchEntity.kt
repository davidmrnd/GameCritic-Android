package es.ulpgc.gamecritic.data.local.search

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_searches",
    indices = [
        Index(value = ["item_id"], unique = true),
        Index(value = ["timestamp"])
    ]
)
data class RecentSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "item_id")
    val itemId: String,

    @ColumnInfo(name = "display_text")
    val displayText: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "tab")
    val tab: String,

    @ColumnInfo(name = "image_url")
    val imageUrl: String
)
