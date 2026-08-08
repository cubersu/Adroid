package com.adroid.cryptosignal.presentation.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adroid.cryptosignal.R

/**
 * Shows the "not financial advice" disclaimer once, the first time the app is ever opened.
 * [DisclaimerGateViewModel.hasSeenDisclaimer] starts as null until DataStore's first emission
 * resolves, so the dialog never flashes on a cold start where it was already acknowledged.
 */
@Composable
fun FirstLaunchDisclaimerGate(viewModel: DisclaimerGateViewModel = hiltViewModel()) {
    val hasSeenDisclaimer by viewModel.hasSeenDisclaimer.collectAsStateWithLifecycle()
    if (hasSeenDisclaimer == false) {
        AlertDialog(
            onDismissRequest = { /* must be explicitly acknowledged */ },
            confirmButton = {
                TextButton(onClick = viewModel::onDisclaimerAcknowledged) {
                    Text("Anladım")
                }
            },
            title = { Text(stringResource(R.string.disclaimer_title)) },
            text = { Text(stringResource(R.string.disclaimer_body)) }
        )
    }
}
