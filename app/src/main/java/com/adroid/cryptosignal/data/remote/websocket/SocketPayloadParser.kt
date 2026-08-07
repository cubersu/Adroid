package com.adroid.cryptosignal.data.remote.websocket

import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.Ticker
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/**
 * Shape-based extraction from a socket push payload. BtcTurk's public docs were unreachable
 * from this build environment, so field names are matched against the aliases most commonly
 * used across BtcTurk's REST/socket payloads and community wrappers. If the live payload uses
 * different keys, only this file needs updating.
 */
internal object SocketPayloadParser {

    private fun JsonObject.stringField(vararg keys: String): String? {
        for (key in keys) {
            val value = this[key]?.jsonPrimitive?.content
            if (!value.isNullOrBlank()) return value
        }
        return null
    }

    private fun JsonObject.doubleField(vararg keys: String): Double? {
        for (key in keys) {
            val value = this[key]?.jsonPrimitive?.doubleOrNull
            if (value != null) return value
        }
        return null
    }

    private fun JsonObject.longField(vararg keys: String): Long? {
        for (key in keys) {
            val value = this[key]?.jsonPrimitive?.longOrNull
            if (value != null) return value
        }
        return null
    }

    /** A candle push mirrors OHLCV: it must have open/high/low/close, in whatever key style. */
    fun tryParseCandle(pairSymbolHint: String, payload: JsonObject): Candle? {
        val open = payload.doubleField("o", "open") ?: return null
        val high = payload.doubleField("h", "high") ?: return null
        val low = payload.doubleField("l", "low") ?: return null
        val close = payload.doubleField("c", "close") ?: return null
        val volume = payload.doubleField("v", "volume") ?: 0.0
        val time = payload.longField("t", "time", "timestamp") ?: (System.currentTimeMillis() / 1000)
        // Normalize: some feeds send epoch millis, candle math expects seconds.
        val openTimeSeconds = if (time > 10_000_000_000L) time / 1000 else time
        return Candle(
            openTimeSeconds = openTimeSeconds,
            open = open,
            high = high,
            low = low,
            close = close,
            volume = volume
        )
    }

    /** A ticker push must at least carry a last-traded price. */
    fun tryParseTicker(pairSymbolHint: String, payload: JsonObject): Ticker? {
        val last = payload.doubleField("last", "l", "close", "price") ?: return null
        val pair = payload.stringField("pair", "pairSymbol", "symbol") ?: pairSymbolHint
        return Ticker(
            pairSymbol = pair,
            last = last,
            bid = payload.doubleField("bid", "b") ?: last,
            ask = payload.doubleField("ask", "a") ?: last,
            dailyPercent = payload.doubleField("dailyPercent", "changePercent") ?: 0.0,
            volume = payload.doubleField("volume", "v") ?: 0.0,
            timestampMillis = payload.longField("timestamp", "t") ?: System.currentTimeMillis()
        )
    }

    fun extractPairSymbol(payload: JsonObject): String? =
        payload.stringField("pair", "pairSymbol", "symbol")
}
