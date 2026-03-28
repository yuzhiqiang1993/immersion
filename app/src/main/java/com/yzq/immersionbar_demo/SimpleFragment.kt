package com.yzq.immersionbar_demo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.yzq.immersion.applyStatusBarMargin
import com.yzq.immersion.applyStatusBarPadding
import com.yzq.immersion.syncStatusBarFontWithBg
import com.yzq.immersionbar_demo.databinding.FragmentSimpleBinding

class SimpleFragment : Fragment() {

    private var _binding: FragmentSimpleBinding? = null
    private val binding get() = _binding!!

    private var bgColor: Int = Color.WHITE
    private var title: String = ""
    private var avoidMode: Int = 0

    companion object {
        private const val ARG_COLOR = "arg_color"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_AVOID_MODE = "arg_avoid_mode"

        fun newInstance(title: String, color: Int, avoidMode: Int = 0) = SimpleFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_COLOR, color)
                putString(ARG_TITLE, title)
                putInt(ARG_AVOID_MODE, avoidMode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bgColor = it.getInt(ARG_COLOR)
            title = it.getString(ARG_TITLE) ?: ""
            avoidMode = it.getInt(ARG_AVOID_MODE, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSimpleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rootView.setBackgroundColor(bgColor)

        val modeLabel = when (avoidMode) {
            0 -> "沉浸"
            1 -> "Padding"
            else -> "Margin"
        }
        binding.tvTitle.text = "$title · $modeLabel"

        // 库 API：根据模式进行顶部避让
        when (avoidMode) {
            1 -> binding.rootView.applyStatusBarPadding()
            2 -> binding.rootView.applyStatusBarMargin()
        }

        // 局部 UI 染色
        val isLight = ColorUtils.calculateLuminance(bgColor) > 0.5
        val contentColor = if (isLight) Color.BLACK else Color.WHITE
        binding.tvTitle.setTextColor(contentColor)
        binding.tvDesc.setTextColor(contentColor)
    }

    override fun onResume() {
        super.onResume()
        // 库 API：当 Fragment 可见时，通知 Activity 刷新状态栏文字颜色
        activity?.syncStatusBarFontWithBg()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
