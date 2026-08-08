package com.adroid.cryptosignal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adroid.cryptosignal.data.local.entity.WatchedPairEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedPairDao {

    @Query("SELECT * FROM watched_pairs ORDER BY addedAtMillis ASC")
    fun observeAll(): Flow<List<WatchedPairEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WatchedPairEntity)

    @Query("DELETE FROM watched_pairs WHERE symbol = :symbol")
    suspend fun deleteBySymbol(symbol: String)

    @Query("UPDATE watched_pairs SET isActive = :isActive WHERE symbol = :symbol")
    suspend fun setActive(symbol: String, isActive: Boolean)
}
