package es.ulpgc.gamecritic.model

import es.ulpgc.gamecritic.viewmodel.SearchTab

data class RecentSearch(
    val id: Long? = null,
    val itemId: String,
    val displayText: String,
    val timestamp: Long,
    val tab: SearchTab
)
