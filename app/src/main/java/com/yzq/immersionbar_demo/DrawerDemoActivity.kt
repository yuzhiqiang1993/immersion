package com.yzq.immersionbar_demo

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.yzq.immersion.*
import com.yzq.immersionbar_demo.databinding.ActivityDrawerDemoBinding

class DrawerDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawerDemoBinding
    private lateinit var toggle: ActionBarDrawerToggle
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
        setupImmersionConfig()
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
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply { isDrawerIndicatorEnabled = true }

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { finish(); true }
                R.id.nav_settings -> { showSettingsDialog(); true }
                R.id.nav_about -> { showAboutDialog(); true }
                else -> false
            }.also { if (it) binding.drawerLayout.closeDrawers() }
        }
    }

    private fun setupImmersionConfig() {
        // 库 API：初始化沉浸式（状态栏/导航栏默认自动推断）
        setupImmersion()

        // Toolbar 标题栏适配状态栏高度
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.layoutParams = binding.toolbar.layoutParams.apply {
                height = actionBarHeight + systemBars.top
            }
            binding.toolbar.setPadding(
                binding.toolbar.paddingLeft, systemBars.top,
                binding.toolbar.paddingRight, binding.toolbar.paddingBottom
            )
            insets
        }

        // 库 API：NavigationView Header 避让状态栏
        binding.navView.getHeaderView(0).applyStatusBarPadding()

        toggle.drawerArrowDrawable.color = Color.WHITE

        // 库 API：FAB 避让导航栏
        binding.fabRandom.applyNavigationBarMargin()
    }

    private fun setupClickListeners() {
        binding.fabRandom.setOnClickListener {
            applyBackgroundColor(predefinedColors.random().toColorInt())
        }

        binding.btnToggleTheme.setOnClickListener {
            val isCurrentlyLight = currentBackgroundColor.isLightColor()
            applyBackgroundColor(if (isCurrentlyLight) Color.parseColor("#2C2C2C") else Color.WHITE)
        }

        binding.switchDarkStatus.setOnCheckedChangeListener { _, isChecked ->
            // 库 API：直接控制状态栏文字深浅色
            setStatusBarDarkFont(isChecked)
        }

        binding.switchShowStatus.setOnCheckedChangeListener { _, isChecked ->
            // 库 API：重新配置沉浸式可见性
            setupImmersion(showStatusBar = isChecked)
        }
    }

    private fun applyBackgroundColor(color: Int) {
        val currentColor = (binding.mainContent.background as? android.graphics.drawable.ColorDrawable)?.color ?: Color.WHITE
        currentBackgroundColor = color

        ValueAnimator.ofArgb(currentColor, color).apply {
            duration = 300
            addUpdateListener { binding.mainContent.setBackgroundColor(it.animatedValue as Int) }
        }.start()

        updateUIColors(color)
    }

    private fun updateUIColors(bgColor: Int) {
        val isLightBg = bgColor.isLightColor()
        val textColor = if (isLightBg) Color.parseColor("#212121") else Color.WHITE
        binding.switchDarkStatus.setTextColor(textColor)
        binding.switchShowStatus.setTextColor(textColor)

        val toolbarColor = bgColor.darkenColor(if (isLightBg) 0.4f else 0.8f)
        binding.toolbar.setBackgroundColor(toolbarColor)

        val isToolbarLight = toolbarColor.isLightColor()
        val toolbarContentColor = if (isToolbarLight) Color.parseColor("#212121") else Color.WHITE
        binding.toolbar.setTitleTextColor(toolbarContentColor)
        toggle.drawerArrowDrawable.color = toolbarContentColor

        // 库 API：根据 Toolbar 亮度动态刷新状态栏文字颜色
        setStatusBarDarkFont(isToolbarLight)
        binding.switchDarkStatus.isChecked = isToolbarLight

        binding.btnToggleTheme.text = if (isLightBg) "切换为深色主题" else "切换为浅色主题"
        binding.navView.getHeaderView(0).setBackgroundColor(toolbarColor.darkenColor(0.85f))
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val switchDarkStatus = dialogView.findViewById<MaterialSwitch>(R.id.switch_dark_status)
        val switchShowStatus = dialogView.findViewById<MaterialSwitch>(R.id.switch_show_status)
        val switchShowNav = dialogView.findViewById<MaterialSwitch>(R.id.switch_show_nav)

        // 假设当前状态从 View 获取
        switchDarkStatus.isChecked = currentBackgroundColor.isLightColor() 
        switchShowStatus.isChecked = true
        switchShowNav.isChecked = true

        androidx.appcompat.app.AlertDialog.Builder(this).setTitle("沉浸式设置").setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                this@DrawerDemoActivity.setupImmersion(
                    showStatusBar = switchShowStatus.isChecked,
                    showNavigationBar = switchShowNav.isChecked,
                    isStatusBarDarkFont = switchDarkStatus.isChecked
                )
            }.setNegativeButton("取消", null).show()
    }

    private fun showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this).setTitle("关于 Immersion")
            .setMessage("Immersion Demo - DrawerLayout 示例\n\n基于官方 Edge-to-Edge 模式设计的沉浸式库。")
            .setPositiveButton("确定", null).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }
}
