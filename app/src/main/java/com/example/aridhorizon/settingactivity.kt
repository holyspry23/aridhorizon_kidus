package com.example.aridhorizon  // Ensure this matches your app's package

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat  // Import SwitchCompat

class SettingActivity : AppCompatActivity() {

    private lateinit var soundSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // Ensure correct XML file

        // Initialize SoundManager
        soundManager = SoundManager(this)

        // Initialize SharedPreferences to save sound settings
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE)

        // Find switch in layout
        soundSwitch = findViewById(R.id.soundSwitch)

        // Load saved sound setting
        val isSoundEnabled = sharedPreferences.getBoolean("SoundEnabled", true)
        soundSwitch.isChecked = isSoundEnabled

        // Apply saved sound setting on launch
        updateSoundState(isSoundEnabled)

        // Toggle sound on switch change
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the setting
            sharedPreferences.edit().putBoolean("SoundEnabled", isChecked).apply()

            // Apply the setting to the game
            updateSoundState(isChecked)
        }
    }

    // Function to update sound state globally
    private fun updateSoundState(isEnabled: Boolean) {
        if (isEnabled) {
            soundManager.playBackgroundMusic()
        } else {
            soundManager.pauseBackgroundMusic()
        }
    }
}
