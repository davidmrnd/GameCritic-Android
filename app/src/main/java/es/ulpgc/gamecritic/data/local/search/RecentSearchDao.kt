package es.ulpgc.gamecritic.data.local.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM recent_searches WHERE item_id = :itemId")
    suspend fun deleteByItemId(itemId: String)

    @Query("DELETE FROM recent_searches")
    suspend fun deleteAll()

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentSearches(limit: Int): Flow<List<RecentSearchEntity>>
}
