package com.samyak.linkhub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samyak.linkhub.data.Link
import com.samyak.linkhub.ui.LinkViewModel
import com.samyak.linkhub.ui.components.AddLinkDialog
import com.samyak.linkhub.ui.components.LinkItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LinkViewModel,
    onLinkClick: (Link) -> Unit
) {
    val links by viewModel.filteredLinks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LinkHub") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add link")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search links...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Links list
            if (links.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No links saved yet" else "No matching links",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(links, key = { it.id }) { link ->
                        LinkItem(
                            link = link,
                            onClick = { onLinkClick(link) }
                        )
                    }
                }
            }
        }
    }
    
    if (showDialog) {
        AddLinkDialog(
            onDismiss = { showDialog = false },
            onSave = { title, url ->
                viewModel.insert(Link(title = title, url = url))
                showDialog = false
            }
        )
    }
}
