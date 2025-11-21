package com.samyak.linkhub.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samyak.linkhub.data.Link
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLinkDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit,
    existingLink: Link? = null,
    categories: List<String> = emptyList()
) {
    var title by remember { mutableStateOf(existingLink?.title ?: "") }
    var url by remember { mutableStateOf(existingLink?.url ?: "") }
    var category by remember { mutableStateOf(existingLink?.category ?: "General") }
    var notes by remember { mutableStateOf(existingLink?.notes ?: "") }
    var urlError by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    
    val allCategories = remember(categories) {
        (categories + listOf("General", "Work", "Personal", "Shopping", "Entertainment", "Education")).distinct()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (existingLink != null) "Edit Link" else "Add New Link",
                style = MaterialTheme.typography.headlineSmall
            ) 
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = url,
                    onValueChange = { 
                        url = it
                        urlError = false
                    },
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = urlError,
                    supportingText = if (urlError) {
                        { Text("Please enter a valid URL") }
                    } else null,
                    singleLine = true
                )
                
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = false,
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        allCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isValidUrl(url) && title.isNotBlank()) {
                        onSave(title, url, category, notes)
                    } else {
                        urlError = true
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun isValidUrl(url: String): Boolean {
    return try {
        URL(url)
        url.startsWith("http://") || url.startsWith("https://")
    } catch (e: Exception) {
        false
    }
}
