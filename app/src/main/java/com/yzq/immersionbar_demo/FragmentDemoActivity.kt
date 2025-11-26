package com.yzq.immersionbar_demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yzq.immersionbar.ImmersionBar

class FragmentDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_demo)

        // 启用沉浸式
        ImmersionBar.enable(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    SimpleFragment.newInstance("Fragment 演示", Color.parseColor("#FF9800"))
                )
                .commit()
        }
    }
}
