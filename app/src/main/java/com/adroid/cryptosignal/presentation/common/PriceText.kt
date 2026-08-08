package com.adroid.cryptosignal.presentation.common

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.adroid.cryptosignal.presentation.theme.MonoFontFamily

/**
 * Renders [text] in the app's monospace font — used for every numeric readout (price, percent
 * change, RSI, MACD, etc.) so figures stay aligned and easy to scan at a glance.
 */
@Composable
fun PriceText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = LocalContentColor.current
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(fontFamily = MonoFontFamily),
        color = color
    )
}
