package com.adroid.cryptosignal.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adroid.cryptosignal.R

@Composable
fun DisclaimerBanner(modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.outlinedCardColors()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(imageVector = Icons.Filled.WarningAmber, contentDescription = null)
            Text(
                text = stringResource(R.string.disclaimer_title),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(R.string.disclaimer_body),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
