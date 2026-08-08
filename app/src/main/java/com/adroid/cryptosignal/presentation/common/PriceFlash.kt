package com.adroid.cryptosignal.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

private val FlashGreen = Color(0x3300C853)
private val FlashRed = Color(0x33FF3B30)

/**
 * Briefly highlights green (price went up) or red (price went down) whenever [price] changes,
 * then fades back to transparent. Used as a Card/Row background to give watchlist rows the
 * "ticking" feel of a live price feed.
 */
@Composable
fun rememberPriceFlashColor(price: Double?): Color {
    var previousPrice by remember { mutableStateOf(price) }
    var flashColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(price) {
        val previous = previousPrice
        previousPrice = price
        if (price != null && previous != null && price != previous) {
            flashColor = if (price > previous) FlashGreen else FlashRed
            delay(FLASH_DURATION_MS)
            flashColor = Color.Transparent
        }
    }

    return flashColor
}

private const val FLASH_DURATION_MS = 350L
