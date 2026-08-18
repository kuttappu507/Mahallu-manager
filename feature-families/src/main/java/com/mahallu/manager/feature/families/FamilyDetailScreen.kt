package com.mahallu.manager.feature.families

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.ui.components.AnimatedReveal
import com.mahallu.manager.core.ui.components.DetailAction
import com.mahallu.manager.core.ui.components.DetailActionsRow
import com.mahallu.manager.core.ui.components.DetailSectionTitle
import com.mahallu.manager.core.ui.components.InfoGridCard
import com.mahallu.manager.core.ui.components.ProfileHeroCard
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.util.Formatters
import com.mahallu.manager.core.ui.util.dial
import com.mahallu.manager.core.ui.util.whatsapp
import feature.families.feature.families.R

@Composable
fun FamilyDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onMemberClick: (String) -> Unit,
    onStatement: (() -> Unit)? = null
) {
    val vm: FamilyDetailViewModel = hiltViewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                TopAppBar(
                    title = stringResource(R.string.family_detail_title),
                    showBack = true,
                    onBackClick = onBack,
                    trailingActions = {
                        IconButton(onClick = { state.family?.let { onEdit(it.id) } }) {
                            Icon(Icons.Rounded.Edit, contentDescription = stringResource(R.string.family_detail_cd_edit), tint = colors.primaryIndigo)
                        }
                    }
                )
            }
            state.family?.let { fam ->
                item {
                    ProfileHeroCard(
                        title = fam.houseName,
                        initials = Formatters.initials(fam.houseName),
                        chips = listOfNotNull(
                            fam.familyNumber.takeIf { it.isNotBlank() },
                            fam.address?.takeIf { it.isNotBlank() }
                        )
                    )
                }
                item {
                    Spacer(Modifier.height(14.dp))
                    AnimatedReveal {
                        DetailActionsRow(
                            actions = listOf(
                                {
                                    DetailAction(
                                        stringResource(R.string.family_detail_action_call), Icons.Rounded.Call, colors.primaryIndigo,
                                        onClick = { fam.primaryMobile?.takeIf { it.isNotBlank() }?.let { dial(context, it) } }
                                    )
                                },
                                {
                                    DetailAction(
                                        stringResource(R.string.family_detail_action_whatsapp), Icons.Rounded.Chat, Color(0xFF16A34A),
                                        onClick = { fam.primaryMobile?.takeIf { it.isNotBlank() }?.let { whatsapp(context, it) } }
                                    )
                                },
                                { DetailAction(stringResource(R.string.family_detail_action_statement), Icons.Rounded.Receipt, colors.accentCoral, onClick = { onStatement?.invoke() }) }
                            )
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    AnimatedReveal {
                        InfoGridCard(
                            modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                            title = stringResource(R.string.family_detail_contact_title),
                            items = listOf(
                                stringResource(R.string.family_detail_field_number) to fam.familyNumber,
                                stringResource(R.string.family_detail_field_house_name) to fam.houseName,
                                stringResource(R.string.family_detail_field_house_number) to (fam.houseNumber ?: stringResource(R.string.family_detail_not_available)),
                                stringResource(R.string.family_detail_field_ward) to (fam.ward ?: stringResource(R.string.family_detail_not_available)),
                                stringResource(R.string.family_detail_field_area) to (fam.area ?: stringResource(R.string.family_detail_not_available)),
                                stringResource(R.string.family_detail_field_pincode) to (fam.pincode ?: stringResource(R.string.family_detail_not_available)),
                                stringResource(R.string.family_detail_field_primary_mobile) to (fam.primaryMobile ?: stringResource(R.string.family_detail_not_available)),
                                stringResource(R.string.family_detail_field_secondary_mobile) to (fam.secondaryMobile ?: stringResource(R.string.family_detail_not_available)),
                                stringResource(R.string.family_detail_field_email) to (fam.email ?: stringResource(R.string.family_detail_not_available)),
                                stringResource(R.string.family_detail_field_status) to fam.status,
                                stringResource(R.string.family_detail_field_created) to Formatters.date(fam.createdAt)
                            )
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(14.dp))
                    DetailSectionTitle(stringResource(R.string.family_detail_members, state.members.size))
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .clip(RoundedCornerShape(17.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.border, RoundedCornerShape(17.dp))
                            .padding(vertical = 2.dp)
                    ) {
                        state.members.forEachIndexed { index, member ->
                            if (index > 0) {
                                HorizontalDivider(color = colors.border, thickness = 1.dp)
                            }
                            FamilyMemberRow(member, onClick = { onMemberClick(member.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyMemberRow(member: MemberEntity, onClick: () -> Unit) {
    val colors = LocalMahalluColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                color = if (member.gender == "MALE") colors.primaryIndigo else colors.purple,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.labelLarge
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
                text = stringResource(R.string.family_member_relation, member.relationToHead ?: stringResource(R.string.family_member_default_relation), member.occupation ?: stringResource(R.string.family_detail_not_available)),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.family_member_age_suffix, Formatters.calculateAge(member.dateOfBirth)),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = colors.textTertiary,
            modifier = Modifier.size(16.dp)
        )
    }
}
