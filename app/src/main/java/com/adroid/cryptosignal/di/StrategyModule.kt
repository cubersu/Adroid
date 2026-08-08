package com.adroid.cryptosignal.di

import com.adroid.cryptosignal.domain.strategy.DefaultSignalStrategy
import com.adroid.cryptosignal.domain.strategy.SignalStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StrategyModule {

    @Binds
    @Singleton
    abstract fun bindSignalStrategy(impl: DefaultSignalStrategy): SignalStrategy
}
