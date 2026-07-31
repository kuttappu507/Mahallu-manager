package com.mahallu.manager.feature.members

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCard
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.components.InfoRow
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun MemberDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onAddCollection: (String) -> Unit,
    onGenerateCertificate: (com.mahallu.manager.core.database.entity.MemberEntity) -> Unit = {}
) {
    val vm: MemberDetailViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                TopAppBar(
                    title = "Member Details",
                    showBack = true,
                    onBackClick = onBack,
                    trailingActions = {
                        IconCircleButton(
                            icon = Icons.Rounded.Edit,
                            onClick = { state.member?.let { onEdit(it.id) } }
                        )
                    }
                )
            }
            state.member?.let { m ->
                item { MemberHeroCard(m) }
                item {
                    Spacer(Modifier.height(12.dp))
                    AppCard(
                        modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
                        contentPadding = PaddingValues(14.dp)
                    ) {
                        androidx.compose.foundation.layout.Column {
                            AppButton(
                                text = "Record Subscription / Collection",
                                onClick = { onAddCollection(m.id) },
                                leadingIcon = Icons.Rounded.MonetizationOn
                            )
                            Spacer(Modifier.height(10.dp))
                            AppButton(
                                text = "Generate Membership Certificate",
                                onClick = { onGenerateCertificate(m) },
                                leadingIcon = androidx.compose.material.icons.Icons.Rounded.PictureAsPdf
                            )
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    SectionTitle("Profile")
                    AppCard(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        InfoRow("Member ID", m.memberNumber)
                        InfoRow("Name", m.name)
                        if (!m.arabicName.isNullOrBlank()) InfoRow("Arabic Name", m.arabicName!!)
                        InfoRow("Gender", m.gender)
                        InfoRow("Date of Birth", "${Formatters.date(m.dateOfBirth)} (${Formatters.calculateAge(m.dateOfBirth)}y)")
                        InfoRow("Marital Status", m.maritalStatus ?: "—")
                        InfoRow("Relation to Head", m.relationToHead ?: "—")
                        InfoRow("Blood Group", m.bloodGroup ?: "—")
                        InfoRow("Nationality", m.nationality ?: "—")
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    SectionTitle("Education & Work")
                    AppCard(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        InfoRow("Education", m.education ?: "—")
                        InfoRow("Occupation", m.occupation ?: "—")
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    SectionTitle("Contact")
                    AppCard(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        InfoRow("Mobile", m.mobile ?: "—")
                        InfoRow("Email", m.email ?: "—")
                        InfoRow("Emergency Contact", listOfNotNull(m.emergencyContactName, m.emergencyContactNumber).joinToString(" • ").ifBlank { "—" })
                        InfoRow("Address", m.address ?: state.family?.address ?: "—")
                    }
                }
                if (!m.notes.isNullOrBlank()) {
                    val notes = m.notes.orEmpty()
                    item {
                        Spacer(Modifier.height(12.dp))
                        SectionTitle("Notes")
                        AppCard(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                            Text(text = notes, color = colors.textSecondary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                state.family?.let { fam ->
                    item {
                        Spacer(Modifier.height(12.dp))
                        SectionTitle("Family")
                        AppCard(
                            modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(),
                            onClick = { /* navigate to family */ },
                            contentPadding = PaddingValues(14.dp)
                        ) {
                            InfoRow("Family Number", fam.familyNumber)
                            InfoRow("House Name", fam.houseName)
                            InfoRow("Address", fam.address)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberHeroCard(member: com.mahallu.manager.core.database.entity.MemberEntity) {
    val colors = LocalMahalluColors.current
    val accent = if (member.gender == "MALE") colors.info else colors.accentCoral
    AppCard(
        modifier = Modifier.padding(14.dp).fillMaxWidth(),
        contentPadding = PaddingValues(18.dp),
        backgroundColor = accent.copy(alpha = 0.06f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                if (!member.arabicName.isNullOrBlank()) {
                    Text(
                        text = member.arabicName!!,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${member.memberNumber} • ${member.relationToHead ?: "Member"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = accent,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    val colors = LocalMahalluColors.current
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = colors.textPrimary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}