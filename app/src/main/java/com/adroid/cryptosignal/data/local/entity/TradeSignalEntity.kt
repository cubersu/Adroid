package com.adroid.cryptosignal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_signals")
data class TradeSignalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pairSymbol: String,
    val type: String,
    val price: Double,
    val confidenceScore: Int,
    val totalCriteria: Int,
    val matchedCriteria: String,
    val strategyName: String,
    val timestampMillis: Long
)
