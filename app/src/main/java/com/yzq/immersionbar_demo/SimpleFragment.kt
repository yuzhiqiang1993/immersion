package com.yzq.immersionbar_demo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.yzq.immersionbar.ImmersionBar
import com.yzq.immersionbar_demo.databinding.FragmentSimpleBinding

class SimpleFragment : Fragment() {

    private var _binding: FragmentSimpleBinding? = null
    private val binding get() = _binding!!

    private var bgColor: Int = Color.WHITE
    private var title: String = "Fragment"

    companion object {
        private const val ARG_COLOR = "arg_color"
        private const val ARG_TITLE = "arg_title"

        fun newInstance(title: String, color: Int): SimpleFragment {
            val fragment = SimpleFragment()
            val args = Bundle()
            args.putInt(ARG_COLOR, color)
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bgColor = it.getInt(ARG_COLOR)
            title = it.getString(ARG_TITLE) ?: "Fragment"
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
        binding.tvTitle.text = title

        // 根据背景亮度调整文字颜色
        val isLight = ColorUtils.calculateLuminance(bgColor) > 0.5
        val textColor = if (isLight) Color.BLACK else Color.WHITE
        binding.tvTitle.setTextColor(textColor)
        binding.tvDesc.setTextColor(textColor)
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            val isLightBg = ColorUtils.calculateLuminance(bgColor) > 0.5
            // 浅色背景 -> 深色文字(true)
            ImmersionBar.setStatusBarTextDark(it, isLightBg)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
