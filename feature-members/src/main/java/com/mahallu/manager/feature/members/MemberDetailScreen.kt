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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahallu.manager.core.database.entity.MemberEntity
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
                    title = "Member Details",
                    showBack = true,
                    onBackClick = onBack,
                    trailingActions = {
                        androidx.compose.material3.IconButton(onClick = { state.member?.let { onEdit(it.id) } }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = colors.primaryIndigo)
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
                                        "Call", Icons.Rounded.Call, colors.primaryIndigo,
                                        onClick = { m.mobile?.takeIf { it.isNotBlank() }?.let { dial(context, it) } }
                                    )
                                },
                                {
                                    DetailAction(
                                        "WhatsApp", Icons.Rounded.Chat, Color(0xFF16A34A),
                                        onClick = { m.mobile?.takeIf { it.isNotBlank() }?.let { whatsapp(context, it) } }
                                    )
                                },
                                { DetailAction("Edit", Icons.Rounded.Edit, colors.accentCoral, onClick = { onEdit(m.id) }) }
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
                                    text = "Record Subscription / Collection",
                                    onClick = { onAddCollection(m.id) },
                                    leadingIcon = Icons.Rounded.MonetizationOn
                                )
                                AppButton(
                                    text = "Generate Membership Certificate",
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
                            title = "Contact",
                            items = listOf(
                                "Phone" to (m.mobile ?: "—"),
                                "Email" to (m.email ?: "—"),
                                "Emergency" to listOfNotNull(m.emergencyContactName, m.emergencyContactNumber).joinToString(" • ").ifBlank { "—" },
                                "Address" to (m.address ?: state.family?.address ?: "—")
                            )
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    AnimatedReveal {
                        InfoGridCard(
                            modifier = Modifier.padding(horizontal = 18.dp).fillMaxWidth(),
                            title = "Profile",
                            items = listOf(
                                "Member ID" to m.memberNumber,
                                "Gender" to m.gender,
                                "Date of Birth" to "${Formatters.date(m.dateOfBirth)} (${Formatters.calculateAge(m.dateOfBirth)}y)",
                                "Marital Status" to (m.maritalStatus ?: "—"),
                                "Relation to Head" to (m.relationToHead ?: "—"),
                                "Blood Group" to (m.bloodGroup ?: "—"),
                                "Nationality" to (m.nationality ?: "—"),
                                "Education" to (m.education ?: "—"),
                                "Occupation" to (m.occupation ?: "—")
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
                                title = "Notes",
                                items = listOf(
                                    "Notes" to notes
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
                                title = "Family",
                                items = listOf(
                                    "Family Number" to fam.familyNumber,
                                    "House Name" to fam.houseName,
                                    "Address" to fam.address
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
                maxLines = 1
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroChip(member.memberNumber)
                Spacer(Modifier.width(6.dp))
                HeroChip(member.relationToHead ?: "Member")
                if (!member.occupation.isNullOrBlank()) {
                    Spacer(Modifier.width(6.dp))
                    HeroChip(member.occupation.orEmpty())
                }
            }
        }
    }
}

@Composable
private fun HeroChip(text: String) {    Box(
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
            fontWeight = FontWeight.ExtraBold
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
