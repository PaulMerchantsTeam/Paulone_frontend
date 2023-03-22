package com.paulmerchants.gold

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun hideStatusBar() {
        this.window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun showStatusBar() {
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


}