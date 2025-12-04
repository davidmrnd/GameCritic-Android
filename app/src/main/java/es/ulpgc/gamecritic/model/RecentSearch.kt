package es.ulpgc.gamecritic.model

data class RecentSearch(
    val id: Long? = null,
    val itemId: String,
    val displayText: String,
    val timestamp: Long,
    val tab: SearchTab,
    val imageUrl: String
)
