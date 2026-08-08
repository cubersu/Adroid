package com.adroid.cryptosignal.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adroid.cryptosignal.R
import com.adroid.cryptosignal.domain.model.SignalType
import com.adroid.cryptosignal.presentation.theme.SignalBuyGreen
import com.adroid.cryptosignal.presentation.theme.SignalNeutralGray
import com.adroid.cryptosignal.presentation.theme.SignalSellRed

@Composable
fun SignalBadge(type: SignalType, modifier: Modifier = Modifier, large: Boolean = false) {
    val (color, labelRes) = when (type) {
        SignalType.BUY -> SignalBuyGreen to R.string.signal_buy
        SignalType.SELL -> SignalSellRed to R.string.signal_sell
        SignalType.NEUTRAL -> SignalNeutralGray to R.string.signal_none
    }
    Text(
        text = stringResource(labelRes),
        color = Color.White,
        fontWeight = FontWeight.Bold,
        style = if (large) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(color = color, shape = RoundedCornerShape(if (large) 10.dp else 6.dp))
            .padding(
                horizontal = if (large) 20.dp else 10.dp,
                vertical = if (large) 10.dp else 4.dp
            )
    )
}

/** Small colored accent stripe for list rows — shown only when [type] is BUY or SELL. */
@Composable
fun SignalAccentStripe(type: SignalType?, modifier: Modifier = Modifier) {
    val color = when (type) {
        SignalType.BUY -> SignalBuyGreen
        SignalType.SELL -> SignalSellRed
        else -> Color.Transparent
    }
    Box(modifier = modifier.background(color))
}
