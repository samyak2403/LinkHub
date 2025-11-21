package com.samyak.linkhub.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samyak.linkhub.data.Link
import com.samyak.linkhub.data.LinkDatabase
import com.samyak.linkhub.data.LinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption {
    DATE_DESC, DATE_ASC, TITLE_ASC, MOST_VISITED
}

enum class FilterOption {
    ALL, FAVORITES, CATEGORY
}

class LinkViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LinkRepository
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _sortOption = MutableStateFlow(SortOption.DATE_DESC)
    val sortOption: StateFlow<SortOption> = _sortOption
    
    private val _filterOption = MutableStateFlow(FilterOption.ALL)
    val filterOption: StateFlow<FilterOption> = _filterOption
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory
    
    init {
        val linkDao = LinkDatabase.getDatabase(application).linkDao()
        repository = LinkRepository(linkDao)
    }
    
    val categories: StateFlow<List<String>> = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val filteredLinks: StateFlow<List<Link>> = combine(
        repository.allLinks,
        repository.favoriteLinks,
        _searchQuery,
        _sortOption,
        _filterOption,
        _selectedCategory
    ) { flows ->
        val allLinks = flows[0] as List<Link>
        val favorites = flows[1] as List<Link>
        val query = flows[2] as String
        val sort = flows[3] as SortOption
        val filter = flows[4] as FilterOption
        val category = flows[5] as String?
        
        var links = when (filter) {
            FilterOption.ALL -> allLinks
            FilterOption.FAVORITES -> favorites
            FilterOption.CATEGORY -> category?.let { cat ->
                allLinks.filter { it.category == cat }
            } ?: allLinks
        }
        
        // Apply search filter
        if (query.isNotBlank()) {
            links = links.filter { link ->
                link.title.contains(query, ignoreCase = true) ||
                link.url.contains(query, ignoreCase = true) ||
                link.category.contains(query, ignoreCase = true)
            }
        }
        
        // Apply sorting and return
        when (sort) {
            SortOption.DATE_DESC -> links.sortedByDescending { it.createdAt }
            SortOption.DATE_ASC -> links.sortedBy { it.createdAt }
            SortOption.TITLE_ASC -> links.sortedBy { it.title.lowercase() }
            SortOption.MOST_VISITED -> links.sortedByDescending { it.clickCount }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateSortOption(option: SortOption) {
        _sortOption.value = option
    }
    
    fun updateFilterOption(option: FilterOption, category: String? = null) {
        _filterOption.value = option
        _selectedCategory.value = category
    }
    
    fun insert(link: Link) = viewModelScope.launch {
        repository.insert(link)
    }
    
    fun delete(link: Link) = viewModelScope.launch {
        repository.delete(link)
    }
    
    fun update(link: Link) = viewModelScope.launch {
        repository.update(link)
    }
    
    fun toggleFavorite(link: Link) = viewModelScope.launch {
        repository.update(link.copy(isFavorite = !link.isFavorite))
    }
    
    fun incrementClickCount(linkId: Long) = viewModelScope.launch {
        repository.incrementClickCount(linkId)
    }
}
