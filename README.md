# ImmersionBar

[![Maven Central](https://img.shields.io/maven-central/v/com.yzq/immersionbar.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:com.yzq%20AND%20a:immersionbar)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.7.21+-blue.svg)](https://kotlinlang.org)

一个现代化的 Android 沉浸式状态栏库，基于 Android 官方推荐的 Edge-to-Edge 模式设计，提供简洁易用的 API。

## ✨ 特性

- 🚀 **现代化实现**: 基于 Android 15+ 官方推荐的 Edge-to-Edge 模式
- 📱 **全面兼容**: 支持 API 21+ (Android 5.0+)
- 🎨 **简洁 API**: 链式调用，一行代码实现沉浸式
- 🔧 **灵活配置**: 支持状态栏、导航栏独立控制
- 🌙 **智能适配**: 自动处理深色/浅色状态栏文字
- 📊 **实时信息**: 提供状态栏/导航栏高度等系统信息

## 📦 安装

在模块的 `build.gradle.kts` 文件中添加依赖：

```kotlin
dependencies {
    implementation("com.xeonyu:immersionbar:1.0.0")
}
```

## 🚀 快速开始

### 基础用法

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 启用沉浸式模式
        ImmersionBar.enable(
            activity = this,
            rootView = findViewById(R.id.content_container), // 需要添加 padding 的根视图
            darkStatusBarText = true,  // 深色文字，适合浅色背景
            showStatusBar = true,      // 显示状态栏
            showNavigationBar = true   // 显示导航栏
        )
    }
}
```

### 完整示例

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
        // 动态切换状态栏文字颜色
        binding.changeThemeButton.setOnClickListener {
            val isDark = binding.switchDarkText.isChecked
            ImmersionBar.setStatusBarTextDark(this, isDark)
        }
    }
}
```

## 📖 API 文档

### ImmersionBar.enable()

启用沉浸式模式的主要方法。

```kotlin
ImmersionBar.enable(
    activity: Activity,              // 目标 Activity
    rootView: View? = null,          // 需要应用 Insets 的视图
    darkStatusBarText: Boolean = true, // 状态栏文字是否为深色
    showStatusBar: Boolean = true,   // 是否显示状态栏
    showNavigationBar: Boolean = true // 是否显示导航栏
)
```

### 其他常用方法

```kotlin
// 手动应用 WindowInsets 到指定视图
ImmersionBar.applyWindowInsets(view)

// 移除 WindowInsets 监听器
ImmersionBar.removeWindowInsets(view)

// 设置状态栏文字颜色
ImmersionBar.setStatusBarTextDark(activity, isDark = true)

// 获取状态栏高度
val statusBarHeight = ImmersionBar.getStatusBarHeight(context)

// 获取导航栏高度
val navigationBarHeight = ImmersionBar.getNavigationBarHeight(context)

// 检查是否含有导航栏
val hasNavigationBar = ImmersionBar.hasNavigationBar(context)

// 检查是否为刘海屏
val hasNotch = ImmersionBar.hasNotch()
```

## 🎨 使用场景

### 1. 在 Fragment 中使用

```kotlin
class MyFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 为 Fragment 的根视图应用 WindowInsets
        ImmersionBar.applyWindowInsets(view)
    }
}
```

### 2. 在 ViewPager 中使用

```kotlin
class ViewPagerAdapter : FragmentPagerAdapter {
    override fun getItem(position: Int): Fragment {
        val fragment = MyFragment()
        // 在 Fragment 内部处理 WindowInsets
        return fragment
    }
}
```

### 3. 动态主题切换

```kotlin
private fun toggleTheme() {
    val isDarkTheme = currentTheme == Theme.DARK
    val rootView = findViewById<View>(R.id.root_layout)

    // 更换背景颜色
    rootView.setBackgroundColor(if (isDarkTheme) Color.BLACK else Color.WHITE)

    // 更新状态栏文字颜色
    ImmersionBar.setStatusBarTextDark(this, !isDarkTheme)
}
```

## 🔧 高级配置

### 1. DrawerLayout 完整配置

#### 布局文件示例 (activity_drawer.xml)
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- 主内容区域 -->
<androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/main_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/white">

        <!-- AppBar 区域 -->
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

        <!-- 内容容器 -->
<FrameLayout
            android:id="@+id/content_container"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            app:layout_constraintTop_toBottomOf="@id/app_bar_layout"
            app:layout_constraintBottom_toBottomOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

    <!-- 侧滑菜单 -->
<com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        app:menu="@menu/drawer_menu"
        app:headerLayout="@layout/nav_header" />

</androidx.drawerlayout.widget.DrawerLayout>
```

#### Activity 完整实现
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
        // 设置 ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 设置导航项点击监听
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // 处理首页点击
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_settings -> {
                    // 处理设置点击
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupImmersionBar() {
        // 启用沉浸式，不传 rootView 让 AppBar 自己处理 Insets
        ImmersionBar.enable(
            activity = this,
            rootView = binding.contentContainer, // 只为内容区域添加 padding
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

### 2. CoordinatorLayout 与 AppBarLayout

#### 布局示例
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

    <!-- 内容容器 -->
<androidx.core.widget.NestedScrollView
        android:id="@+id/content_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <!-- 这里放置你的滚动内容 -->
<TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="16dp"
            android:text="@string/long_content_text" />

    </androidx.core.widget.NestedScrollView>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### 3. 动态主题切换增强版

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

        // 平滑过渡背景颜色
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

        // 更新状态栏文字颜色
        ImmersionBar.setStatusBarTextDark(this, !isDarkTheme)

        // 更新 UI 元素颜色
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

### 4. 与其他库的配合使用

#### BottomNavigationView 配置
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
            rootView = binding.contentContainer, // 只为内容区域添加 padding
            darkStatusBarText = true
        )
    }

    private fun setupBottomNavigation() {
        // BottomNavigationView 会自动处理 WindowInsets
        // 无需额外配置，但确保 android:fitsSystemWindows="false"
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // 处理首页切换
                    true
                }
                R.id.nav_explore -> {
                    // 处理探索页切换
                    true
                }
                R.id.nav_profile -> {
                    // 处理个人页切换
                    true
                }
                else -> false
            }
        }
    }
}
```

#### MaterialDialog 配置
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
                title(text = "示例对话框")
                message(text = "这是在沉浸式模式下显示的对话框")
                positiveButton(text = "确定") { dialog ->
                    dialog.dismiss()
                }
                // 对话框会自动处理 WindowInsets，无需额外配置
            }
        }
    }
}
```

### 5. 处理不同 Android 版本

```kotlin
class ImmersionHelper {
    fun setup(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                // Android 11+ 使用 Edge-to-Edge
                ImmersionBar.enable(activity, binding.root)
            }
            else -> {
                // 旧版本的兼容处理
                setupLegacyImmersive(activity)
            }
        }
    }

    private fun setupLegacyImmersive(activity: Activity) {
        // 为 Android 10 及以下版本的特殊处理
        ImmersionBar.enable(
            activity = activity,
            rootView = binding.content,
            darkStatusBarText = true,
            showStatusBar = true,
            showNavigationBar = true
        )

        // 旧版本可能需要额外的 View 处理
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Android 5.x/6.x 的特殊处理
            handleLegacySystemUI(activity)
        }
    }

    private fun handleLegacySystemUI(activity: Activity) {
        // 对 Android 5.x/6.x 的额外兼容处理
        val window = activity.window
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}
```

### 6. 实用工具类

#### ImmersionBarUtils 工具类
```kotlin
object ImmersionBarUtils {

    /**
     * 获取安全区域边距
     */
    fun getSafeAreaInsets(view: View): Rect {
        val insets = ViewCompat.getRootWindowInsets(view)
            ?: return Rect()

        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        return Rect(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
    }

    /**
     * 检查是否需要调整状态栏文字颜色
     */
    fun shouldUseLightStatusBar(background: Int): Boolean {
        // 计算背景颜色亮度
        val red = Color.red(background)
        val green = Color.green(background)
        val blue = Color.blue(background)
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000

        return brightness > 128 // 背景亮则使用深色文字
    }

    /**
     * 动态调整状态栏文字颜色
     */
    fun adjustStatusBarTextColor(activity: Activity, backgroundColor: Int) {
        val shouldUseDarkText = shouldUseLightStatusBar(backgroundColor)
        ImmersionBar.setStatusBarTextDark(activity, shouldUseDarkText)
    }
}
```

## 🏗️ 项目结构

```
immersionbar/
├── immersion-bar/                 # 主要库模块
│   ├── src/main/java/com/yzq/immersionbar/
│   │   ├── ImmersionBar.kt       # 主要 API 入口
│   │   ├── ImmersionDelegate.kt  # 沉浸式实现逻辑
│   │   ├── InsetsDelegate.kt     # WindowInsets 处理
│   │   └── BarUtils.kt          # 系统栏工具类
├── app/                          # 示例应用
│   └── src/main/java/com/yzq/immersionbar_demo/
│       ├── MainActivity.kt       # 主界面演示
│       ├── FragmentDemoActivity.kt  # Fragment 演示
│       └── ViewPagerDemoActivity.kt # ViewPager 演示
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支: `git checkout -b feature/AmazingFeature`
3. 提交更改: `git commit -m 'Add some AmazingFeature'`
4. 推送到分支: `git push origin feature/AmazingFeature`
5. 提交 Pull Request

## 📄 许可证

本项目采用 Apache License 2.0 许可证。详情请查看 [LICENSE](LICENSE) 文件。

## 🙏 致谢

- 感谢 [Android 官方文档](https://developer.android.com/) 提供的 Edge-to-Edge 指导
- 感谢所有贡献者和用户的支持

---

**作者**: [yuzhiqiang](https://github.com/yuzhiqiang1993)
**版本**: 1.0.0
**更新时间**: 2025-11-25

如果这个项目对您有帮助，请给个 ⭐️ Star 支持一下！

---

**English Documentation**: [README_EN.md](README_EN.md)