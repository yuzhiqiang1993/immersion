package com.yzq.immersionbar_demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.yzq.immersion.applyStatusBarMargin
import com.yzq.immersion.applyStatusBarPadding
import com.yzq.immersion.setStatusBarDark
import com.yzq.immersionbar_demo.databinding.FragmentSimpleBinding

class SimpleFragment : Fragment() {

    private var _binding: FragmentSimpleBinding? = null
    private val binding get() = _binding!!

    private var bgColor: Int = Color.WHITE
    private var title: String = "Fragment"
    // 0: 完全沉浸
    // 1: Padding 模式（背景铺满，内容推下）
    // 2: Margin 模式（整个视图和背景全部推下）
    private var avoidMode: Int = 0

    companion object {
        private const val ARG_COLOR = "arg_color"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_AVOID_MODE = "arg_avoid_mode"

        fun newInstance(title: String, color: Int, avoidMode: Int = 0): SimpleFragment {
            val fragment = SimpleFragment()
            val args = Bundle()
            args.putInt(ARG_COLOR, color)
            args.putString(ARG_TITLE, title)
            args.putInt(ARG_AVOID_MODE, avoidMode)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bgColor = it.getInt(ARG_COLOR)
            title = it.getString(ARG_TITLE) ?: "Fragment"
            avoidMode = it.getInt(ARG_AVOID_MODE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimpleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootView.setBackgroundColor(bgColor)

        val modeText = when (avoidMode) {
            0 -> "沉浸"
            1 -> "Padding"
            else -> "Margin"
        }
        binding.tvTitle.text = "$title · $modeText"
        binding.tvDesc.text = "观察顶部内容与状态栏文字颜色"

        when (avoidMode) {
            0 -> { /* 完全沉浸，不做任何避让 */ }
            1 -> binding.rootView.applyStatusBarPadding()
            2 -> {
                binding.rootView.applyStatusBarMargin()

            }
        }

        // 根据背景亮度调整文字颜色
        val isLight = ColorUtils.calculateLuminance(bgColor) > 0.5
        val textColor = if (isLight) Color.BLACK else Color.WHITE
        binding.tvTitle.setTextColor(textColor)
        binding.tvDesc.setTextColor(textColor)
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            // Margin 模式下顶部显示的是 Activity 背景，不是 Fragment 背景。
            val targetColor = if (avoidMode == 2) {
                (it.window.decorView.background as? ColorDrawable)?.color ?: Color.WHITE
            } else {
                bgColor
            }
            val isLightBg = ColorUtils.calculateLuminance(targetColor) > 0.5
            it.setStatusBarDark(isLightBg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
