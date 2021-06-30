package ru.netology.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.R

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        findViewById<StatsView>(R.id.stats).data = listOf(
            0.25F,
            0.15F,
            0.25F
        )
    }
}