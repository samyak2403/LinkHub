package com.samyak.linkhub

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Settings
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samyak.linkhub.ui.FilterOption
import com.samyak.linkhub.ui.LinkViewModel
import com.samyak.linkhub.ui.SortOption
import com.samyak.linkhub.ui.screens.AllLinksScreen
import com.samyak.linkhub.ui.screens.FavoritesScreen
import com.samyak.linkhub.ui.screens.SettingsScreen
import com.samyak.linkhub.ui.screens.WebViewScreen
import com.samyak.linkhub.ui.screens.WebViewState
import com.samyak.linkhub.ui.theme.LinkHubTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object AllLinks : Screen("all_links", "All Links", Icons.Filled.Link, Icons.Outlined.Link)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllLinksTopBar(viewModel: LinkViewModel) {
    val filterOption by viewModel.filterOption.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showFilterMenu by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = { Text("All Links") },
        actions = {
            // Filter button
            IconButton(onClick = { showFilterMenu = true }) {
                Icon(
                    imageVector = when (filterOption) {
                        FilterOption.CATEGORY -> Icons.Filled.Label
                        else -> Icons.Filled.FilterList
                    },
                    contentDescription = "Filter"
                )
            }
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Categories") },
                    onClick = {
                        viewModel.updateFilterOption(FilterOption.ALL)
                        showFilterMenu = false
                    },
                    leadingIcon = { Icon(Icons.Filled.Link, null) }
                )
                if (categories.isNotEmpty()) {
                    HorizontalDivider()
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                viewModel.updateFilterOption(FilterOption.CATEGORY, category)
                                showFilterMenu = false
                            },
                            leadingIcon = { Icon(Icons.Filled.Label, null) }
                        )
                    }
                }
            }
            
            // Sort button
            IconButton(onClick = { showSortMenu = true }) {
                Icon(Icons.Filled.Sort, contentDescription = "Sort")
            }
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Newest First") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.DATE_DESC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Oldest First") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.DATE_ASC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Title A-Z") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.TITLE_ASC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Most Visited") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.MOST_VISITED)
                        showSortMenu = false
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTopBar(viewModel: LinkViewModel) {
    var showSortMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = { Text("Favorites") },
        actions = {
            // Sort button
            IconButton(onClick = { showSortMenu = true }) {
                Icon(Icons.Filled.Sort, contentDescription = "Sort")
            }
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Newest First") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.DATE_DESC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Oldest First") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.DATE_ASC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Title A-Z") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.TITLE_ASC)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Most Visited") },
                    onClick = {
                        viewModel.updateSortOption(SortOption.MOST_VISITED)
                        showSortMenu = false
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    TopAppBar(
        title = { Text("Settings") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewTopBar(title: String, onBackClick: () -> Unit, showInfo: Boolean, onInfoClick: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = if (showInfo) Icons.Filled.Close else Icons.Filled.Info,
                    contentDescription = "Info"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Configure edge-to-edge with proper insets
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val scope = rememberCoroutineScope()
            val isDarkTheme by remember {
                dataStore.data.map { preferences ->
                    preferences[booleanPreferencesKey("dark_theme")] ?: false
                }
            }.collectAsState(initial = false)
            
            LinkHubTheme(darkTheme = isDarkTheme) {
                // Set status bar color to match top bar
                val statusBarColor = MaterialTheme.colorScheme.primaryContainer
                val isLight = !isDarkTheme
                
                LaunchedEffect(statusBarColor, isLight) {
                    window.statusBarColor = statusBarColor.toArgb()
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = isLight
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: LinkViewModel = viewModel()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    var showWebViewInfo by remember { mutableStateOf(false) }
                    var webViewState by remember { mutableStateOf(WebViewState()) }
                    
                    val bottomNavItems = listOf(
                        Screen.AllLinks,
                        Screen.Favorites,
                        Screen.Settings
                    )
                    
                    Scaffold(
                        topBar = {
                            when {
                                currentRoute == Screen.AllLinks.route -> AllLinksTopBar(viewModel = viewModel)
                                currentRoute == Screen.Favorites.route -> FavoritesTopBar(viewModel = viewModel)
                                currentRoute == Screen.Settings.route -> SettingsTopBar()
                                currentRoute?.startsWith("webview/") == true -> {
                                    val title = navBackStackEntry?.arguments?.getString("title")?.let { 
                                        Uri.decode(it) 
                                    } ?: ""
                                    WebViewTopBar(
                                        title = title, 
                                        onBackClick = { navController.popBackStack() },
                                        showInfo = showWebViewInfo,
                                        onInfoClick = { showWebViewInfo = !showWebViewInfo }
                                    )
                                }
                            }
                        },
                        bottomBar = {
                            // Show bottom bar only on main screens
                            if (currentRoute in bottomNavItems.map { it.route }) {
                                NavigationBar {
                                    bottomNavItems.forEach { screen ->
                                        NavigationBarItem(
                                            icon = {
                                                Icon(
                                                    imageVector = if (currentRoute == screen.route) 
                                                        screen.selectedIcon 
                                                    else 
                                                        screen.unselectedIcon,
                                                    contentDescription = screen.title
                                                )
                                            },
                                            label = { Text(screen.title) },
                                            selected = currentRoute == screen.route,
                                            onClick = {
                                                if (currentRoute != screen.route) {
                                                    navController.navigate(screen.route) {
                                                        popUpTo(Screen.AllLinks.route) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.AllLinks.route,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable(Screen.AllLinks.route) {
                                AllLinksScreen(
                                    viewModel = viewModel,
                                    onLinkClick = { link ->
                                        val encodedUrl = Uri.encode(link.url)
                                        val encodedTitle = Uri.encode(link.title)
                                        navController.navigate("webview/$encodedUrl/$encodedTitle")
                                    }
                                )
                            }
                            
                            composable(Screen.Favorites.route) {
                                FavoritesScreen(
                                    viewModel = viewModel,
                                    onLinkClick = { link ->
                                        val encodedUrl = Uri.encode(link.url)
                                        val encodedTitle = Uri.encode(link.title)
                                        navController.navigate("webview/$encodedUrl/$encodedTitle")
                                    }
                                )
                            }
                            
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    viewModel = viewModel,
                                    onBackClick = { navController.popBackStack() },
                                    isDarkTheme = isDarkTheme,
                                    onThemeChange = { newTheme ->
                                        scope.launch {
                                            dataStore.edit { preferences ->
                                                preferences[booleanPreferencesKey("dark_theme")] = newTheme
                                            }
                                        }
                                    }
                                )
                            }
                            
                            composable(
                                route = "webview/{url}/{title}",
                                arguments = listOf(
                                    navArgument("url") { type = NavType.StringType },
                                    navArgument("title") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val url = backStackEntry.arguments?.getString("url")?.let { 
                                    Uri.decode(it) 
                                } ?: ""
                                val title = backStackEntry.arguments?.getString("title")?.let { 
                                    Uri.decode(it) 
                                } ?: ""
                                WebViewScreen(
                                    url = url,
                                    title = title,
                                    onBackClick = { navController.popBackStack() },
                                    showInfo = showWebViewInfo,
                                    onWebViewStateChange = { newState ->
                                        webViewState = newState
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
