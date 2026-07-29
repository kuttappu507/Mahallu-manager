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
import androidx.compose.material.icons.rounded.Check
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
import com.mahallu.manager.core.ui.components.AppButton
import com.mahallu.manager.core.ui.components.AppCard
import com.mahallu.manager.core.ui.components.TopAppBar
import com.mahallu.manager.core.ui.theme.LocalMahalluColors

@Composable
fun MembershipCertificateScreen(onBack: () -> Unit, viewModel: CertificateFormViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = "Membership Certificate", showBack = true, onBackClick = onBack)
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
                    Column {
                        Text("Al Noor Mahallu", style = MaterialTheme.typography.headlineSmall, color = colors.primaryIndigo, fontWeight = FontWeight.Bold)
                        Text("Membership Certificate", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text("This is to certify that the following member is a registered member of our Mahallu.", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        Spacer(Modifier.height(20.dp))
                        Text("Member Details", style = MaterialTheme.typography.titleSmall, color = colors.textPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text("Name: ${state.memberName.ifBlank { "[Member Name]" }}", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        Text("Father: ${state.fatherName.ifBlank { "[Father Name]" }}", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        Text("Address: ${state.address.ifBlank { "[Address]" }}", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        Text("Member ID: ${state.memberNumber.ifBlank { "[Member ID]" }}", style = MaterialTheme.typography.bodyMedium, color = colors.textPrimary)
                        Spacer(Modifier.height(20.dp))
                        Text("Issued on: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                    }
                }
            }
            AppButton(text = "Generate PDF", onClick = { viewModel.generate("MEMBERSHIP") }, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun ResidenceCertificateScreen(onBack: () -> Unit, viewModel: CertificateFormViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = "Residence Certificate", showBack = true, onBackClick = onBack)
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
                    Column {
                        Text("Al Noor Mahallu", style = MaterialTheme.typography.headlineSmall, color = colors.primaryIndigo, fontWeight = FontWeight.Bold)
                        Text("Residence Certificate", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text("This is to certify that the following person is a resident of the area under this Mahallu.", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        Spacer(Modifier.height(20.dp))
                        Text("Name: ${state.memberName.ifBlank { "[Name]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Father: ${state.fatherName.ifBlank { "[Father Name]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Address: ${state.address.ifBlank { "[Address]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Ward: ${state.ward.ifBlank { "[Ward]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Pincode: ${state.pincode.ifBlank { "[Pincode]" }}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(20.dp))
                        Text("Issued on: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}", style = MaterialTheme.typography.labelLarge, color = colors.textSecondary)
                    }
                }
            }
            AppButton(text = "Generate PDF", onClick = { viewModel.generate("RESIDENCE") }, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun MarriageCertificateScreen(onBack: () -> Unit, viewModel: CertificateFormViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = "Marriage Certificate", showBack = true, onBackClick = onBack)
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
                    Column {
                        Text("Al Noor Mahallu", style = MaterialTheme.typography.headlineSmall, color = colors.primaryIndigo, fontWeight = FontWeight.Bold)
                        Text("Marriage Certificate", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text("This is to certify that the Nikah ceremony between the following was solemnized as per Islamic rites.", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        Spacer(Modifier.height(20.dp))
                        Text("Bride: ${state.brideName.ifBlank { "[Bride Name]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Groom: ${state.groomName.ifBlank { "[Groom Name]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Nikah Date: ${state.date.ifBlank { "[Date]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Witnesses: ${state.witnesses.ifBlank { "[Witnesses]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Registration: ${state.registrationNumber.ifBlank { "[Reg #]" }}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            AppButton(text = "Generate PDF", onClick = { viewModel.generate("MARRIAGE") }, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun DeathCertificateScreen(onBack: () -> Unit, viewModel: CertificateFormViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = LocalMahalluColors.current
    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = "Death Certificate", showBack = true, onBackClick = onBack)
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
                    Column {
                        Text("Al Noor Mahallu", style = MaterialTheme.typography.headlineSmall, color = colors.primaryIndigo, fontWeight = FontWeight.Bold)
                        Text("Death Certificate", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
                        Spacer(Modifier.height(12.dp))
                        Text("This is to certify the death of the following person as recorded in our register.", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
                        Spacer(Modifier.height(20.dp))
                        Text("Name: ${state.deceasedName.ifBlank { "[Name]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Father: ${state.fatherName.ifBlank { "[Father Name]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Date of Death: ${state.date.ifBlank { "[Date]" }}", style = MaterialTheme.typography.bodyMedium)
                        Text("Registration: ${state.registrationNumber.ifBlank { "[Reg #]" }}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            AppButton(text = "Generate PDF", onClick = { viewModel.generate("DEATH") }, modifier = Modifier.padding(16.dp))
        }
    }
}