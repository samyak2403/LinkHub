package com.samyak.linkhub.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {
    @Query("SELECT * FROM links ORDER BY createdAt DESC")
    fun getAllLinks(): Flow<List<Link>>
    
    @Query("SELECT * FROM links WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteLinks(): Flow<List<Link>>
    
    @Query("SELECT * FROM links WHERE category = :category ORDER BY createdAt DESC")
    fun getLinksByCategory(category: String): Flow<List<Link>>
    
    @Query("SELECT DISTINCT category FROM links ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>
    
    @Insert
    suspend fun insert(link: Link)
    
    @Delete
    suspend fun delete(link: Link)
    
    @Update
    suspend fun update(link: Link)
    
    @Query("UPDATE links SET clickCount = clickCount + 1, lastOpened = :timestamp WHERE id = :linkId")
    suspend fun incrementClickCount(linkId: Long, timestamp: Long)
}
