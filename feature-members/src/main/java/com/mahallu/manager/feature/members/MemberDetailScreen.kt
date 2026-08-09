package com.mahallu.manager.feature.members

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.MemberEntity
import feature.members.feature.members.R
import com.mahallu.manager.core.ui.components.AnimatedReveal
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.DetailAction
import com.mahallu.manager.core.ui.components.DetailActionsRow
import com.mahallu.manager.core.ui.components.InfoGridCard
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors
import com.mahallu.manager.core.ui.theme.PrimaryIndigo
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
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item {
                TopAppBar(
                    title = stringResource(R.string.member_detail_title),
                    showBack = true,
                    onBackClick = onBack,
                    trailingActions = {
                        androidx.compose.material3.IconButton(onClick = { state.member?.let { onEdit(it.id) } }) {
                            Icon(Icons.Rounded.Edit, contentDescription = stringResource(R.string.cd_edit), tint = colors.primaryIndigo)
                        }
                    }
                )
            }
            state.member?.let { m ->
                item { MemberHeroCard(m) }
                item {
                    Spacer(Modifier.height(14.dp))
                    AnimatedReveal {
                        DetailActionsRow(
                            actions = listOf(
                                {
                                    DetailAction(
                                        stringResource(R.string.member_detail_call), Icons.Rounded.Call, colors.primaryIndigo,
                                        onClick = { m.mobile?.takeIf { it.isNotBlank() }?.let { dial(context, it) } }
                                    )
                                },
                                {
                                    DetailAction(
                                        stringResource(R.string.member_detail_whatsapp), Icons.Rounded.Chat, Color(0xFF16A34A),
                                        onClick = { m.mobile?.takeIf { it.isNotBlank() }?.let { whatsapp(context, it) } }
                                    )
                                },
                                { DetailAction(stringResource(R.string.cd_edit), Icons.Rounded.Edit, colors.accentCoral, onClick = { onEdit(m.id) }) }
                            )
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    AnimatedReveal {
                        AppCard(
                            modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                            contentPadding = PaddingValues(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                AppButton(
                                    text = stringResource(R.string.member_detail_record_collection),
                                    onClick = { onAddCollection(m.id) },
                                    leadingIcon = Icons.Rounded.MonetizationOn
                                )
                                AppButton(
                                    text = stringResource(R.string.member_detail_generate_certificate),
                                    onClick = { onGenerateCertificate(m) },
                                    leadingIcon = Icons.Rounded.PictureAsPdf
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    AnimatedReveal {
                        InfoGridCard(
                            modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                            title = stringResource(R.string.member_detail_contact),
                            items = listOf(
                                stringResource(R.string.member_field_phone) to (m.mobile ?: "—"),
                                stringResource(R.string.member_field_email) to (m.email ?: "—"),
                                stringResource(R.string.member_field_emergency) to listOfNotNull(m.emergencyContactName, m.emergencyContactNumber).joinToString(" • ").ifBlank { "—" },
                                stringResource(R.string.member_field_address) to (m.address ?: state.family?.address ?: "—")
                            )
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    AnimatedReveal {
                        InfoGridCard(
                            modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                            title = stringResource(R.string.member_detail_profile),
                            items = listOf(
                                stringResource(R.string.member_field_member_id) to m.memberNumber,
                                stringResource(R.string.member_field_gender) to m.gender,
                                stringResource(R.string.member_field_dob) to stringResource(R.string.member_detail_dob_age, Formatters.date(m.dateOfBirth), Formatters.calculateAge(m.dateOfBirth)),
                                stringResource(R.string.member_field_marital_status) to (m.maritalStatus ?: "—"),
                                stringResource(R.string.member_field_relation) to (m.relationToHead ?: "—"),
                                stringResource(R.string.member_field_blood_group) to (m.bloodGroup ?: "—"),
                                stringResource(R.string.member_field_nationality) to (m.nationality ?: "—"),
                                stringResource(R.string.member_field_education) to (m.education ?: "—"),
                                stringResource(R.string.member_field_occupation) to (m.occupation ?: "—")
                            )
                        )
                    }
                }
                if (!m.notes.isNullOrBlank()) {
                    val notes = m.notes.orEmpty()
                    item {
                        Spacer(Modifier.height(12.dp))
                        AnimatedReveal {
                            InfoGridCard(
                                modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                                title = stringResource(R.string.member_field_notes),
                                items = listOf(
                                    stringResource(R.string.member_field_notes) to notes
                                )
                            )
                        }
                    }
                }
                state.family?.let { fam ->
                    item {
                        Spacer(Modifier.height(12.dp))
                        AnimatedReveal {
                            InfoGridCard(
                                modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                                title = stringResource(R.string.member_field_family),
                                items = listOf(
                                    stringResource(R.string.member_field_family_number) to fam.familyNumber,
                                    stringResource(R.string.member_field_house_name) to fam.houseName,
                                    stringResource(R.string.member_field_address) to fam.address
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberHeroCard(member: MemberEntity) {
    val colors = LocalMahalluColors.current
    val accent = if (member.gender == "MALE") colors.primaryIndigo else colors.rose
    Box(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = accent.copy(alpha = 0.35f), spotColor = accent.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = if (member.gender == "MALE")
                        listOf(PrimaryIndigo, colors.primaryDark, Color(0xFF7C3AED))
                    else
                        listOf(Color(0xFFBE185D), Color(0xFFF43F5E), Color(0xFFFB7185))
                )
            )
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Formatters.initials(member.name),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(13.dp))
            Text(
                text = member.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroChip(member.memberNumber)
                Spacer(Modifier.width(6.dp))
                HeroChip(member.relationToHead ?: stringResource(R.string.member_role))
                if (!member.occupation.isNullOrBlank()) {
                    Spacer(Modifier.width(6.dp))
                    HeroChip(member.occupation.orEmpty())
                }
            }
        }
    }
}

@Composable
private fun HeroChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(9.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 132.dp)
        )
    }
}

private fun dial(context: android.content.Context, number: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number.trim()}")))
    }
}

private fun whatsapp(context: android.content.Context, number: String) {
    runCatching {
        val normalized = number.replace(Regex("[^\\d]"), "").trimStart('0')
        val uri = if (normalized.startsWith("91")) normalized else "91$normalized"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$uri"))
        intent.setPackage("com.whatsapp")
        context.startActivity(intent)
    }
}
