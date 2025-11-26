# ImmersionBar

[![Maven Central](https://img.shields.io/maven-central/v/com.yzq/immersionbar.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.yzq%20AND%20a:immersionbar)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.21+-blue.svg)](https://kotlinlang.org)

A modern Android immersion bar library based on official Edge-to-Edge mode with simple API.

## ✨ Features

- 🚀 **Modern Implementation**: Based on Android 15+ official Edge-to-Edge mode
- 📱 **Full Compatibility**: Supports API 21+ (Android 5.0+)
- 🎨 **Simple API**: Chain calls, implement immersive mode in one line
- 🔧 **Flexible Configuration**: Independent control of status bar and navigation bar
- 🌙 **Smart Adaptation**: Auto-handle dark/light status bar text
- 📊 **Real-time Info**: Provide system info like status/navigation bar height

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

        // Enable immersive mode
        ImmersionBar.enable(
            activity = this,
            rootView = findViewById(R.id.content_container), // Root view to add padding
            darkStatusBarText = true,  // Dark text for light background
            showStatusBar = true,      // Show status bar
            showNavigationBar = true   // Show navigation bar
        )
    }
}
```

### Complete Example

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupImmersionBar()
        setupListeners()
    }

    private fun setupImmersionBar() {
        ImmersionBar.enable(
            activity = this,
            rootView = binding.contentContainer,
            darkStatusBarText = isLightBackground(),
            showStatusBar = true,
            showNavigationBar = true
        )
    }

    private fun setupListeners() {
        // Dynamically toggle status bar text color
        binding.changeThemeButton.setOnClickListener {
            val isDark = binding.switchDarkText.isChecked
            ImmersionBar.setStatusBarTextDark(this, isDark)
        }
    }
}
```

## 📖 API Documentation

### ImmersionBar.enable()

Main method to enable immersive mode.

```kotlin
ImmersionBar.enable(
    activity: Activity,              // Target Activity
    rootView: View? = null,          // View to apply WindowInsets
    darkStatusBarText: Boolean = true, // Status bar text is dark
    showStatusBar: Boolean = true,   // Show status bar
    showNavigationBar: Boolean = true // Show navigation bar
)
```

### Other Common Methods

```kotlin
// Manually apply WindowInsets to specified view
ImmersionBar.applyWindowInsets(view)

// Remove WindowInsets listener
ImmersionBar.removeWindowInsets(view)

// Set status bar text color
ImmersionBar.setStatusBarTextDark(activity, isDark = true)

// Get status bar height
val statusBarHeight = ImmersionBar.getStatusBarHeight(context)

// Get navigation bar height
val navigationBarHeight = ImmersionBar.getNavigationBarHeight(context)

// Check if has navigation bar
val hasNavigationBar = ImmersionBar.hasNavigationBar(context)

// Check if has notch screen
val hasNotch = ImmersionBar.hasNotch()
```

## 🎨 Usage Scenarios

### 1. Using in Fragment

```kotlin
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply WindowInsets to Fragment's root view
        ImmersionBar.applyWindowInsets(view)
    }
}
```

### 2. Using in ViewPager

```kotlin
class ViewPagerAdapter : FragmentPagerAdapter {
    override fun getItem(position: Int): Fragment {
        val fragment = MyFragment()
        // Handle WindowInsets inside Fragment
        return fragment
    }
}
```

### 3. Dynamic Theme Switching

```kotlin
private fun toggleTheme() {
    val isDarkTheme = currentTheme == Theme.DARK
    val rootView = findViewById<View>(R.id.root_layout)

    // Change background color
    rootView.setBackgroundColor(if (isDarkTheme) Color.BLACK else Color.WHITE)

    // Update status bar text color
    ImmersionBar.setStatusBarTextDark(this, !isDarkTheme)
}
```

## 🔧 Advanced Configuration

### 1. Complete DrawerLayout Configuration

#### Layout Example (activity_drawer.xml)
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Main content area -->
<androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/main_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/white">

        <!-- AppBar area -->
<com.google.android.material.appbar.AppBarLayout
            android:id="@+id/app_bar_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintTop_toTopOf="parent">

<com.google.android.material.appbar.MaterialToolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                android:background="@color/primary"
                app:title="@string/app_name" />

        </com.google.android.material.appbar.AppBarLayout>

        <!-- Content container -->
<FrameLayout
            android:id="@+id/content_container"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            app:layout_constraintTop_toBottomOf="@id/app_bar_layout"
            app:layout_constraintBottom_toBottomOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <!-- Side navigation drawer -->
<com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        app:menu="@menu/drawer_menu"
        app:headerLayout="@layout/nav_header" />

</androidx.drawerlayout.widget.DrawerLayout>
```

#### Complete Activity Implementation
```kotlin
class DrawerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrawerBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawerLayout()
        setupImmersionBar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupDrawerLayout() {
        // Set up ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set up navigation item click listener
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Handle home click
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_settings -> {
                    // Handle settings click
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupImmersionBar() {
        // Enable immersive mode, let AppBar handle its own Insets
        ImmersionBar.enable(
            activity = this,
            rootView = binding.contentContainer, // Only add padding to content area
            darkStatusBarText = true,
            showStatusBar = true,
            showNavigationBar = true
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
```

### 2. CoordinatorLayout with AppBarLayout

#### Layout Example
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/coordinator"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

<com.google.android.material.appbar.AppBarLayout
        android:id="@+id/app_bar_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">

<com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/collapsing_toolbar"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            app:contentScrim="@color/primary">

<com.google.android.material.appbar.MaterialToolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin" />

        </com.google.android.material.appbar.CollapsingToolbarLayout>

    </com.google.android.material.appbar.AppBarLayout>

    <!-- Content container -->
<androidx.core.widget.NestedScrollView
        android:id="@+id/content_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <!-- Place your scrollable content here -->
<TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="16dp"
            android:text="@string/long_content_text" />

    </androidx.core.widget.NestedScrollView>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### 3. Enhanced Dynamic Theme Switching

```kotlin
class ThemedActivity : AppCompatActivity() {
    private var isDarkTheme = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_themed)

        setupThemeToggle()
        applyTheme()
    }

    private fun setupThemeToggle() {
        binding.themeToggleFab.setOnClickListener {
            isDarkTheme = !isDarkTheme
            applyTheme()
        }
    }

    private fun applyTheme() {
        val rootView = findViewById<View>(R.id.root_layout)
        val bgColor = if (isDarkTheme) {
            Color.parseColor("#1E1E1E")
        } else {
            Color.parseColor("#FFFFFF")
        }

        // Smooth background color transition
        val colorAnim = ValueAnimator.ofArgb(
            Color.parseColor(if (isDarkTheme) "#FFFFFF" else "#1E1E1E"),
            bgColor
        ).apply {
            duration = 300
            addUpdateListener { animator ->
                rootView.setBackgroundColor(animator.animatedValue as Int)
            }
        }
        colorAnim.start()

        // Update status bar text color
        ImmersionBar.setStatusBarTextDark(this, !isDarkTheme)

        // Update UI element colors
        updateUIColors(!isDarkTheme)
    }

    private fun updateUIColors(isLightText: Boolean) {
        val textColor = if (isLightText) Color.BLACK else Color.WHITE
        val cardColor = if (isLightText)
            Color.parseColor("#F5F5F5") else
            Color.parseColor("#2D2D2D")

        binding.titleText.setTextColor(textColor)
        binding.contentCard.setCardBackgroundColor(cardColor)
    }
}
```

### 4. Working with Other Libraries

#### BottomNavigationView Configuration
```kotlin
class BottomNavActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        setupImmersionBar()
        setupBottomNavigation()
    }

    private fun setupImmersionBar() {
        ImmersionBar.enable(
            activity = this,
            rootView = binding.contentContainer, // Only add padding to content area
            darkStatusBarText = true
        )
    }

    private fun setupBottomNavigation() {
        // BottomNavigationView automatically handles WindowInsets
        // Just ensure android:fitsSystemWindows="false"
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Handle home tab switch
                    true
                }
                R.id.nav_explore -> {
                    // Handle explore tab switch
                    true
                }
                R.id.nav_profile -> {
                    // Handle profile tab switch
                    true
                }
                else -> false
            }
        }
    }
}
```

#### MaterialDialog Configuration
```kotlin
class DialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        setupImmersionBar()
        setupDialogs()
    }

    private fun setupImmersionBar() {
        ImmersionBar.enable(
            activity = this,
            rootView = binding.mainContent,
            darkStatusBarText = true
        )
    }

    private fun setupDialogs() {
        binding.showDialogBtn.setOnClickListener {
            MaterialDialog(this).show {
                title(text = "Sample Dialog")
                message(text = "This is a dialog displayed in immersive mode")
                positiveButton(text = "OK") { dialog ->
                    dialog.dismiss()
                }
                // Dialogs automatically handle WindowInsets, no extra config needed
            }
        }
    }
}
```

### 5. Handle Different Android Versions

```kotlin
class ImmersionHelper {
    fun setup(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ use Edge-to-Edge
                ImmersionBar.enable(activity, binding.root)
            }
            else -> {
                // Compatibility handling for older versions
                setupLegacyImmersive(activity)
            }
        }
    }

    private fun setupLegacyImmersive(activity: Activity) {
        // Special handling for Android 10 and below
        ImmersionBar.enable(
            activity = activity,
            rootView = binding.content,
            darkStatusBarText = true,
            showStatusBar = true,
            showNavigationBar = true
        )

        // Older versions might need additional View handling
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Special handling for Android 5.x/6.x
            handleLegacySystemUI(activity)
        }
    }

    private fun handleLegacySystemUI(activity: Activity) {
        // Additional compatibility handling for Android 5.x/6.x
        val window = activity.window
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}
```

### 6. Utility Classes

#### ImmersionBarUtils Utility Class
```kotlin
object ImmersionBarUtils {

    /**
     * Get safe area insets
     */
    fun getSafeAreaInsets(view: View): Rect {
        val insets = ViewCompat.getRootWindowInsets(view)
            ?: return Rect()

        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        return Rect(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
    }

    /**
     * Check if light status bar text should be used
     */
    fun shouldUseLightStatusBar(background: Int): Boolean {
        // Calculate background color brightness
        val red = Color.red(background)
        val green = Color.green(background)
        val blue = Color.blue(background)
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000

        return brightness > 128 // Light background -> dark text
    }

    /**
     * Dynamically adjust status bar text color
     */
    fun adjustStatusBarTextColor(activity: Activity, backgroundColor: Int) {
        val shouldUseDarkText = shouldUseLightStatusBar(backgroundColor)
        ImmersionBar.setStatusBarTextDark(activity, shouldUseDarkText)
    }
}
```

## 🏗️ Project Structure

```
immersionbar/
├── immersion-bar/                 # Main library module
│   ├── src/main/java/com/yzq/immersionbar/
│   │   ├── ImmersionBar.kt       # Main API entry
│   │   ├── ImmersionDelegate.kt  # Immersive implementation
│   │   ├── InsetsDelegate.kt     # WindowInsets handling
│   │   └── BarUtils.kt          # System bar utilities
├── app/                          # Demo app
│   └── src/main/java/com/yzq/immersionbar_demo/
│       ├── MainActivity.kt       # Main demo
│       ├── FragmentDemoActivity.kt  # Fragment demo
│       └── ViewPagerDemoActivity.kt # ViewPager demo
```

## 🤝 Contributing

Issues and Pull Requests are welcome!

1. Fork this repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add some AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Submit Pull Request

## 📄 License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

## 🙏 Acknowledgments

- Thanks to [Android official documentation](https://developer.android.com/) for Edge-to-Edge guidance
- Thanks to all contributors and users for their support

---

**Author**: [yuzhiqiang](https://github.com/yuzhiqiang1993)
**Version**: 1.0.0
**Updated**: 2025-11-25

If this project helps you, please give it a ⭐️ Star to support!

---

**中文文档**: [README.md](README.md)