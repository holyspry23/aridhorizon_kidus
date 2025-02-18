
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

class Obstacle(
    var x: Float,       // X position of the obstacle
    var y: Float,       // Y position of the obstacle
    var width: Float,   // Width of the obstacle (used for collision detection)
    var height: Float,  // Height of the obstacle (used for collision detection)
    var speed: Float,   // Speed at which the obstacle moves (negative value for leftward movement)
    var type: Int       // Different obstacle types (0 to 4)
) {
    private val paint = Paint()

    init {
        // Set different colors for different obstacles
        paint.color = when (type) {
            0 -> Color.RED      // Rectangular obstacle
            1 -> Color.BLUE     // Circular obstacle
            2 -> Color.GREEN    // Triangular obstacle
            3 -> Color.YELLOW   // Pentagon obstacle
            else -> Color.MAGENTA // Hexagonal obstacle
        }
        paint.style = Paint.Style.FILL
    }

    // Update the obstacle's position by moving it leftward
    fun update() {
        x -= speed // Update obstacle position based on its speed
    }

    // Draw the obstacle on the canvas
    fun draw(canvas: Canvas) {
        when (type) {
            0 -> drawRectangle(canvas)  // Draw rectangle for type 0
            1 -> drawCircle(canvas)     // Draw circle for type 1
            2 -> drawTriangle(canvas)   // Draw triangle for type 2
            3 -> drawPentagon(canvas)   // Draw pentagon for type 3
            4 -> drawHexagon(canvas)    // Draw hexagon for type 4
        }
    }

    // Draw a rectangular obstacle
    private fun drawRectangle(canvas: Canvas) {
        canvas.drawRect(x, y, x + width, y + height, paint)
    }

    // Draw a circular obstacle
    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(x + width / 2, y + height / 2, width / 2, paint)
    }

    // Draw a triangular obstacle
    private fun drawTriangle(canvas: Canvas) {
        val path = Path()
        path.moveTo(x + width / 2, y) // Top point
        path.lineTo(x, y + height)     // Bottom left
        path.lineTo(x + width, y + height) // Bottom right
        path.close()
        canvas.drawPath(path, paint)
    }

    // Draw a pentagonal obstacle
    private fun drawPentagon(canvas: Canvas) {
        val path = Path()
        path.moveTo(x + width / 2, y) // Top
        path.lineTo(x, y + height / 3)
        path.lineTo(x + width / 4, y + height)
        path.lineTo(x + (3 * width) / 4, y + height)
        path.lineTo(x + width, y + height / 3)
        path.close()
        canvas.drawPath(path, paint)
    }

    // Draw a hexagonal obstacle
    private fun drawHexagon(canvas: Canvas) {
        val path = Path()
        val midX = x + width / 2
        val midY = y + height / 2
        val radius = width / 2

        // Draw the six sides of the hexagon
        for (i in 0 until 6) {
            val angle = Math.toRadians((60 * i).toDouble()).toFloat()
            val xPoint = midX + radius * Math.cos(angle.toDouble()).toFloat()
            val yPoint = midY + radius * Math.sin(angle.toDouble()).toFloat()
            if (i == 0) {
                path.moveTo(xPoint, yPoint)
            } else {
                path.lineTo(xPoint, yPoint)
            }
        }
        path.close()
        canvas.drawPath(path, paint)
    }
}
