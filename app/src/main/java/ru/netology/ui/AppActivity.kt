package ru.netology.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.R

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        findViewById<StatsView>(R.id.stats).data = listOf(
            500F,
            100F,
            500F,
            250F,
        )
    }
}