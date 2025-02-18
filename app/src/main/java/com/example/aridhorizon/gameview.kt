package com.example.aridhorizon

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.MotionEvent
import kotlin.random.Random

class GameView : SurfaceView, SurfaceHolder.Callback {

    private val paint = Paint()
    private var personX = 100f // Player stays in the same X position
    private var personY = 800f // Adjusted to be above the ground
    private var isJumping = false
    private var jumpVelocity = 0f
    private val gravity = 3.5f  // Increases falling speed (previously 2.5f)
    private val jumpPower = -50f  // Increases jump speed (previously -40f)


    private var obstacles = mutableListOf<Obstacle>()
    private var obstacleSpeed = 30f
    private var maxObstacles = 5

    private var groundY = 980f // Ground level
    private val groundPaint = Paint()

    var isGameOver = false
        private set

    private var score = 0

    private var backgroundImage: Bitmap? = null
    private var spriteSheet: Bitmap? = null
    private val frameCount = 6
    private var currentFrame = 0
    private val frameTime = 2
    private var frameTick = 0
    private val spriteWidth = 200
    private val spriteHeight = 200

    private var backgroundX = 0f
    private var gameThread: GameThread? = null
    private val soundManager = SoundManager(context)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        holder.addCallback(this)
        groundPaint.color = Color.rgb(0, 50, 0) // Dark Green

        backgroundImage = BitmapFactory.decodeResource(resources, R.drawable.k)
        spriteSheet = BitmapFactory.decodeResource(resources, R.drawable.h)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = GameThread(holder, this)
        gameThread?.setRunning(true)
        gameThread?.start()
        soundManager.playBackgroundMusic() // Play background music when the game starts
        generateObstacle()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        soundManager.stopBackgroundMusic() // Stop background music when the game ends
    }

    private fun generateObstacle() {
        if (obstacles.size < maxObstacles) {
            val xPosition = width.toFloat()
            val obstacleWidth = Random.nextInt(50, 120).toFloat()
            val obstacleHeight = Random.nextInt(50, 100).toFloat()

            obstacles.add(Obstacle(xPosition, groundY - obstacleHeight, obstacleWidth, obstacleHeight))
        }
    }

    private fun checkCollisions(): Boolean {
        for (obstacle in obstacles) {
            if (personX + spriteWidth > obstacle.x && personX < obstacle.x + obstacle.width &&
                personY + spriteHeight > obstacle.y) {

                soundManager.stopBackgroundMusic() // Stop music on game over
                return true
            }
        }
        return false
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (canvas != null) {
            canvas.drawColor(Color.WHITE)

            if (!isGameOver) {
                // Draw scrolling background
                backgroundImage?.let {
                    canvas.drawBitmap(it, backgroundX, 0f, paint)
                    if (backgroundX + it.width < width) {
                        canvas.drawBitmap(it, backgroundX + it.width, 0f, paint)
                    }
                }

                // Move background
                backgroundX -= 7f
                if (backgroundX + backgroundImage!!.width <= 0) {
                    backgroundX = 0f
                }

                // Draw ground
                canvas.drawRect(0f, groundY, width.toFloat(), height.toFloat(), groundPaint)

                // Draw running character sprite animation
                spriteSheet?.let {
                    val frameWidth = it.width / frameCount
                    val srcRect = Rect(frameWidth * currentFrame, 0, frameWidth * (currentFrame + 1), it.height)
                    val dstRect = Rect(personX.toInt(), personY.toInt(), personX.toInt() + spriteWidth, personY.toInt() + spriteHeight)
                    canvas.drawBitmap(it, srcRect, dstRect, paint)

                    frameTick++
                    if (frameTick >= frameTime) {
                        frameTick = 0
                        currentFrame = (currentFrame + 1) % frameCount
                    }
                }

                // Move & Draw Obstacles
                // Adjust difficulty dynamically based on the current score
                obstacleSpeed = when (score) {
                    in 0..57 -> 30f  // Easy
                    in 58..70 -> 40f  // Medium
                    in 71..80 -> 50f  // Hard
                    else -> 60f  // Very Hard
                }

                maxObstacles = when (score) {
                    in 0..33 -> 5
                    in 34..59 -> 7
                    in 60..80 -> 9
                    else -> 10
                }

// Move & Draw Obstacles
                var iterator = obstacles.iterator()
                while (iterator.hasNext()) {
                    val obstacle = iterator.next()
                    obstacle.x -= obstacleSpeed  // Now difficulty is controlled correctly

                    paint.color = Color.RED
                    canvas.drawRect(obstacle.x, obstacle.y, obstacle.x + obstacle.width, obstacle.y + obstacle.height, paint)

                    if (obstacle.x + obstacle.width < 0) {
                        iterator.remove()
                        score++

                        // Increase obstacle speed dynamically based on score
                        obstacleSpeed += 0.5f

                        // Reduce obstacle spacing to make the game harder
                        if (score % 10 == 0) {
                            maxObstacles = (maxObstacles + 1).coerceAtMost(10)  // Max limit to prevent overload
                        }

                        generateObstacle()
                    }



                }

                // Handle jumping
                if (isJumping) {
                    personY += jumpVelocity
                    jumpVelocity += gravity
                    if (personY >= groundY - spriteHeight) {
                        personY = groundY - spriteHeight
                        isJumping = false
                    }
                }

                // Collision Handling
                if (checkCollisions()) {
                    isGameOver = true
                    soundManager.stopBackgroundMusic() // Stop music if game over
                }

                // Display score
                // Display score
                paint.color = Color.BLACK
                paint.textSize = 50f
                paint.textAlign = Paint.Align.LEFT  // Ensure the score stays in the same position after restart
                canvas.drawText("Score: $score", 50f, 100f, paint)

            }

            // Display "Game Over" and restart button if the game is over
            if (isGameOver) {
                paint.textAlign = Paint.Align.CENTER // Center text horizontally

                // Draw 'Game Over' in large text
                paint.color = Color.RED
                paint.textSize = 100f
                canvas.drawText("GAME OVER", width / 2f, height / 3f, paint)

                // Draw the score
                paint.color = Color.BLACK
                paint.textSize = 70f
                canvas.drawText("Score: $score", width / 2f, height / 2.2f, paint)

                // Draw 'Tap to Restart'
                paint.color = Color.BLUE
                paint.textSize = 60f
                canvas.drawText("Tap to Restart", width / 2f, height / 1.8f, paint)
            }

        }
    }

    fun restartGame() {
        isGameOver = false
        personY = groundY - spriteHeight
        obstacles.clear()
        score = 0
        obstacleSpeed = 30f  // Reset obstacle speed to Easy level
        maxObstacles = 5     // Reset max obstacles to Easy level
        generateObstacle()
        backgroundX = 0f

        soundManager.stopBackgroundMusic()
        soundManager.playBackgroundMusic()
    }



    fun jump() {
        if (!isJumping) {
            isJumping = true
            jumpVelocity = jumpPower
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (isGameOver) {
                restartGame()
            } else {
                jump()
            }
        }
        return true
    }

    data class Obstacle(var x: Float, var y: Float, val width: Float, val height: Float)
}
