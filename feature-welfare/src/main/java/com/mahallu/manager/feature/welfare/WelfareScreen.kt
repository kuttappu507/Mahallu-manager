package com.mahallu.manager.feature.welfare

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.HealthAndSafety
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
import com.mahallu.manager.core.database.entity.WelfareEntity
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.FabAdd
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun WelfareScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onItemClick: (String) -> Unit,
    viewModel: WelfareListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCircleButton(icon = Icons.Rounded.ArrowBack, onClick = onBack, backgroundColor = colors.background, tint = colors.textPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Welfare Management", style = MaterialTheme.typography.headlineSmall, color = colors.textPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                FabAdd(onClick = onAdd)
            }

            AppCard(
                modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
                backgroundColor = colors.success.copy(alpha = 0.06f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Column {
                    Text("Total Disbursed", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                    Text(
                        text = Formatters.currency(state.totalDisbursed),
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("ALL", "PENDING", "APPROVED", "DISBURSED", "REJECTED")) { s ->
                    ChipPill(text = s, selected = state.statusFilter == s, onClick = { viewModel.setStatus(s) })
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.items, key = { it.id }) { w -> WelfareRow(w, onClick = { onItemClick(w.id) }) }
            }
        }
    }
}

@Composable
private fun WelfareRow(w: WelfareEntity, onClick: () -> Unit) {
    val colors = LocalMahalluColors.current
    val statusColor = when (w.status) {
        "PENDING" -> colors.warning
        "APPROVED" -> colors.info
        "DISBURSED" -> colors.success
        "REJECTED" -> colors.error
        else -> colors.textSecondary
    }
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.success.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.HealthAndSafety, contentDescription = null, tint = colors.success)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(w.applicantName, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    text = w.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                Text(Formatters.date(w.date), style = MaterialTheme.typography.labelSmall, color = colors.textTertiary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(Formatters.currency(w.amount), style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.10f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = w.status, style = MaterialTheme.typography.labelSmall, color = statusColor)
                }
            }
        }
    }
}