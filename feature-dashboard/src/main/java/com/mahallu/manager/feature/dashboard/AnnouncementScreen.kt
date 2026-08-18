package com.mahallu.manager.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.RadiusLg
import feature.dashboard.feature.dashboard.R

/**
 * Announcement composer — mirrors the Masjidi "New announcement" sheet.
 * A simple form (no persistence yet) that pops back once sent.
 */
@Composable
fun AnnouncementScreen(
    onDone: () -> Unit,
    viewModel: AnnouncementViewModel = hiltViewModel()
) {
    val colors = LocalMahalluColors.current
    val memberCount by viewModel.memberCount.collectAsStateWithLifecycle()
    var message by rememberSaveable { mutableStateOf("") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val empty = message.isBlank()

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = stringResource(R.string.announcement_title),
                showBack = true,
                onBackClick = onDone
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.announcement_sent_to, memberCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(14.dp))
                AppTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = stringResource(R.string.announcement_message_label),
                    placeholder = stringResource(R.string.announcement_message_placeholder),
                    singleLine = false,
                    maxLines = 6,
                    isError = attempted && empty,
                    errorMessage = if (attempted && empty) stringResource(R.string.announcement_error_empty) else null
                )
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusLg.value.dp))
                        .background(colors.primaryIndigo.copy(alpha = 0.08f))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.announcement_preview),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.primaryIndigo,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = message.ifBlank { stringResource(R.string.announcement_preview_placeholder) },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (message.isBlank()) colors.textTertiary else colors.textPrimary
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                AppButton(
                    text = stringResource(R.string.announcement_send),
                    onClick = {
                        if (empty) attempted = true else onDone()
                    },
                    leadingIcon = Icons.Rounded.Campaign
                )
            }
        }
    }
}
