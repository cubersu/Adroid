package com.adroid.cryptosignal.di

import com.adroid.cryptosignal.data.repository.MarketDataRepositoryImpl
import com.adroid.cryptosignal.data.repository.SettingsRepositoryImpl
import com.adroid.cryptosignal.data.repository.SignalRepositoryImpl
import com.adroid.cryptosignal.data.repository.WatchlistRepositoryImpl
import com.adroid.cryptosignal.domain.repository.MarketDataRepository
import com.adroid.cryptosignal.domain.repository.SettingsRepository
import com.adroid.cryptosignal.domain.repository.SignalRepository
import com.adroid.cryptosignal.domain.repository.WatchlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMarketDataRepository(impl: MarketDataRepositoryImpl): MarketDataRepository

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository

    @Binds
    @Singleton
    abstract fun bindSignalRepository(impl: SignalRepositoryImpl): SignalRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
