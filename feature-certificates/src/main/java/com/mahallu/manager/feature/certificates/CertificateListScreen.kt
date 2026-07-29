package com.mahallu.manager.feature.certificates

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
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

private data class CertificateType(
    val type: String,
    val label: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun CertificateListScreen(
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    viewModel: CertificateListViewModel = hiltViewModel()
) {
    val colors = LocalMahalluColors.current
    val certificates = listOf(
        CertificateType("MEMBERSHIP", "Membership Certificate", "Generate membership certificate", Icons.Rounded.Person),
        CertificateType("RESIDENCE", "Residence Certificate", "Generate residence certificate", Icons.Rounded.Home),
        CertificateType("MARRIAGE", "Marriage Certificate", "Generate marriage certificate", Icons.Rounded.Favorite),
        CertificateType("DEATH", "Death Certificate", "Generate death certificate", Icons.Rounded.Description)
    )

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = "Certificates", showBack = true, onBackClick = onBack)
            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(certificates) { cert ->
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSelect(cert.type) },
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(colors.primaryIndigo.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(cert.icon, contentDescription = null, tint = colors.primaryIndigo)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cert.label, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                                Text(cert.description, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                            }
                            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = colors.textTertiary)
                        }
                    }
                }
            }
        }
    }
}