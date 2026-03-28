package com.yzq.immersionbar_demo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.yzq.immersion.*
import com.yzq.immersionbar_demo.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        refreshImmersion()

        // 绑定视图避让（Padding/Margin 开关应用）
        applyInsetsWithLibraryApi()

        initListeners()
        updateInfoText()
    }

    private fun applyInsetsWithLibraryApi() {
        // 获取内容容器
        val container = binding.contentContainer.parent as View

        // 库 API：同时控制系统栏 Padding 避让
        container.applySystemBarsPadding(
            addStatusBar = binding.switchPaddingStatusBar.isChecked,
            addNavigationBar = binding.switchPaddingNavBar.isChecked
        )

        // 库 API：同时控制系统栏 Margin 避让
        container.applySystemBarsMargin(
            addStatusBar = binding.switchMarginStatusBar.isChecked,
            addNavigationBar = binding.switchMarginNavBar.isChecked
        )

        updateInfoText()
    }

    private fun initListeners() {
        val immersionListener = CompoundButton.OnCheckedChangeListener { _, _ -> refreshImmersion() }
        binding.switchShowStatusBar.setOnCheckedChangeListener(immersionListener)
        binding.switchShowNavBar.setOnCheckedChangeListener(immersionListener)
        binding.switchDarkStatusText.setOnCheckedChangeListener(immersionListener)

        val insetsListener = CompoundButton.OnCheckedChangeListener { _, _ -> applyInsetsWithLibraryApi() }
        binding.switchPaddingStatusBar.setOnCheckedChangeListener(insetsListener)
        binding.switchPaddingNavBar.setOnCheckedChangeListener(insetsListener)
        binding.switchMarginStatusBar.setOnCheckedChangeListener(insetsListener)
        binding.switchMarginNavBar.setOnCheckedChangeListener(insetsListener)

        // 随机背景颜色
        binding.btnChangeColor.setOnClickListener {
            val color = randomColor()
            binding.rootView.setBackgroundColor(color)

            val isLightBg = color.isLightColor()
            binding.switchDarkStatusText.isChecked = isLightBg
            updateUITheme(isLightBg)
            refreshImmersion()
        }

        // 跳转逻辑
        binding.btnViewPager.setOnClickListener { startActivity(Intent(this, ViewPagerDemoActivity::class.java)) }
        binding.btnWebView.setOnClickListener { startActivity(Intent(this, WebViewActivity::class.java)) }
        binding.btnDrawer.setOnClickListener { startActivity(Intent(this, DrawerDemoActivity::class.java)) }
        binding.btnCoordinator.setOnClickListener { startActivity(Intent(this, CoordinatorDemoActivity::class.java)) }
    }

    private fun refreshImmersion() {
        // 库 API：Activity 沉浸式全局入口
        setupImmersion(
            showStatusBar = binding.switchShowStatusBar.isChecked,
            showNavigationBar = binding.switchShowNavBar.isChecked,
            isStatusBarDarkFont = binding.switchDarkStatusText.isChecked
        )
        updateInfoText()
    }

    private fun updateInfoText() {
        binding.rootView.post {
            binding.tvInfo.text = buildString {
                append("系统信息:\n")
                append("• Android 版本: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
                append("• 状态栏高度: ${binding.rootView.statusBarHeight}px\n")
                append("• 导航栏高度: ${binding.rootView.navigationBarHeight}px\n\n")
                append("当前配置:\n")
                append("• 状态栏: ${if (binding.switchShowStatusBar.isChecked) "显示" else "隐藏"}\n")
                append("• 导航栏: ${if (binding.switchShowNavBar.isChecked) "显示" else "隐藏"}\n")
                append("• 文字颜色: ${if (binding.switchDarkStatusText.isChecked) "深色" else "浅色"}\n")
            }
        }
    }

    private fun updateUITheme(isLight: Boolean) {
        val textColor = if (isLight) Color.parseColor("#212121") else Color.WHITE
        val cardBgColor = if (isLight) Color.parseColor("#F5F5F5") else Color.parseColor("#424242")
        val infoTextColor = if (isLight) Color.parseColor("#616161") else Color.parseColor("#BDBDBD")

        binding.tvTitle.setTextColor(textColor)
        listOf(
            binding.switchShowStatusBar, binding.switchShowNavBar, binding.switchDarkStatusText,
            binding.switchPaddingStatusBar, binding.switchPaddingNavBar,
            binding.switchMarginStatusBar, binding.switchMarginNavBar
        ).forEach { it.setTextColor(textColor) }

        binding.cardSettings.setCardBackgroundColor(cardBgColor)
        binding.tvInfo.setBackgroundColor(cardBgColor)
        binding.tvInfo.setTextColor(infoTextColor)
    }

    private fun randomColor() = Color.argb(255, Random().nextInt(256), Random().nextInt(256), Random().nextInt(256))
}
