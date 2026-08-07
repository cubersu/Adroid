package com.adroid.cryptosignal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adroid.cryptosignal.data.local.dao.TradeSignalDao
import com.adroid.cryptosignal.data.local.dao.WatchedPairDao
import com.adroid.cryptosignal.data.local.entity.TradeSignalEntity
import com.adroid.cryptosignal.data.local.entity.WatchedPairEntity

@Database(
    entities = [WatchedPairEntity::class, TradeSignalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchedPairDao(): WatchedPairDao
    abstract fun tradeSignalDao(): TradeSignalDao

    companion object {
        const val DATABASE_NAME = "adroid_signal_db"
    }
}
