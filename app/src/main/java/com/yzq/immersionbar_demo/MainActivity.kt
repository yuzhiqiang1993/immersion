package com.yzq.immersionbar_demo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yzq.immersionbar.ImmersionBar
import com.yzq.immersionbar_demo.databinding.ActivityMainBinding
import java.util.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateImmersionBar()
        initListeners()
        updatePaddingSwitchesState()
        updateInfo()
    }

    private fun initListeners() {
        // 沉浸式开关
        binding.switchEnableImmersion.setOnCheckedChangeListener { _, isChecked ->
            // 沉浸式关闭时，禁用并关闭 padding 开关
            if (!isChecked) {
                binding.switchPaddingStatusBar.isChecked = false
                binding.switchPaddingNavBar.isChecked = false
            }
            updatePaddingSwitchesState()
            updateImmersionBar()
        }

        // 状态栏开关
        binding.switchShowStatusBar.setOnCheckedChangeListener { _, _ ->
            updateImmersionBar()
        }

        // 导航栏开关
        binding.switchShowNavBar.setOnCheckedChangeListener { _, _ ->
            updateImmersionBar()
        }

        // 状态栏文字颜色开关
        binding.switchDarkStatusText.setOnCheckedChangeListener { _, _ ->
            updateImmersionBar()
        }

        // 状态栏 padding 开关
        binding.switchPaddingStatusBar.setOnCheckedChangeListener { _, _ ->
            updateImmersionBar()
        }

        // 导航栏 padding 开关
        binding.switchPaddingNavBar.setOnCheckedChangeListener { _, _ ->
            updateImmersionBar()
        }



        // 随机背景颜色
        binding.btnChangeColor.setOnClickListener {
            val randomColor = randomColor()
            binding.rootView.setBackgroundColor(randomColor)

            // 自动调整文字颜色以保持对比度
            val isLightBg = ColorUtils.calculateLuminance(randomColor) > 0.5
            binding.switchDarkStatusText.isChecked = isLightBg

            updateUIColors(isLightBg)
            updateImmersionBar()
        }

        // Fragment 演示跳转
        binding.btnFragment.setOnClickListener {
            startActivity(Intent(this, FragmentDemoActivity::class.java))
        }

        // ViewPager 演示跳转
        binding.btnViewPager.setOnClickListener {
            startActivity(Intent(this, ViewPagerDemoActivity::class.java))
        }

        // DrawerLayout 演示跳转
        binding.btnDrawer.setOnClickListener {
            startActivity(Intent(this, DrawerDemoActivity::class.java))
        }

        // CoordinatorLayout 演示跳转
        binding.btnCoordinator.setOnClickListener {
            startActivity(Intent(this, CoordinatorDemoActivity::class.java))
        }
    }



    private fun updateImmersionBar() {
        val enableImmersion = binding.switchEnableImmersion.isChecked

        if (enableImmersion) {
            // 启用沉浸式模式
            ImmersionBar.enable(
                activity = this,
                paddingStatusBar = binding.switchPaddingStatusBar.isChecked,
                paddingNavigationBar = binding.switchPaddingNavBar.isChecked,
                darkStatusBarText = binding.switchDarkStatusText.isChecked,
                showStatusBar = binding.switchShowStatusBar.isChecked,
                showNavigationBar = binding.switchShowNavBar.isChecked
            )
        } else {
            // 禁用沉浸式模式，恢复默认状态
            ImmersionBar.disable(this)
            // 禁用沉浸式后，仍需要更新系统栏显示/隐藏状态
            ImmersionBar.updateSystemBars(
                activity = this,
                darkStatusBarText = binding.switchDarkStatusText.isChecked,
                showStatusBar = binding.switchShowStatusBar.isChecked,
                showNavigationBar = binding.switchShowNavBar.isChecked
            )
        }

        updateInfo()
    }

    private fun updateInfo() {
        binding.root.post {
            val insets = ViewCompat.getRootWindowInsets(binding.root)
            val sb = StringBuilder()
            sb.append("系统信息:\n")
            sb.append("• Android 版本: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})\n")

            if (insets != null) {
                val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                sb.append("• 状态栏高度: ${statusBar.top}px\n")
                sb.append("• 导航栏高度: ${navBar.bottom}px\n")
            }

            sb.append("\n当前配置:\n")
            sb.append("• 沉浸式: ${if (binding.switchEnableImmersion.isChecked) "开启" else "关闭"}\n")
            sb.append("• 状态栏: ${if (binding.switchShowStatusBar.isChecked) "显示" else "隐藏"}\n")
            sb.append("• 导航栏: ${if (binding.switchShowNavBar.isChecked) "显示" else "隐藏"}\n")
            sb.append("• 文字颜色: ${if (binding.switchDarkStatusText.isChecked) "深色" else "浅色"}\n")
            sb.append("• 状态栏 Padding: ${if (binding.switchPaddingStatusBar.isChecked) "开启" else "关闭"}\n")
            sb.append("• 导航栏 Padding: ${if (binding.switchPaddingNavBar.isChecked) "开启" else "关闭"}\n")


            binding.tvInfo.text = sb.toString()
        }
    }

    /**
     * 更新 padding 开关的启用/禁用状态
     * 只有沉浸式开启时，padding 开关才可用
     */
    private fun updatePaddingSwitchesState() {
        val enableImmersion = binding.switchEnableImmersion.isChecked
        binding.switchPaddingStatusBar.isEnabled = enableImmersion
        binding.switchPaddingNavBar.isEnabled = enableImmersion
    }

    private fun updateUIColors(isLightBg: Boolean) {
        val textColor = if (isLightBg) Color.parseColor("#212121") else Color.WHITE
        val cardBgColor =
            if (isLightBg) Color.parseColor("#F5F5F5") else Color.parseColor("#424242")
        val infoBgColor =
            if (isLightBg) Color.parseColor("#F5F5F5") else Color.parseColor("#424242")
        val infoTextColor =
            if (isLightBg) Color.parseColor("#616161") else Color.parseColor("#BDBDBD")

        binding.tvTitle.setTextColor(textColor)
        binding.switchShowStatusBar.setTextColor(textColor)
        binding.switchShowNavBar.setTextColor(textColor)
        binding.switchDarkStatusText.setTextColor(textColor)
        binding.switchPaddingStatusBar.setTextColor(textColor)
        binding.switchPaddingNavBar.setTextColor(textColor)

        binding.cardSettings.setCardBackgroundColor(cardBgColor)

        binding.tvInfo.setBackgroundColor(infoBgColor)
        binding.tvInfo.setTextColor(infoTextColor)
    }

    private fun randomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }
}
