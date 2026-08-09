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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import feature.certificates.feature.certificates.R

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
        CertificateType("MEMBERSHIP", stringResource(R.string.cert_membership_title), stringResource(R.string.cert_membership_description), Icons.Rounded.Person),
        CertificateType("RESIDENCE", stringResource(R.string.cert_residence_title), stringResource(R.string.cert_residence_description), Icons.Rounded.Home),
        CertificateType("MARRIAGE", stringResource(R.string.cert_marriage_title), stringResource(R.string.cert_marriage_description), Icons.Rounded.Favorite),
        CertificateType("DEATH", stringResource(R.string.cert_death_title), stringResource(R.string.cert_death_description), Icons.Rounded.Description)
    )

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            CertificatePageHead(
                title = stringResource(R.string.cert_certificates_title),
                count = stringResource(R.string.cert_issued_count, 86),
                showBack = true,
                onBack = onBack,
                showAdd = true,
                onAdd = { onSelect("MEMBERSHIP") }
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.primaryIndigo.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(cert.icon, contentDescription = null, tint = colors.primaryIndigo, modifier = Modifier.size(18.dp))
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

@Composable
private fun CertificatePageHead(
    title: String,
    count: String,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    showAdd: Boolean = false,
    onAdd: (() -> Unit)? = null
) {
    val colors = LocalMahalluColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp, start = 14.dp, end = 14.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack && onBack != null) {
            IconCircleButton(icon = Icons.AutoMirrored.Rounded.ArrowBack, onClick = onBack, backgroundColor = Color.White, tint = colors.textPrimary)
            Spacer(Modifier.width(8.dp))
        }
        Text(title, style = MaterialTheme.typography.headlineMedium, color = colors.textPrimary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(10.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall,
            color = colors.primaryIndigo,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(colors.indigoTint)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
        Spacer(Modifier.weight(1f))
        if (showAdd) {
            IconCircleButton(icon = Icons.Rounded.Add, onClick = { onAdd?.invoke() }, backgroundColor = Color.White, tint = colors.textPrimary)
        }
    }
}
