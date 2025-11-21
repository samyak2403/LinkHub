# LinkHub - Android Link Manager

A modern Android app built with Jetpack Compose for saving and managing web links.

## Features

- ✅ Save links with title and URL
- ✅ Search/filter links by title or URL
- ✅ View links in built-in WebView
- ✅ Material 3 Design with dynamic colors
- ✅ Local persistence with Room database
- ✅ MVVM architecture with Repository pattern

## Tech Stack

- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (ViewModel + Repository)
- **Database**: Room
- **Async**: Kotlin Coroutines + StateFlow
- **Navigation**: Jetpack Navigation Compose

## Project Structure

```
app/src/main/java/com/samyak/linkhub/
├── data/
│   ├── Link.kt              # Room entity
│   ├── LinkDao.kt           # Database access object
│   ├── LinkDatabase.kt      # Room database
│   └── LinkRepository.kt    # Data repository
├── ui/
│   ├── components/
│   │   ├── AddLinkDialog.kt # Dialog for adding links
│   │   └── LinkItem.kt      # Link list item composable
│   ├── screens/
│   │   ├── HomeScreen.kt    # Main screen with link list
│   │   └── WebViewScreen.kt # WebView for opening links
│   ├── theme/               # Material 3 theme
│   └── LinkViewModel.kt     # ViewModel with StateFlow
└── MainActivity.kt          # Single activity with navigation
```

## How to Run

1. Open project in Android Studio
2. Sync Gradle
3. Run on emulator or device (API 24+)

## Requirements

- Android Studio Hedgehog or later
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 35 (Android 15)
