# ImmersionBar

[![Maven Central](https://img.shields.io/maven-central/v/com.xeonyu/immersionbar.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.xeonyu%20AND%20a:immersionbar)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.21+-blue.svg)](https://kotlinlang.org)

A modern Android immersion bar library based on the officially recommended Edge-to-Edge mode for Android 15+, providing a simple and easy-to-use API.

## ✨ Features

- 🚀 **Modern Implementation**: Based on Android 15+ official Edge-to-Edge mode
- 📱 **Wide Compatibility**: Supports API 21+ (Android 5.0+)
- 🎨 **Simple API**: Chain calls, implement immersion with one line of code
- 🔧 **Flexible Configuration**: Independent control of status bar and navigation bar
- 🌙 **Smart Adaptation**: Automatically handle dark/light status bar text
- 📊 **Real-time Information**: Provide status bar/navigation bar height and other system information

## 📦 Installation

Add the dependency to your module's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.xeonyu:immersionbar:1.0.0")
}
```

## 🚀 Quick Start

### Basic Usage

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable immersion mode
        ImmersionBar.enable(
            activity = this,
            rootView = findViewById(R.id.content_container), // Root view that needs status bar padding to avoid coverage
            darkStatusBarText = true,  // Dark text, suitable for light background
            showStatusBar = true,      // Show status bar
            showNavigationBar = true   // Show navigation bar
        )
    }
}
```

## 📖 API Documentation

### Core Method - ImmersionBar.enable()

The main method to enable immersion mode, complete all immersion settings with one line of code.

```kotlin
ImmersionBar.enable(
    activity: Activity,                    // Required - Target Activity instance
    rootView: View? = null,                // Optional - View that needs Insets applied
    darkStatusBarText: Boolean = true,     // Optional - Whether status bar text is dark
    showStatusBar: Boolean = true,         // Optional - Whether to show status bar
    showNavigationBar: Boolean = true      // Optional - Whether to show navigation bar
)
```

#### Detailed Parameter Description

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| **activity** | `Activity` | Required | Activity instance to set immersion. Usually pass `this` |
| **rootView** | `View?` | `null` | View that needs automatic system bar padding.<br>• **Pass value**: Automatically add padding of status bar and navigation bar height to this view<br>• **null**: No automatic padding handling, content extends under system bars<br>• **Common usage**: Pass root layout or content container to avoid content being covered by system bars |
| **darkStatusBarText** | `Boolean` | `true` | Status bar text color mode.<br>• **true**: Dark text (suitable for white/light backgrounds)<br>• **false**: Light text (suitable for black/dark backgrounds)<br>• **Note**: Only effective on Android 6.0+ |
| **showStatusBar** | `Boolean` | `true` | Whether to show status bar.<br>• **true**: Show status bar<br>• **false**: Hide status bar<br>• **When hidden**: Can be temporarily shown with swipe gesture |
| **showNavigationBar** | `Boolean` | `true` | Whether to show navigation bar.<br>• **true**: Show navigation bar<br>• **false**: Hide navigation bar<br>• **When hidden**: Can be temporarily shown with swipe gesture |

### Auxiliary Methods

#### WindowInsets Related Methods

```kotlin
// Manually apply WindowInsets to specified view (add system bar height padding)
ImmersionBar.applyWindowInsets(view: View)

// Remove WindowInsets listener from view
ImmersionBar.removeWindowInsets(view: View)
```

#### Status Bar Text Control

```kotlin
// Dynamically set status bar text color
ImmersionBar.setStatusBarTextDark(
    activity: Activity,        // Target Activity
    isDark: Boolean = true     // true=dark text, false=light text
)
```

#### System Information Retrieval

```kotlin
// Get status bar height (in pixels)
val statusBarHeight = ImmersionBar.getStatusBarHeight(context: Context)

// Get navigation bar height (in pixels)
val navigationBarHeight = ImmersionBar.getNavigationBarHeight(context: Context)

// Check if device has navigation bar (returns Boolean)
val hasNavigationBar = ImmersionBar.hasNavigationBar(context: Context)

// Check if device has notch screen (returns Boolean)
val hasNotch = ImmersionBar.hasNotch()
```

## 🎨 Usage Scenarios

### 1. Complete Immersion Layout

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Complete immersion: content extends under system bars, automatically handle padding
        ImmersionBar.enable(
            activity = this,
            rootView = findViewById(R.id.content_container),
            darkStatusBarText = true,
            showStatusBar = true,
            showNavigationBar = true
        )
    }
}
```

### 2. Hide Navigation Bar

```kotlin
// Hide navigation bar, keep status bar visible
ImmersionBar.enable(
    activity = this,
    rootView = binding.contentContainer,
    darkStatusBarText = true,
    showStatusBar = true,
    showNavigationBar = false  // Hide navigation bar
)
```

### 3. Dark Background with Light Text

```kotlin
// Use light status bar text for dark background
ImmersionBar.enable(
    activity = this,
    rootView = binding.rootView,
    darkStatusBarText = false,  // Light text suitable for dark background
    showStatusBar = true,
    showNavigationBar = true
)
```

### 4. Usage in Fragment

```kotlin
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply WindowInsets to Fragment's root view
        ImmersionBar.applyWindowInsets(view)
    }
}
```

### 5. Dynamic Theme Switching

```kotlin
class ThemedActivity : AppCompatActivity() {
    private fun toggleTheme() {
        val isDarkBackground = currentTheme == Theme.DARK

        // Update background color
        binding.rootView.setBackgroundColor(
            if (isDarkBackground) Color.BLACK else Color.WHITE
        )

        // Sync update status bar text color
        ImmersionBar.setStatusBarTextDark(this, !isDarkBackground)
    }
}
```

## 💡 Best Practices

### Recommended Configuration Combinations

```kotlin
// 1. Recommended configuration for most apps
ImmersionBar.enable(
    activity = this,
    rootView = binding.contentContainer,
    darkStatusBarText = true,
    showStatusBar = true,
    showNavigationBar = true
)

// 2. Full-screen video/image viewer
ImmersionBar.enable(
    activity = this,
    rootView = null,  // No padding needed, content fully extends
    darkStatusBarText = false,
    showStatusBar = false,    // Hide status bar
    showNavigationBar = false // Hide navigation bar
)

// 3. Reading apps (can hide navigation bar)
ImmersionBar.enable(
    activity = this,
    rootView = binding.contentContainer,
    darkStatusBarText = isLightTheme,
    showStatusBar = true,
    showNavigationBar = false  // Hide navigation bar for more reading space
)
```

### Notes

1. **rootView parameter**: Should be passed in most cases to avoid content being covered by system bars
2. **Version compatibility**: `darkStatusBarText` only effective on Android 6.0+
3. **Gesture navigation**: Hidden system bars can be temporarily shown with gestures
4. **Performance optimization**: Avoid frequent calls to `enable()` method outside of `onCreate`

## 🏗️ Project Structure

```
immersionbar/
├── immersion-bar/                 # Main library module
│   └── src/main/java/com/yzq/immersionbar/
│       ├── ImmersionBar.kt       # Main API entry
│       ├── ImmersionDelegate.kt  # Immersion implementation logic
│       ├── InsetsDelegate.kt     # WindowInsets handling
│       └── BarUtils.kt          # System bar utility class
└── app/                          # Demo application
    └── src/main/java/com/yzq/immersionbar_demo/
        ├── MainActivity.kt       # Main interface demo
        ├── FragmentDemoActivity.kt  # Fragment demo
        └── ViewPagerDemoActivity.kt # ViewPager demo
```

## 🤝 Contributing

Welcome to submit Issues and Pull Requests!

1. Fork this repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add some AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Submit Pull Request

## 📄 License

This project is licensed under the Apache License 2.0. For details, please see the [LICENSE](LICENSE) file.

---

**Author**: [yuzhiqiang](https://github.com/yuzhiqiang1993)
**Version**: 1.0.0
**Update Time**: 2025-11-26
**Repository**: https://github.com/yuzhiqiang1993/immersionbar

If this project helps you, please give it a ⭐️ Star to support!

---

**Chinese Documentation**: [README.md](README.md)