# LinkHub - Android Link Manager

A modern, feature-rich Android app built with Jetpack Compose for saving and managing web links.

## ✨ Features

### Core Features
- ✅ **Save & Manage Links** - Add links with title, URL, category, and notes
- ✅ **Smart Search** - Filter links by title, URL, or category
- ✅ **Categories** - Organize links (Work, Personal, Shopping, etc.)
- ✅ **Favorites** - Star important links for quick access
- ✅ **WebView** - Open links in built-in browser

### Advanced Features
- ✅ **Swipe to Delete** - Swipe left to delete with undo option
- ✅ **Long-press Menu** - Edit, copy URL, share, or delete
- ✅ **Sort Options** - Sort by date, title, or most visited
- ✅ **Click Analytics** - Track view counts for each link
- ✅ **Export/Import** - Backup and restore links as JSON
- ✅ **Dark/Light Theme** - Manual theme toggle with persistence
- ✅ **Share Links** - Share URLs with other apps
- ✅ **Copy URL** - Quick copy to clipboard
- ✅ **Favicon Support** - Display website favicons (with Coil)
- ✅ **Material 3 Design** - Dynamic colors and modern UI

## 🛠 Tech Stack

- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (ViewModel + Repository)
- **Database**: Room with migrations
- **Async**: Kotlin Coroutines + StateFlow
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil
- **Preferences**: DataStore
- **Storage**: JSON export/import

## 📁 Project Structure

```
app/src/main/java/com/samyak/linkhub/
├── data/
│   ├── Link.kt              # Room entity with all fields
│   ├── LinkDao.kt           # Database queries
│   ├── LinkDatabase.kt      # Room database
│   └── LinkRepository.kt    # Data repository
├── ui/
│   ├── components/
│   │   ├── AddLinkDialog.kt # Add/Edit dialog with categories
│   │   └── LinkItem.kt      # Link card with swipe & long-press
│   ├── screens/
│   │   ├── HomeScreen.kt    # Main screen with all features
│   │   ├── WebViewScreen.kt # WebView for opening links
│   │   └── SettingsScreen.kt # Settings & export/import
│   ├── theme/               # Material 3 theme
│   └── LinkViewModel.kt     # ViewModel with filters & sorting
├── utils/
│   └── LinkUtils.kt         # Export/import & favicon utilities
└── MainActivity.kt          # Navigation & theme management
```

## 🚀 How to Run

1. Open project in Android Studio
2. Sync Gradle dependencies
3. Run on emulator or device (API 24+)

## 📋 Requirements

- Android Studio Hedgehog or later
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 35 (Android 15)
- Kotlin 2.0.21

## 🎯 Key Features Breakdown

### Link Management
- Add links with title, URL, category, and notes
- Edit existing links
- Delete with undo functionality
- Swipe to delete gesture

### Organization
- 9 predefined categories + custom categories
- Favorite/unfavorite links
- Filter by category or favorites
- Search across all fields

### Sorting
- Newest first (default)
- Oldest first
- Alphabetical (A-Z)
- Most visited

### Data Management
- Export all links to JSON file
- Import links from JSON backup
- Persistent theme preference
- Click count tracking

### UI/UX
- Material 3 with dynamic colors
- Dark/Light theme toggle
- Swipe gestures
- Long-press context menu
- Empty state illustrations
- Snackbar notifications
