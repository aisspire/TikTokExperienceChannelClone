package com.simulate.tiktok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.simulate.tiktok.ui.screen.ExperienceScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 直接调用我们写好的主界面
            ExperienceScreen()
        }
    }
}
