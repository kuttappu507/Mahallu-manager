package com.mahallu.manager.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.BackupEntity
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun BackupScreen(onBack: () -> Unit, viewModel: BackupViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = "Backup & Restore", showBack = true, onBackClick = onBack)
            AppCard(
                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                backgroundColor = colors.primaryIndigo.copy(alpha = 0.06f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(colors.primaryIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Backup, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cloud Backup", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Encrypted backup to Google Drive (AES-256-GCM)",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "Last: " + if (state.lastBackupAt > 0) Formatters.date(state.lastBackupAt) else "Never",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textTertiary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AppButton(
                    text = if (state.isBackingUp) "Backing up..." else "Backup Now",
                    onClick = { viewModel.backupNow() },
                    isLoading = state.isBackingUp,
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = "Auto Backup",
                    onClick = { viewModel.toggleAutoBackup() },
                    modifier = Modifier.weight(1f),
                    style = if (state.autoEnabled) com.mahallu.manager.core.ui.components.AppButtonStyle.Outline
                    else com.mahallu.manager.core.ui.components.AppButtonStyle.Primary
                )
            }
            Spacer(Modifier.height(14.dp))

            Text(
                text = "Backup History",
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.backups, key = { it.id }) { b -> BackupRow(b, onRestore = { viewModel.restore(b.id) }) }
            }

            if (!state.message.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                        .background(colors.successLight)
                        .padding(12.dp)
                ) {
                    Text(state.message ?: "", color = colors.success, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun BackupRow(b: BackupEntity, onRestore: () -> Unit) {
    val colors = LocalMahalluColors.current
    val statusColor = when (b.status) {
        "SUCCESS" -> colors.success
        "FAILED" -> colors.error
        "IN_PROGRESS" -> colors.warning
        else -> colors.textSecondary
    }
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (b.status) {
                        "SUCCESS" -> Icons.Rounded.CheckCircle
                        "FAILED" -> Icons.Rounded.Error
                        else -> Icons.Rounded.Schedule
                    },
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(b.fileName, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${Formatters.date(b.createdAt)} • ${"%.1f".format(b.size / 1024.0)} KB • ${b.type}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary
                )
            }
            if (b.status == "SUCCESS") {
                IconCircleButton(icon = Icons.Rounded.Restore, onClick = onRestore, backgroundColor = colors.primaryIndigo.copy(alpha = 0.10f), tint = colors.primaryIndigo, size = 36.dp, iconSize = 16.dp)
            }
        }
    }
}