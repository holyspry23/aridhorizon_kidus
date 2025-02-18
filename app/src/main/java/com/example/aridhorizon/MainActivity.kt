package com.example.aridhorizon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ensure this matches your XML file name

        val startGameButton = findViewById<Button>(R.id.startGameButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        startGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java) // ✅ FIXED: Corrected class name
            startActivity(intent)
        }
    }
}
