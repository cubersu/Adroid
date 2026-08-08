package com.adroid.cryptosignal.di

import android.content.Context
import androidx.room.Room
import com.adroid.cryptosignal.data.local.AppDatabase
import com.adroid.cryptosignal.data.local.dao.TradeSignalDao
import com.adroid.cryptosignal.data.local.dao.WatchedPairDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideWatchedPairDao(database: AppDatabase): WatchedPairDao = database.watchedPairDao()

    @Provides
    fun provideTradeSignalDao(database: AppDatabase): TradeSignalDao = database.tradeSignalDao()
}
