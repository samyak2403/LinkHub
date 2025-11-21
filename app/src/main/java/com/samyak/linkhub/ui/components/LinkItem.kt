package com.samyak.linkhub.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.samyak.linkhub.data.Link

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LinkItem(
    link: Link,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favicon placeholder
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = link.title.firstOrNull()?.uppercase() ?: "L",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = link.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (link.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (link.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (link.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = link.url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (link.category.isNotBlank() || link.clickCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (link.category.isNotBlank()) {
                            AssistChip(
                                onClick = {},
                                label = { Text(link.category, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        if (link.clickCount > 0) {
                            AssistChip(
                                onClick = {},
                                label = { Text("${link.clickCount} views", style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
