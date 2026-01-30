# ImmersionBar

[![Maven Central](https://img.shields.io/maven-central/v/com.xeonyu/immersionbar.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.xeonyu%20AND%20a:immersionbar)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.21+-blue.svg)](https://kotlinlang.org)

A modern Android immersion bar library based on the officially recommended Edge-to-Edge mode for Android 15+, providing a simple and easy-to-use API.

## Features

- **Modern Implementation**: Based on Android 15+ official Edge-to-Edge mode
- **Wide Compatibility**: Supports API 21+ (Android 5.0+)
- **Simple API**: Chain calls, implement immersion with one line of code
- **Flexible Configuration**: Independent control of status bar and navigation bar
- **Smart Adaptation**: Automatically handle dark/light status bar text
- **Real-time Information**: Provide status bar/navigation bar height and other system information
- **Dynamic Toggle**: Support runtime enable/disable immersion mode

## Installation

Add the dependency to your module's `build.gradle.kts` file:

> **Latest Version**: Please visit [Maven Central](https://central.sonatype.com/artifact/com.xeonyu/immersionbar) to get the latest version number.

```kotlin
dependencies {
    implementation("com.xeonyu:immersionbar:x.x.x")
}
```

Replace `x.x.x` with the latest version number.

## Quick Start

### Basic Usage

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enable immersion mode
        ImmersionBar.enable(
            activity = this,
            darkStatusBarText = true,    // Dark text, suitable for light background
            showStatusBar = true,        // Show status bar
            showNavigationBar = true,    // Show navigation bar
            fitNavigationBar = false     // Not extend to navigation bar
        )
    }
}
```

## API Documentation

### Core Method - ImmersionBar.enable()

The main method to enable immersion mode, complete all immersion settings with one line of code.

```kotlin
ImmersionBar.enable(
    activity: Activity,                    // Required - Target Activity instance
    darkStatusBarText: Boolean = true,     // Optional - Whether status bar text is dark
    showStatusBar: Boolean = true,         // Optional - Whether to show status bar
    showNavigationBar: Boolean = true,     // Optional - Whether to show navigation bar
    fitNavigationBar: Boolean = false      // Optional - Whether content extends to navigation bar
)
```

#### Detailed Parameter Description

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| **activity** | `Activity` | Required | Activity instance to set immersion. Usually pass `this` |
| **darkStatusBarText** | `Boolean` | `true` | Status bar text color mode.<br>• **true**: Dark text (suitable for white/light backgrounds)<br>• **false**: Light text (suitable for black/dark backgrounds)<br>• **Note**: Only effective on Android 6.0+ |
| **showStatusBar** | `Boolean` | `true` | Whether to show status bar.<br>• **true**: Show status bar<br>• **false**: Hide status bar<br>• **When hidden**: Can be temporarily shown with swipe gesture |
| **showNavigationBar** | `Boolean` | `true` | Whether to show navigation bar.<br>• **true**: Show navigation bar<br>• **false**: Hide navigation bar<br>• **When hidden**: Can be temporarily shown with swipe gesture |
| **fitNavigationBar** | `Boolean` | `false` | Whether content extends to navigation bar area.<br>• **false (recommended)**: Does not extend, automatically adds navigation bar height padding to root layout to avoid content being covered by navigation bar<br>• **true**: Extends to bottom, navigation bar transparent, suitable for full-screen scenarios |

### Disable Immersion - ImmersionBar.disable()

Disable immersion mode and restore to default state (system bars white, content not extended).

```kotlin
ImmersionBar.disable(activity: Activity)
```

### Update System Bars - ImmersionBar.updateSystemBars()

Dynamically update system bars visibility and text color, effective regardless of immersion mode state.

```kotlin
ImmersionBar.updateSystemBars(
    activity: Activity,                    // Target Activity
    showStatusBar: Boolean = true,         // Whether to show status bar
    showNavigationBar: Boolean = true,     // Whether to show navigation bar
    darkStatusBarText: Boolean = true      // Whether status bar text is dark
)
```

### Auxiliary Methods

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

### Independent View Padding Control - applyInsetsPadding

Independent of Activity immersion configuration, apply WindowInsets padding to any View individually.

#### Usage Scenario: Full-screen Overlay (Loading/Error View)

When Activity enables immersive mode (Edge-to-Edge), content extends below the status bar. If a full-screen Loading or Error page covers the screen, its top content (like a title bar) will be obscured by the status bar.

At this time, you can use `applyInsetsPadding` to add padding to the top View of the overlay independently, without affecting the layout of the underlying main page.

```kotlin
// Add top padding to the title bar of Loading View to avoid status bar
ImmersionBar.applyInsetsPadding(
    view = binding.tvStatusTitle,
    paddingStatusBar = true
)
```

#### Method Definition

```kotlin
ImmersionBar.applyInsetsPadding(
    view: View,
    paddingStatusBar: Boolean = false,    // Whether to add status bar padding
    paddingNavigationBar: Boolean = false // Whether to add navigation bar padding
)

// Clear padding, restore View's original padding
ImmersionBar.clearInsetsPadding(view: View)
```

#### Core Features

1.  **Independence**: Can be used independently, no need for Activity to enable `ImmersionBar.enable()`.
2.  **Auto-cleanup**: Automatically listens to View attach/detach state, releases listener when View is removed to prevent memory leaks.
3.  **Original Padding Protection**: Records original padding before application, restores automatically on clear or detach, suitable for dynamic Views or RecyclerView Items.

### Dialog Immersion

#### Full Screen Dialog

```kotlin
// Enable full screen dialog immersion (content extends under system bars)
ImmersionBar.enableFullScreenDialog(
    dialog: Dialog,                       // Target Dialog
    darkStatusBarText: Boolean = true,    // Whether status bar text is dark
    paddingStatusBar: Boolean = false,    // Whether to add status bar padding
    paddingNavigationBar: Boolean = false // Whether to add navigation bar padding
)
```

#### Bottom Sheet

```kotlin
// Enable bottom sheet immersion (navigation bar transparent)
ImmersionBar.enableBottomSheetDialog(
    dialog: Dialog,                       // Target Dialog
    paddingNavigationBar: Boolean = false // Whether to add navigation bar padding
)
```

#### Dialog Status Bar Text

```kotlin
// Dynamically set dialog status bar text color
ImmersionBar.setDialogStatusBarTextDark(
    dialog: Dialog,        // Target Dialog
    isDark: Boolean = true // true=dark text, false=light text
)
```

## Usage Scenarios

### 1. Recommended Configuration for Most Apps

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Standard immersion: transparent status bar, navigation bar keeps default color
        ImmersionBar.enable(
            activity = this,
            darkStatusBarText = true,
            showStatusBar = true,
            showNavigationBar = true,
            fitNavigationBar = false  // Not extend to navigation bar
        )
    }
}
```

### 2. Complete Immersion Layout

```kotlin
// Content extends under all system bars (navigation bar transparent)
ImmersionBar.enable(
    activity = this,
    darkStatusBarText = true,
    showStatusBar = true,
    showNavigationBar = true,
    fitNavigationBar = true  // Extend to bottom
)
```

### 3. Hide Navigation Bar

```kotlin
// Hide navigation bar, keep status bar visible
ImmersionBar.enable(
    activity = this,
    darkStatusBarText = true,
    showStatusBar = true,
    showNavigationBar = false  // Hide navigation bar
)
```

### 4. Dark Background with Light Text

```kotlin
// Use light status bar text for dark background
ImmersionBar.enable(
    activity = this,
    darkStatusBarText = false,  // Light text suitable for dark background
    showStatusBar = true,
    showNavigationBar = true,
    fitNavigationBar = false
)
```

### 5. Dynamic Toggle Immersion Mode

```kotlin
class MainActivity : AppCompatActivity() {

    private var isImmersionEnabled = true

    private fun toggleImmersion() {
        if (isImmersionEnabled) {
            // Disable immersion
            ImmersionBar.disable(this)
            // Still can control system bar visibility after disable
            ImmersionBar.updateSystemBars(
                activity = this,
                darkStatusBarText = true,
                showStatusBar = true,
                showNavigationBar = true
            )
        } else {
            // Enable immersion
            ImmersionBar.enable(
                activity = this,
                darkStatusBarText = true,
                showStatusBar = true,
                showNavigationBar = true,
                fitNavigationBar = false
            )
        }
        isImmersionEnabled = !isImmersionEnabled
    }
}
```

### 6. Dynamic Theme Switching

```kotlin
class ThemedActivity : AppCompatActivity() {
    private fun toggleTheme() {
        val isLightTheme = currentTheme == Theme.LIGHT

        // Update background color
        binding.rootView.setBackgroundColor(
            if (isLightTheme) Color.WHITE else Color.BLACK
        )

        // Sync update status bar text color
        ImmersionBar.setStatusBarTextDark(this, isLightTheme)
    }
}
```

### 7. Full-screen Video/Image Viewer

```kotlin
// Complete immersion: hide all system bars
ImmersionBar.enable(
    activity = this,
    darkStatusBarText = false,
    showStatusBar = false,     // Hide status bar
    showNavigationBar = false, // Hide navigation bar
    fitNavigationBar = true    // Fully extend
)
```

### 9. Full Screen Dialog

```kotlin
// Create full screen dialog
val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
dialog.setContentView(R.layout.dialog_fullscreen)

// One line to enable immersion
ImmersionBar.enableFullScreenDialog(dialog)

dialog.show()
```

### 10. Bottom Sheet

```kotlin
// Create bottom sheet dialog
val bottomSheet = BottomSheetDialog(this)
bottomSheet.setContentView(R.layout.dialog_bottom_sheet)

// One line to enable immersion (navigation bar transparent)
ImmersionBar.enableBottomSheetDialog(bottomSheet)

bottomSheet.show()
```

## Best Practices

### Recommended Configuration Combinations

```kotlin
// 1. Recommended configuration for most apps (transparent status bar, navigation bar keeps default)
ImmersionBar.enable(
    activity = this,
    darkStatusBarText = true,
    showStatusBar = true,
    showNavigationBar = true,
    fitNavigationBar = false
)

// 2. Full-screen reading/video apps
ImmersionBar.enable(
    activity = this,
    darkStatusBarText = isLightTheme,
    showStatusBar = false,     // Hide status bar
    showNavigationBar = false, // Hide navigation bar
    fitNavigationBar = true
)

// 3. Game apps
ImmersionBar.enable(
    activity = this,
    darkStatusBarText = false,
    showStatusBar = false,
    showNavigationBar = false,
    fitNavigationBar = true
)
```

### Notes

1. **fitNavigationBar parameter**:
   - Most apps use `false` (default), avoid content being covered by navigation bar
   - Full-screen apps use `true`, content extends to bottom

2. **Version compatibility**: `darkStatusBarText` only effective on Android 6.0+

3. **Gesture navigation**: Hidden system bars can be temporarily shown with gestures

4. **Performance optimization**: Avoid frequent calls to `enable()` method outside of `onCreate`

5. **Disable immersion**: After `disable()`, system bars restore to white and content no longer extends under system bars

6. **Padding parameter dependency**:
   - `paddingStatusBar` and `paddingNavigationBar` parameters are only effective when immersion mode is enabled
   - When immersion mode is disabled, padding settings automatically become ineffective
   - When implementing UI switches, it's recommended to bind padding switch enabled state to immersion switch state
   - Example code:
     ```kotlin
     // When immersion is disabled, disable and uncheck padding switches
     if (!isChecked) {
         binding.switchPaddingStatusBar.isChecked = false
         binding.switchPaddingNavBar.isChecked = false
     }
     // Control padding switches availability based on immersive state
     binding.switchPaddingStatusBar.isEnabled = isChecked
     binding.switchPaddingNavBar.isEnabled = isChecked
     ```

## Project Structure

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

## Contributing

Welcome to submit Issues and Pull Requests!

1. Fork this repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add some AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Submit Pull Request



## License

This project is licensed under the Apache License 2.0. For details, please see the [LICENSE](LICENSE) file.

---

If this project helps you, please give it a Star to support!

---

**Chinese Documentation**: [README.md](README.md)
