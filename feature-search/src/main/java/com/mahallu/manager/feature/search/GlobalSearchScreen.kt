package com.mahallu.manager.feature.search

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material.icons.rounded.AccountBalanceWallet
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.AppTextField
import com.mahallu.manager.core.ui.components.EmptyState
import com.mahallu.manager.core.ui.components.IconCircleButton
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters

@Composable
fun GlobalSearchScreen(
    onBack: () -> Unit,
    onFamilyClick: (String) -> Unit,
    onMemberClick: (String) -> Unit,
    onMarriageClick: (String) -> Unit,
    onDeathClick: (String) -> Unit,
    onWelfareClick: (String) -> Unit,
    viewModel: GlobalSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current

    Column(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconCircleButton(icon = Icons.AutoMirrored.Rounded.ArrowBack, onClick = onBack, backgroundColor = colors.background, tint = colors.textPrimary)
            AppTextField(
                value = state.query,
                onValueChange = viewModel::setQuery,
                label = "Search",
                placeholder = "Search families, members, donations...",
                leadingIcon = Icons.Rounded.Search,
                imeAction = ImeAction.Search,
                modifier = Modifier.weight(1f).padding(start = 6.dp)
            )
        }

        when {
            state.query.isBlank() -> EmptyState(
                icon = Icons.Rounded.Search,
                title = "Search across everything",
                message = "Find families, members, donations, marriages, deaths and more."
            )
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Searching...", color = colors.textSecondary)
            }
            state.results.isEmpty -> EmptyState(
                icon = Icons.Rounded.Search,
                title = "No results",
                message = "No matches for \"${state.query}\""
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.results.families.isNotEmpty()) {
                    item { SectionTitle("Families (${state.results.families.size})") }
                    items(state.results.families) { f ->
                        ResultCard(
                            title = "${f.familyNumber} • ${f.houseName}",
                            subtitle = f.address,
                            icon = Icons.Rounded.FamilyRestroom,
                            onClick = { onFamilyClick(f.id) }
                        )
                    }
                }
                if (state.results.members.isNotEmpty()) {
                    item { SectionTitle("Members (${state.results.members.size})") }
                    items(state.results.members) { m ->
                        ResultCard(
                            title = "${m.memberNumber} • ${m.name}",
                            subtitle = "${m.relationToHead ?: "Member"} • ${m.mobile ?: "—"}",
                            icon = Icons.Rounded.Groups,
                            onClick = { onMemberClick(m.id) }
                        )
                    }
                }
                if (state.results.donations.isNotEmpty()) {
                    item { SectionTitle("Donations (${state.results.donations.size})") }
                    items(state.results.donations) { d ->
                        ResultCard(
                            title = "${d.receiptNumber} • ${d.donorName}",
                            subtitle = "${d.category} • ${Formatters.currency(d.amount)}",
                            icon = Icons.Rounded.VolunteerActivism
                        )
                    }
                }
                if (state.results.subscriptions.isNotEmpty()) {
                    item { SectionTitle("Subscriptions (${state.results.subscriptions.size})") }
                    items(state.results.subscriptions) { s ->
                        ResultCard(
                            title = "${s.receiptNumber} • ${s.type}",
                            subtitle = "${Formatters.currency(s.amount)} • ${Formatters.date(s.date)}",
                            icon = Icons.Rounded.ReceiptLong
                        )
                    }
                }
                if (state.results.marriages.isNotEmpty()) {
                    item { SectionTitle("Marriages (${state.results.marriages.size})") }
                    items(state.results.marriages) { m ->
                        ResultCard(
                            title = "${m.registrationNumber} • ${m.brideName} & ${m.groomName}",
                            subtitle = "Nikah: ${Formatters.date(m.nikahDate)}",
                            icon = Icons.Rounded.FamilyRestroom,
                            onClick = { onMarriageClick(m.id) }
                        )
                    }
                }
                if (state.results.deaths.isNotEmpty()) {
                    item { SectionTitle("Deaths (${state.results.deaths.size})") }
                    items(state.results.deaths) { d ->
                        ResultCard(
                            title = "${d.registrationNumber} • ${d.name}",
                            subtitle = "Died: ${Formatters.date(d.dateOfDeath)}",
                            icon = Icons.Rounded.FamilyRestroom,
                            onClick = { onDeathClick(d.id) }
                        )
                    }
                }
                if (state.results.welfare.isNotEmpty()) {
                    item { SectionTitle("Welfare (${state.results.welfare.size})") }
                    items(state.results.welfare) { w ->
                        ResultCard(
                            title = "${w.applicantName} • ${w.category}",
                            subtitle = "${Formatters.currency(w.amount)} • ${w.status}",
                            icon = Icons.Rounded.VolunteerActivism,
                            onClick = { onWelfareClick(w.id) }
                        )
                    }
                }
                if (state.results.finance.isNotEmpty()) {
                    item { SectionTitle("Finance (${state.results.finance.size})") }
                    items(state.results.finance) { f ->
                        ResultCard(
                            title = "${f.category} • ${f.type}",
                            subtitle = "${Formatters.currency(f.amount)} • ${f.description}",
                            icon = Icons.Rounded.AccountBalanceWallet
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    val colors = LocalMahalluColors.current
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = colors.textSecondary,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
private fun ResultCard(title: String, subtitle: String, icon: ImageVector, onClick: (() -> Unit)? = null) {
    val colors = LocalMahalluColors.current
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.primaryIndigo.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = colors.primaryIndigo, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary, maxLines = 1)
            }
        }
    }
}