package com.samyak.linkhub.ui.screens

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

data class WebViewState(
    val isLoading: Boolean = true,
    val loadProgress: Int = 0,
    val currentUrl: String = "",
    val isSecure: Boolean = false,
    val pageStartTime: Long = 0,
    val pageLoadTime: Long = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    title: String,
    onBackClick: () -> Unit,
    showInfo: Boolean,
    onWebViewStateChange: (WebViewState) -> Unit
) {
    var webViewState by remember { mutableStateOf(WebViewState(currentUrl = url, isSecure = url.startsWith("https://"))) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    
    LaunchedEffect(webViewState) {
        onWebViewStateChange(webViewState)
    }
    
    // Handle back button press
    BackHandler(enabled = true) {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBackClick()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webView = this
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            url?.let {
                                webViewState = webViewState.copy(
                                    isLoading = true,
                                    currentUrl = it,
                                    isSecure = it.startsWith("https://"),
                                    pageStartTime = System.currentTimeMillis()
                                )
                            }
                        }
                        
                        override fun onPageFinished(view: WebView?, url: String?) {
                            val loadTime = System.currentTimeMillis() - webViewState.pageStartTime
                            webViewState = webViewState.copy(
                                isLoading = false,
                                pageLoadTime = loadTime
                            )
                        }
                    }
                    
                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            webViewState = webViewState.copy(loadProgress = newProgress)
                        }
                    }
                    
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls = true
                    settings.displayZoomControls = false
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Loading progress bar
        if (webViewState.isLoading && webViewState.loadProgress < 100) {
            LinearProgressIndicator(
                progress = { webViewState.loadProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
            )
        }
        
        // Info bottom sheet
        if (showInfo) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Website Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    HorizontalDivider()
                    
                    // URL Section
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = if (webViewState.isSecure) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (webViewState.isSecure) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                        )
                        Column {
                            Text(
                                text = if (webViewState.isSecure) "Secure Connection" else "Not Secure",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (webViewState.isSecure) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = webViewState.currentUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Page Title
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = "Page Title",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Connection Info
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalCellularAlt,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = "Connection",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = if (webViewState.isSecure) "Encrypted (HTTPS)" else "Not Encrypted (HTTP)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // JavaScript Status
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = "JavaScript",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Enabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Domain Info
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = "Domain",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = try {
                                    java.net.URL(webViewState.currentUrl).host
                                } catch (e: Exception) {
                                    "Unknown"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Page Load Time / Traffic
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = "Page Load Time",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = if (webViewState.pageLoadTime > 0) {
                                    val seconds = webViewState.pageLoadTime / 1000.0
                                    String.format("%.2f seconds", seconds)
                                } else if (webViewState.isLoading) {
                                    "Loading..."
                                } else {
                                    "Not available"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Loading Progress
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.DataUsage,
                            contentDescription = null
                        )
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Loading Progress",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${webViewState.loadProgress}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { webViewState.loadProgress / 100f },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    
                    HorizontalDivider()
                    
                    // Connection Speed Indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = null
                        )
                        Column {
                            Text(
                                text = "Connection Speed",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = when {
                                    webViewState.pageLoadTime == 0L -> "Measuring..."
                                    webViewState.pageLoadTime < 1000 -> "Fast"
                                    webViewState.pageLoadTime < 3000 -> "Good"
                                    webViewState.pageLoadTime < 5000 -> "Average"
                                    else -> "Slow"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    webViewState.pageLoadTime == 0L -> MaterialTheme.colorScheme.onSurfaceVariant
                                    webViewState.pageLoadTime < 1000 -> Color(0xFF4CAF50)
                                    webViewState.pageLoadTime < 3000 -> Color(0xFF8BC34A)
                                    webViewState.pageLoadTime < 5000 -> Color(0xFFFFC107)
                                    else -> MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
