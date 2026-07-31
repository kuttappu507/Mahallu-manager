package com.mahallu.manager.feature.families

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Phone
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
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.components.InfoRow
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun FamilyDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onMemberClick: (String) -> Unit
) {
    val vm: FamilyDetailViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                TopAppBar(
                    title = "Family Details",
                    showBack = true,
                    onBackClick = onBack,
                    trailingActions = {
                        IconCircleButton(
                            icon = Icons.Rounded.Edit,
                            onClick = { state.family?.let { onEdit(it.id) } }
                        )
                    }
                )
            }
            state.family?.let { fam ->
                item { FamilyHeroCard(fam) }
                item {
                    Spacer(Modifier.height(12.dp))
                    SectionTitle("Contact Information")
                    AppCard(modifier = Modifier.padding(horizontal = 14.dp).fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        InfoRow("Family Number", fam.familyNumber)
                        InfoRow("House Name", fam.houseName)
                        InfoRow("House Number", fam.houseNumber ?: "—")
                        InfoRow("Ward", fam.ward ?: "—")
                        InfoRow("Area", fam.area ?: "—")
                        InfoRow("Pincode", fam.pincode ?: "—")
                        InfoRow("Address", fam.address)
                        InfoRow("Primary Mobile", fam.primaryMobile ?: "—")
                        InfoRow("Secondary Mobile", fam.secondaryMobile ?: "—")
                        InfoRow("Email", fam.email ?: "—")
                        InfoRow("Status", fam.status)
                        InfoRow("Created", Formatters.date(fam.createdAt))
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    SectionTitle("Members (${state.members.size})")
                }
                items(state.members, key = { it.id }) { member ->
                    MemberRow(member, onClick = { onMemberClick(member.id) })
                }
            }
        }
    }
}

@Composable
private fun FamilyHeroCard(family: com.mahallu.manager.core.database.entity.FamilyEntity) {
    val colors = LocalMahalluColors.current
    AppCard(
        modifier = Modifier.padding(14.dp).fillMaxWidth(),
        contentPadding = PaddingValues(18.dp),
        backgroundColor = colors.primaryIndigo.copy(alpha = 0.06f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(colors.primaryIndigo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.FamilyRestroom,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = family.houseName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = family.familyNumber,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.primaryIndigo,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = family.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
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

@Composable
private fun MemberRow(member: MemberEntity, onClick: () -> Unit) {
    val colors = LocalMahalluColors.current
    AppCard(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp).fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.info.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.take(1).uppercase(),
                    color = colors.info,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "${member.relationToHead ?: "Member"} • ${member.occupation ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${Formatters.calculateAge(member.dateOfBirth)}y",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary
            )
        }
    }
}