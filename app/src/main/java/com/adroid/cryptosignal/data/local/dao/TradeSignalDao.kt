package com.adroid.cryptosignal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.adroid.cryptosignal.data.local.entity.TradeSignalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeSignalDao {

    @Query("SELECT * FROM trade_signals ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<TradeSignalEntity>>

    @Query("SELECT * FROM trade_signals WHERE pairSymbol = :pairSymbol ORDER BY timestampMillis DESC")
    fun observeForPair(pairSymbol: String): Flow<List<TradeSignalEntity>>

    @Query("SELECT * FROM trade_signals WHERE pairSymbol = :pairSymbol ORDER BY timestampMillis DESC LIMIT 1")
    fun observeLatestForPair(pairSymbol: String): Flow<TradeSignalEntity?>

    @Query("SELECT * FROM trade_signals WHERE id = :id")
    fun observeById(id: Long): Flow<TradeSignalEntity?>

    @Insert
    suspend fun insert(entity: TradeSignalEntity): Long
}
