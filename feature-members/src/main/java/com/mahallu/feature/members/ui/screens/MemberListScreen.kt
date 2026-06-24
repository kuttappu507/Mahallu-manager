package com.mahallu.feature.members.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mahallu.core.ui.theme.MahalluTheme
import com.mahallu.feature.members.ui.viewmodel.MemberViewModel
import com.mahallu.domain.model.MemberWithFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListScreen(
    viewModel: MemberViewModel = hiltViewModel(),
    onMemberClick: (Int) -> Unit,
    onAddMember: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    MahalluTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        if (showSearch) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { 
                                    searchQuery = it
                                    viewModel.searchMembers(it)
                                },
                                placeholder = { Text("Search members...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MahalluTheme.colors.primary,
                                    unfocusedBorderColor = MahalluTheme.colors.border
                                )
                            )
                        } else {
                            Text(
                                "Members",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearch = !showSearch }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = onAddMember) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Member"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MahalluTheme.colors.background
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddMember,
                    containerColor = MahalluTheme.colors.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Member")
                }
            }
        ) { paddingValues ->
            when (uiState) {
                is MemberUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MahalluTheme.colors.primary)
                    }
                }
                is MemberUiState.Success -> {
                    val members = (uiState as MemberUiState.Success).members
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(members, key = { it.member.id }) { memberWithFamily ->
                            MemberCard(
                                memberWithFamily = memberWithFamily,
                                onClick = { onMemberClick(memberWithFamily.member.id) }
                            )
                        }
                        if (members.isEmpty()) {
                            item {
                                EmptyStateView(
                                    title = "No Members Found",
                                    description = "Add your first member to get started"
                                )
                            }
                        }
                    }
                }
                is MemberUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (uiState as MemberUiState.Error).message,
                            color = MahalluTheme.colors.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberCard(
    memberWithFamily: MemberWithFamily,
    onClick: () -> Unit
) {
    val member = memberWithFamily.member
    val family = memberWithFamily.family

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MahalluTheme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo
            AsyncImage(
                model = member.photoUri,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MahalluTheme.colors.border),
                placeholder = android.R.drawable.ic_menu_gallery
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MahalluTheme.colors.textPrimary
                )
                
                if (member.arabicName.isNotBlank()) {
                    Text(
                        text = member.arabicName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MahalluTheme.colors.textSecondary
                    )
                }
                
                Text(
                    text = "${member.age} years • ${member.gender}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MahalluTheme.colors.textSecondary
                )
                
                family?.let {
                    Text(
                        text = "Family: ${it.houseName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MahalluTheme.colors.primary
                    )
                }
            }

            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (member.isActive) MahalluTheme.colors.success 
                        else MahalluTheme.colors.error
                    )
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MahalluTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MahalluTheme.colors.textSecondary
        )
    }
}
