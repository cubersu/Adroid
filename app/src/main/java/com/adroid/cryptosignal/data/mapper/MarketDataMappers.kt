package com.adroid.cryptosignal.data.mapper

import com.adroid.cryptosignal.data.remote.dto.KlineHistoryResponseDto
import com.adroid.cryptosignal.data.remote.dto.SymbolDto
import com.adroid.cryptosignal.data.remote.dto.TickerDto
import com.adroid.cryptosignal.domain.model.Candle
import com.adroid.cryptosignal.domain.model.Ticker
import com.adroid.cryptosignal.domain.model.TradingPair

fun SymbolDto.toDomain(): TradingPair = TradingPair(
    symbol = name,
    numerator = numerator,
    denominator = denominator,
    displayName = "$numerator/$denominator"
)

fun TickerDto.toDomain(): Ticker = Ticker(
    pairSymbol = pair,
    last = last,
    bid = bid,
    ask = ask,
    dailyPercent = dailyPercent,
    volume = volume,
    timestampMillis = if (timestamp > 10_000_000_000L) timestamp else timestamp * 1000
)

fun KlineHistoryResponseDto.toDomainCandles(): List<Candle> {
    if (s != "ok") return emptyList()
    val size = minOf(t.size, o.size, h.size, l.size, c.size, v.size)
    return (0 until size).map { i ->
        Candle(
            openTimeSeconds = t[i],
            open = o[i],
            high = h[i],
            low = l[i],
            close = c[i],
            volume = v[i]
        )
    }
}
