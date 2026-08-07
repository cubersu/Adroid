package com.adroid.cryptosignal.presentation.common

import androidx.compose.foundation.background
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
fun SignalBadge(type: SignalType, modifier: Modifier = Modifier) {
    val (color, labelRes) = when (type) {
        SignalType.BUY -> SignalBuyGreen to R.string.signal_buy
        SignalType.SELL -> SignalSellRed to R.string.signal_sell
        SignalType.NEUTRAL -> SignalNeutralGray to R.string.signal_none
    }
    Text(
        text = stringResource(labelRes),
        color = Color.White,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(color = color, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
