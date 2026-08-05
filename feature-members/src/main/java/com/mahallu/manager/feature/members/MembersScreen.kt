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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppSearchBar
import com.mahallu.manager.core.ui.components.ChipPill
import com.mahallu.manager.core.ui.components.EmptyState
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun MembersScreen(
    onAddMember: () -> Unit,
    onMemberClick: (String) -> Unit,
    viewModel: MembersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Page head — title + count + add button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 18.dp, end = 18.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Members",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = state.members.size.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.primaryIndigo,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.primaryIndigo.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
                Spacer(Modifier.weight(1f))
                IconCircleButton(
                    icon = Icons.Rounded.Add,
                    onClick = onAddMember,
                    backgroundColor = Color.White,
                    tint = colors.textPrimary
                )
            }

            AppSearchBar(
                query = state.query,
                onQueryChange = viewModel::setQuery,
                placeholder = "Search members...",
                count = state.members.size
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "MALE", "FEMALE").forEach { filter ->
                    ChipPill(
                        text = filter,
                        selected = state.genderFilter == filter,
                        onClick = { viewModel.setGender(filter) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (state.members.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.Groups,
                    title = "No members found",
                    message = "Add a family first, then add members to it.",
                    actionLabel = "Add Member",
                    onAction = onAddMember
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.members, key = { it.id }) { member ->
                        MemberRow(member, onClick = { onMemberClick(member.id) })
                    }
                    item {
                        Text(
                            text = "Showing ${state.members.size} of ${state.totalCount} members",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textTertiary,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberRow(member: MemberEntity, onClick: () -> Unit) {
    val colors = LocalMahalluColors.current
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        cornerRadius = 17.dp,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar with initials
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (member.gender == "MALE") colors.primaryIndigo.copy(alpha = 0.10f)
                        else colors.purple.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Formatters.initials(member.name),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (member.gender == "MALE") colors.primaryIndigo else colors.purple,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${member.memberNumber} • ${member.relationToHead ?: "Member"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = (member.gender ?: "MEMBER").uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (member.gender == "MALE") colors.primaryIndigo else colors.purple,
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        if (member.gender == "MALE") colors.primaryIndigo.copy(alpha = 0.10f)
                        else colors.purple.copy(alpha = 0.12f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = colors.borderStrong,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}