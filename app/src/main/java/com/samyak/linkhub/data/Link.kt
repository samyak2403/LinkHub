package com.samyak.linkhub.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class Link(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val createdAt: Long = System.currentTimeMillis(),
    val category: String = "General",
    val isFavorite: Boolean = false,
    val clickCount: Int = 0,
    val lastOpened: Long = 0,
    val notes: String = "",
    val faviconUrl: String = ""
)
