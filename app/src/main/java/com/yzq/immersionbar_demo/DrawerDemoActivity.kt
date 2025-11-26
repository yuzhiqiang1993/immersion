package com.yzq.immersionbar_demo

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.yzq.immersionbar.ImmersionBar
import com.yzq.immersionbar_demo.databinding.ActivityDrawerDemoBinding

/**
 * @description: DrawerLayout 沉浸式示例
 * @author : yuzhiqiang
 */
class DrawerDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawerDemoBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private var isDarkStatusBarText = false
    private var currentBackgroundColor = Color.WHITE

    private val predefinedColors = listOf(
        "#FFFFFF", "#F5F5F5", "#E3F2FD", "#E8F5E8", "#FFF3E0", "#FCE4EC",
        "#1E1E1E", "#2C2C2C", "#1565C0", "#2E7D32", "#E65100", "#C2185B"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDrawerLayout()
        setupImmersionBar()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setupDrawerLayout() {
        // 设置 ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            isDrawerIndicatorEnabled = true
        }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 设置导航项点击监听
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_settings -> {
                    showSettingsDialog()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_about -> {
                    showAboutDialog()
                    binding.drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupImmersionBar() {
        // 启用沉浸式，不指定 rootView（让整个页面延伸到状态栏）
        ImmersionBar.enable(
            activity = this,
            rootView = binding.toolbar,
            darkStatusBarText = isDarkStatusBarText,
            showStatusBar = true,
            showNavigationBar = true
        )

        // 手动处理 Toolbar 的 WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 获取 ActionBar 标准高度
            val typedValue = android.util.TypedValue()
            var actionBarHeight = 0
            if (theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
                actionBarHeight = android.util.TypedValue.complexToDimensionPixelSize(
                    typedValue.data,
                    resources.displayMetrics
                )
            }

            // 动态设置 Toolbar 的高度为 actionBarSize + statusBarHeight
            val toolbarLp = binding.toolbar.layoutParams
            toolbarLp.height = actionBarHeight + systemBars.top
            binding.toolbar.layoutParams = toolbarLp

            // 给 Toolbar 添加顶部 Padding，将内容（标题、图标）推下来
            binding.toolbar.setPadding(
                binding.toolbar.paddingLeft,
                systemBars.top,
                binding.toolbar.paddingRight,
                binding.toolbar.paddingBottom
            )

            insets
        }

        // 手动处理 NavigationView Header 的 WindowInsets
        val headerView = binding.navView.getHeaderView(0)
        ViewCompat.setOnApplyWindowInsetsListener(headerView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // 给 Header 添加顶部 Padding，避免被状态栏遮挡
            view.setPadding(
                view.paddingLeft,
                view.paddingTop + systemBars.top,
                view.paddingRight,
                view.paddingBottom
            )
            
            insets
        }

        // 设置初始的 Drawer Toggle 图标颜色为白色
        toggle.drawerArrowDrawable.color = Color.WHITE
    }

    private fun setupClickListeners() {
        // FAB：随机切换背景颜色
        binding.fabRandom.setOnClickListener {
            val randomColorString = predefinedColors.random()
            val color = randomColorString.toColorInt()
            applyBackgroundColor(color)
        }

        // 切换深色主题按钮
        binding.btnToggleTheme.setOnClickListener {
            isDarkStatusBarText = !isDarkStatusBarText
            
            // 改变背景颜色演示效果
            val bgColor = if (isDarkStatusBarText) Color.WHITE else Color.parseColor("#2C2C2C")
            applyBackgroundColor(bgColor)
        }

        // 状态栏文字颜色开关
        binding.switchDarkStatus.setOnCheckedChangeListener { _, isChecked ->
            isDarkStatusBarText = isChecked
            ImmersionBar.setStatusBarTextDark(this, isDarkStatusBarText)
        }

        // 状态栏显示开关
        binding.switchShowStatus.setOnCheckedChangeListener { _, isChecked ->
            ImmersionBar.enable(
                activity = this,
                rootView = null,
                darkStatusBarText = isDarkStatusBarText,
                showStatusBar = isChecked,
                showNavigationBar = true
            )
        }
    }

    private fun applyBackgroundColor(color: Int) {
        currentBackgroundColor = color

        // 平滑过渡背景颜色
        val currentColor = (binding.mainContent.background as? android.graphics.drawable.ColorDrawable)?.color
            ?: Color.WHITE

        ValueAnimator.ofArgb(currentColor, color).apply {
            duration = 300
            addUpdateListener { animator ->
                val animatedColor = animator.animatedValue as Int
                binding.mainContent.setBackgroundColor(animatedColor)
            }
        }.start()

        // 判断背景色是否为浅色
        val shouldUseDarkText = isLightColor(color)

        // 更新 UI 颜色
        updateUIColors(shouldUseDarkText, color)
    }

    private fun updateUIColors(isLightBg: Boolean, bgColor: Int) {
        val textColor = if (isLightBg) Color.parseColor("#212121") else Color.WHITE

        // 更新开关颜色
        binding.switchDarkStatus.setTextColor(textColor)
        binding.switchShowStatus.setTextColor(textColor)

        // 基于主背景色生成协调的 Toolbar 颜色
        // 深色背景：稍微加深；浅色背景：使用深色变体
        val toolbarColor = if (isLightBg) {
            // 浅色背景：使用深色变体以保持对比度
            darkenColor(bgColor, 0.4f)
        } else {
            // 深色背景：稍微加深以区分层次
            darkenColor(bgColor, 0.8f)
        }
        binding.toolbar.setBackgroundColor(toolbarColor)
        
        // 根据 Toolbar 背景色判断是否为浅色
        val isToolbarLight = isLightColor(toolbarColor)
        
        // Debug: 打印颜色信息
        android.util.Log.d("DrawerDemo", "Main bg: ${String.format("#%06X", 0xFFFFFF and bgColor)}, Toolbar: ${String.format("#%06X", 0xFFFFFF and toolbarColor)}, isLight: $isToolbarLight")
        
        // 根据 Toolbar 背景色设置标题和图标颜色
        val toolbarContentColor = if (isToolbarLight) Color.parseColor("#212121") else Color.WHITE
        android.util.Log.d("DrawerDemo", "Toolbar content color: ${String.format("#%06X", 0xFFFFFF and toolbarContentColor)}")
        
        binding.toolbar.setTitleTextColor(toolbarContentColor)
        
        // 设置 DrawerToggle 图标颜色（汉堡菜单图标）
        toggle.drawerArrowDrawable.color = toolbarContentColor
        
        // 根据 Toolbar 背景色设置状态栏文字颜色
        val shouldUseDarkStatusText = isToolbarLight
        if (shouldUseDarkStatusText != isDarkStatusBarText) {
            isDarkStatusBarText = shouldUseDarkStatusText
            binding.switchDarkStatus.isChecked = shouldUseDarkStatusText
        }
        ImmersionBar.setStatusBarTextDark(this, shouldUseDarkStatusText)

        // 更新按钮文字
        val btnText = if (isLightBg) "切换为深色主题" else "切换为浅色主题"
        binding.btnToggleTheme.text = btnText

        // 更新 NavigationView Header（使用与 Toolbar 类似但稍有区别的颜色）
        updateNavHeaderColors(toolbarColor)
    }

    private fun updateNavHeaderColors(toolbarColor: Int) {
        val headerView = binding.navView.getHeaderView(0)
        // Nav Header 使用比 Toolbar 稍深一点的颜色
        val navHeaderBg = darkenColor(toolbarColor, 0.85f)
        headerView.setBackgroundColor(navHeaderBg)
    }

    private fun darkenColor(color: Int, factor: Float = 0.7f): Int {
        val red = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val green = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val blue = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.rgb(red, green, blue)
    }

    private fun isLightColor(color: Int): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val brightness = (red * 299 + green * 587 + blue * 114) / 1000
        return brightness > 128
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val switchDarkStatus = dialogView.findViewById<MaterialSwitch>(R.id.switch_dark_status)
        val switchShowStatus = dialogView.findViewById<MaterialSwitch>(R.id.switch_show_status)
        val switchShowNav = dialogView.findViewById<MaterialSwitch>(R.id.switch_show_nav)

        // 设置当前状态
        switchDarkStatus.isChecked = isDarkStatusBarText
        switchShowStatus.isChecked = true
        switchShowNav.isChecked = true

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("沉浸式设置")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                isDarkStatusBarText = switchDarkStatus.isChecked
                ImmersionBar.enable(
                    activity = this,
                    rootView = null,
                    darkStatusBarText = isDarkStatusBarText,
                    showStatusBar = switchShowStatus.isChecked,
                    showNavigationBar = switchShowNav.isChecked
                )
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("关于 ImmersionBar")
            .setMessage(
                """
                ImmersionBar Demo - DrawerLayout 示例

                版本: 1.0.0

                这是一个现代化的 Android 沉浸式状态栏库，
                基于官方推荐的 Edge-to-Edge 模式设计。

                特性:
                • 简洁易用的 API
                • 完整的系统栏控制
                • 智能的颜色适配
                • 广泛的版本兼容
                • DrawerLayout 完美支持
                """.trimIndent()
            )
            .setPositiveButton("确定", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }
}