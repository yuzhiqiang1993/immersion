package com.yzq.immersionbar_demo

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yzq.immersion.applySystemBarsMargin
import com.yzq.immersion.applySystemBarsPadding
import com.yzq.immersion.navigationBarHeight
import com.yzq.immersion.setupBottomSheetImmersion
import com.yzq.immersion.setupImmersion
import com.yzq.immersion.statusBarHeight
import com.yzq.immersion.isLightColor
import com.yzq.immersionbar_demo.databinding.ActivityMainBinding
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 新架构默认就是沉浸式，无需开关
        binding.switchEnableImmersion.visibility = View.GONE

        // 初始化沉浸式（库 API）
        updateImmersion()
        // 初始化 Padding/Margin（库 API）
        applyPaddingWithLibraryApi()
        applyMarginWithLibraryApi()

        initListeners()
        updateInfoText()
    }

    /**
     * 使用库 API：applySystemBarsPadding 控制 View Padding 避让。
     * 库内部通过 View Tag 防叠加，可安全多次调用。
     */
    private fun applyPaddingWithLibraryApi() {
        val scrollView = binding.contentContainer.parent as View
        scrollView.applySystemBarsPadding(
            addStatusBar = binding.switchPaddingStatusBar.isChecked,
            addNavigationBar = binding.switchPaddingNavBar.isChecked
        )
        updateInfoText()
    }

    /**
     * 使用库 API：applySystemBarsMargin 控制 View Margin 避让。
     * 库内部通过 View Tag 防叠加，可安全多次调用。
     */
    private fun applyMarginWithLibraryApi() {
        val scrollView = binding.contentContainer.parent as View
        scrollView.applySystemBarsMargin(
            addStatusBar = binding.switchMarginStatusBar.isChecked,
            addNavigationBar = binding.switchMarginNavBar.isChecked
        )
        updateInfoText()
    }

    private fun initListeners() {
        // 状态栏显示/隐藏
        binding.switchShowStatusBar.setOnCheckedChangeListener { _, _ -> updateImmersion() }

        // 导航栏显示/隐藏
        binding.switchShowNavBar.setOnCheckedChangeListener { _, _ -> updateImmersion() }

        // 状态栏文字颜色
        binding.switchDarkStatusText.setOnCheckedChangeListener { _, _ -> updateImmersion() }

        // Padding 开关
        val paddingChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            applyPaddingWithLibraryApi()
        }
        binding.switchPaddingStatusBar.setOnCheckedChangeListener(paddingChangeListener)
        binding.switchPaddingNavBar.setOnCheckedChangeListener(paddingChangeListener)

        // Margin 开关
        val marginChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            applyMarginWithLibraryApi()
        }
        binding.switchMarginStatusBar.setOnCheckedChangeListener(marginChangeListener)
        binding.switchMarginNavBar.setOnCheckedChangeListener(marginChangeListener)

        // 随机背景颜色
        binding.btnChangeColor.setOnClickListener {
            val randomColor = randomColor()
            binding.rootView.setBackgroundColor(randomColor)

            val isLightBg = randomColor.isLightColor()
            binding.switchDarkStatusText.isChecked = isLightBg
            updateUIColors(isLightBg)
            updateImmersion()
        }

        // 各演示页面跳转

        binding.btnViewPager.setOnClickListener {
            startActivity(Intent(this, ViewPagerDemoActivity::class.java))
        }

        binding.btnDrawer.setOnClickListener {
            startActivity(Intent(this, DrawerDemoActivity::class.java))
        }
        binding.btnCoordinator.setOnClickListener {
            startActivity(Intent(this, CoordinatorDemoActivity::class.java))
        }
        binding.btnFullScreenDialog.setOnClickListener { showFullScreenDialog() }
        binding.btnBottomSheet.setOnClickListener { showBottomSheetDialog() }
        binding.btnViewPadding.setOnClickListener {
            startActivity(Intent(this, ViewPaddingDemoActivity::class.java))
        }
    }

    private fun showFullScreenDialog() {
        val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_full_screen)
        dialog.findViewById<View>(R.id.btnClose).setOnClickListener { dialog.dismiss() }

        // 使用库 API：Dialog 扩展
        dialog.setupImmersion()
        dialog.show()
    }

    private fun showBottomSheetDialog() {
        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(R.layout.dialog_bottom_sheet)

        // 使用库 API：BottomSheet 扩展
        bottomSheet.setupBottomSheetImmersion()
        bottomSheet.show()
    }

    /**
     * 使用库 API：Activity 扩展
     */
    private fun updateImmersion() {
        setupImmersion(
            showStatusBar = binding.switchShowStatusBar.isChecked,
            showNavigationBar = binding.switchShowNavBar.isChecked,
            isStatusBarDark = binding.switchDarkStatusText.isChecked
        )
        updateInfoText()
    }

    private fun updateInfoText() {
        binding.rootView.post {
            val sb = StringBuilder()
            sb.append("系统信息:\n")
            sb.append("• Android 版本: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")
            sb.append("• 状态栏高度: ${binding.rootView.statusBarHeight}px\n")
            sb.append("• 导航栏高度: ${binding.rootView.navigationBarHeight}px\n")

            sb.append("\n当前配置:\n")
            sb.append("• 状态栏: ${if (binding.switchShowStatusBar.isChecked) "显示" else "隐藏"}\n")
            sb.append("• 导航栏: ${if (binding.switchShowNavBar.isChecked) "显示" else "隐藏"}\n")
            sb.append("• 文字颜色: ${if (binding.switchDarkStatusText.isChecked) "深色" else "浅色"}\n")
            sb.append("• 状态栏 Padding: ${if (binding.switchPaddingStatusBar.isChecked) "开启" else "关闭"}\n")
            sb.append("• 导航栏 Padding: ${if (binding.switchPaddingNavBar.isChecked) "开启" else "关闭"}\n")
            sb.append("• 状态栏 Margin: ${if (binding.switchMarginStatusBar.isChecked) "开启" else "关闭"}\n")
            sb.append("• 导航栏 Margin: ${if (binding.switchMarginNavBar.isChecked) "开启" else "关闭"}\n")

            binding.tvInfo.text = sb.toString()
        }
    }

    private fun updateUIColors(isLightBg: Boolean) {
        val textColor = if (isLightBg) Color.parseColor("#212121") else Color.WHITE
        val cardBgColor =
            if (isLightBg) Color.parseColor("#F5F5F5") else Color.parseColor("#424242")
        val infoTextColor =
            if (isLightBg) Color.parseColor("#616161") else Color.parseColor("#BDBDBD")

        binding.tvTitle.setTextColor(textColor)
        binding.switchShowStatusBar.setTextColor(textColor)
        binding.switchShowNavBar.setTextColor(textColor)
        binding.switchDarkStatusText.setTextColor(textColor)
        binding.switchPaddingStatusBar.setTextColor(textColor)
        binding.switchPaddingNavBar.setTextColor(textColor)
        binding.switchMarginStatusBar.setTextColor(textColor)
        binding.switchMarginNavBar.setTextColor(textColor)

        binding.cardSettings.setCardBackgroundColor(cardBgColor)
        binding.tvInfo.setBackgroundColor(cardBgColor)
        binding.tvInfo.setTextColor(infoTextColor)
    }

    private fun randomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}
