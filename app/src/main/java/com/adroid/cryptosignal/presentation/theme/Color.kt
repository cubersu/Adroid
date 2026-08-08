package com.adroid.cryptosignal.presentation.theme

import androidx.compose.ui.graphics.Color

// Trader-panel dark palette. Near-black background, amber/blue accent kept clear of the
// buy/sell hues so selection state never gets confused with signal direction.
val BackgroundDark = Color(0xFF0D1117)
val SurfaceDark = Color(0xFF161B22)
val SurfaceVariantDark = Color(0xFF21262D)
val OutlineDark = Color(0xFF30363D)

val TextPrimary = Color(0xFFE6EDF3)
val TextSecondary = Color(0xFF8B949E)

val BuyGreen = Color(0xFF00C853)
val SellRed = Color(0xFFFF3B30)
val NeutralGray = Color(0xFF8B949E)

val AccentBlue = Color(0xFF5B8DEF)

// Aliases kept for existing call sites (SignalBadge, chart legend, etc.).
val SignalBuyGreen = BuyGreen
val SignalSellRed = SellRed
val SignalNeutralGray = NeutralGray
