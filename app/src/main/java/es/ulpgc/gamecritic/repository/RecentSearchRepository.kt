package es.ulpgc.gamecritic.repository

import es.ulpgc.gamecritic.data.local.search.RecentSearchDao
import es.ulpgc.gamecritic.data.local.search.RecentSearchEntity
import es.ulpgc.gamecritic.model.RecentSearch
import es.ulpgc.gamecritic.viewmodel.SearchTab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentSearchRepository(
    private val dao: RecentSearchDao
) {

    fun observeRecentSearches(limit: Int = 10): Flow<List<RecentSearch>> {
        return dao.observeRecentSearches(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun saveVideogameSearch(videogameId: String, title: String, imageUrl: String) {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isEmpty()) return
        val entity = RecentSearchEntity(
            itemId = videogameId,
            displayText = normalizedTitle,
            timestamp = System.currentTimeMillis(),
            tab = SearchTab.VIDEOGAMES.name,
            imageUrl = imageUrl
        )
        dao.insertOrUpdate(entity)
    }

    suspend fun saveUserSearch(userId: String, username: String, imageUrl: String) {
        val normalizedUsername = username.trim()
        if (normalizedUsername.isEmpty()) return
        val entity = RecentSearchEntity(
            itemId = userId,
            displayText = normalizedUsername,
            timestamp = System.currentTimeMillis(),
            tab = SearchTab.USERS.name,
            imageUrl = imageUrl
        )
        dao.insertOrUpdate(entity)
    }

    suspend fun deleteSearch(search: RecentSearch) {
        val id = search.id
        if (id != null) {
            dao.deleteById(id)
        } else {
            dao.deleteByItemId(search.itemId)
        }
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }
}

private fun RecentSearchEntity.toDomain(): RecentSearch {
    val safeTab = try {
        SearchTab.valueOf(tab)
    } catch (_: IllegalArgumentException) {
        SearchTab.VIDEOGAMES
    }

    return RecentSearch(
        id = id,
        itemId = itemId,
        displayText = displayText,
        timestamp = timestamp,
        tab = safeTab,
        imageUrl = imageUrl
    )
}
