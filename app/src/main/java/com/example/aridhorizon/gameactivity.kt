package com.example.aridhorizon

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the game view as the content view
        gameView = GameView(this, null)
        setContentView(gameView)

        // Handle touch events for jumping and restarting the game
        gameView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (gameView.isGameOver) {
                    gameView.restartGame()  // Restart game when game over
                } else {
                    gameView.jump()  // Make the player jump
                }
            }
            true
        }
    }
}
