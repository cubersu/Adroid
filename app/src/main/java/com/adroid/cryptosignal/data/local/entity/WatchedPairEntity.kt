package com.adroid.cryptosignal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched_pairs")
data class WatchedPairEntity(
    @PrimaryKey val symbol: String,
    val numerator: String,
    val denominator: String,
    val displayName: String,
    val isActive: Boolean,
    val addedAtMillis: Long
)
