package com.mahallu.manager.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.RadiusLg

/**
 * Announcement composer — mirrors the Masjidi "New announcement" sheet.
 * A simple form (no persistence yet) that pops back once sent.
 */
@Composable
fun AnnouncementScreen(
    onDone: () -> Unit,
    memberCount: Int = 0
) {
    val colors = LocalMahalluColors.current
    var message by rememberSaveable { mutableStateOf("") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val empty = message.isBlank()

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = "New announcement",
                showBack = true,
                onBackClick = onDone
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Sent to all $memberCount members via push & WhatsApp.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Spacer(Modifier.height(14.dp))
                AppTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = "Message",
                    placeholder = "e.g. Taraweeh starts at 9:30 PM tonight, in sha' Allah…",
                    singleLine = false,
                    maxLines = 6,
                    isError = attempted && empty,
                    errorMessage = if (attempted && empty) "Write a message first" else null
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
                            text = "Preview",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.primaryIndigo,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = message.ifBlank { "Your announcement will appear here…" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (message.isBlank()) colors.textTertiary else colors.textPrimary
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                AppButton(
                    text = "Send announcement",
                    onClick = {
                        if (empty) attempted = true else onDone()
                    },
                    leadingIcon = Icons.Rounded.Campaign
                )
            }
        }
    }
}
