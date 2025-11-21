package com.samyak.linkhub.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samyak.linkhub.data.Link
import com.samyak.linkhub.ui.FilterOption
import com.samyak.linkhub.ui.LinkViewModel
import com.samyak.linkhub.ui.SortOption
import com.samyak.linkhub.ui.components.AddLinkDialog
import com.samyak.linkhub.ui.components.LinkItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: LinkViewModel,
    onLinkClick: (Link) -> Unit
) {
    val links by viewModel.filteredLinks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    
    var editingLink by remember { mutableStateOf<Link?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var linkToShowOptions by remember { mutableStateOf<Link?>(null) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Set filter to favorites when this screen is displayed
    LaunchedEffect(Unit) {
        viewModel.updateFilterOption(FilterOption.FAVORITES)
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search favorites...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Favorites list
            if (links.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (searchQuery.isBlank()) "No favorite links yet" else "No matching favorites",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (searchQuery.isBlank()) {
                            Text(
                                text = "Star links to add them to favorites",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(links, key = { it.id }) { link ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.delete(link)
                                    scope.launch {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "Link deleted",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.insert(link)
                                        }
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        )
                        
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            enableDismissFromStartToEnd = false
                        ) {
                            LinkItem(
                                link = link,
                                onClick = {
                                    viewModel.incrementClickCount(link.id)
                                    onLinkClick(link)
                                },
                                onLongClick = {
                                    linkToShowOptions = link
                                    showOptionsMenu = true
                                },
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(link)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Edit Dialog
    if (showDialog) {
        AddLinkDialog(
            onDismiss = { 
                showDialog = false
                editingLink = null
            },
            onSave = { title, url, category, notes ->
                if (editingLink != null) {
                    viewModel.update(
                        editingLink!!.copy(
                            title = title,
                            url = url,
                            category = category,
                            notes = notes
                        )
                    )
                }
                showDialog = false
                editingLink = null
            },
            existingLink = editingLink,
            categories = categories
        )
    }
    
    // Options Menu
    if (showOptionsMenu && linkToShowOptions != null) {
        AlertDialog(
            onDismissRequest = { showOptionsMenu = false },
            title = { Text("Link Options") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            editingLink = linkToShowOptions
                            showDialog = true
                            showOptionsMenu = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit")
                    }
                    
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("URL", linkToShowOptions!!.url)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "URL copied", Toast.LENGTH_SHORT).show()
                            showOptionsMenu = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Copy URL")
                    }
                    
                    TextButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${linkToShowOptions!!.title}\n${linkToShowOptions!!.url}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share link"))
                            showOptionsMenu = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Share")
                    }
                    
                    TextButton(
                        onClick = {
                            viewModel.toggleFavorite(linkToShowOptions!!)
                            showOptionsMenu = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Removed from favorites")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.StarBorder, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Remove from Favorites")
                    }
                    
                    TextButton(
                        onClick = {
                            viewModel.delete(linkToShowOptions!!)
                            showOptionsMenu = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Link deleted")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showOptionsMenu = false }) {
                    Text("Close")
                }
            }
        )
    }
}
