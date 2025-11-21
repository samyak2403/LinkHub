package com.samyak.linkhub.data

import kotlinx.coroutines.flow.Flow

class LinkRepository(private val linkDao: LinkDao) {
    val allLinks: Flow<List<Link>> = linkDao.getAllLinks()
    val favoriteLinks: Flow<List<Link>> = linkDao.getFavoriteLinks()
    val allCategories: Flow<List<String>> = linkDao.getAllCategories()
    
    fun getLinksByCategory(category: String): Flow<List<Link>> {
        return linkDao.getLinksByCategory(category)
    }
    
    suspend fun insert(link: Link) {
        linkDao.insert(link)
    }
    
    suspend fun delete(link: Link) {
        linkDao.delete(link)
    }
    
    suspend fun update(link: Link) {
        linkDao.update(link)
    }
    
    suspend fun incrementClickCount(linkId: Long) {
        linkDao.incrementClickCount(linkId, System.currentTimeMillis())
    }
}
