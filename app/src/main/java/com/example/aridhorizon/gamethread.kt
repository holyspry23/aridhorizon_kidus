package com.example.aridhorizon

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(private val surfaceHolder: SurfaceHolder, private val gameView: GameView) : Thread() {
    private var running = false
    private var targetFPS = 60 // Target Frames per second (FPS)
    private var framePeriod: Long = (1000 / targetFPS).toLong()

    // Flag to signal when to pause the thread
    fun setRunning(running: Boolean) {
        this.running = running
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long

        while (running) {
            startTime = System.nanoTime()

            var canvas: Canvas? = null
            try {
                // Synchronize access to the canvas
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    canvas?.let { gameView.draw(it) } // Call draw in GameView
                }
            } finally {
                // Post the drawn canvas to the surface holder
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }

            // Calculate the time taken for the frame and adjust sleep time
            timeMillis = (System.nanoTime() - startTime) / 1000000
            waitTime = framePeriod - timeMillis

            // Ensure the thread runs at the target FPS, otherwise sleep to throttle
            if (waitTime > 0) {
                try {
                    sleep(waitTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
