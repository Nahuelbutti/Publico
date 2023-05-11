package app.juego.airhockey
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity() {

    private lateinit var player1Score: TextView
    private lateinit var player2Score: TextView
    private lateinit var puck: Puck
    private lateinit var player1Paddle: ImageView
    private lateinit var player2Paddle: ImageView

    private var player1ScoreValue = 0
    private var player2ScoreValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player1Score = findViewById(R.id.player1Score)
        player2Score = findViewById(R.id.player2Score)
        puck = findViewById(R.id.puck)
        player1Paddle = findViewById(R.id.player1Paddle)
        player2Paddle = findViewById(R.id.player2Paddle)

        // Posiciones iniciales
        player1Paddle.x = 0f
        player1Paddle.y = 0f
        player2Paddle.x = 0f
        player2Paddle.y = 0f
        puck.x = 0f
        puck.y = 0f

        // Control de movimiento
        player1Paddle.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val x = motionEvent.rawX - player1Paddle.width / 2
                val y = motionEvent.rawY - player1Paddle.height / 2
                player1Paddle.x = x
                player1Paddle.y = y
            }
            true
        }

        player2Paddle.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                val x = motionEvent.rawX - player2Paddle.width / 2
                val y = motionEvent.rawY - player2Paddle.height / 2
                player2Paddle.x = x
                player2Paddle.y = y
            }
            true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Movimiento de la ficha
        val x = event?.rawX
        val y = event?.rawY
        puck.x = x!! - puck.width / 2
        puck.y = y!! - puck.height / 2

        // Detección de colisiones
        if (puck.x < 0 || puck.x + puck.width > window.decorView.width) {
            puck.x = puck.x.coerceAtLeast(0f).coerceAtMost(window.decorView.width - puck.width.toFloat())
            puck.velocityX = -puck.velocityX
        }

        if (puck.y < 0 || puck.y + puck.height > window.decorView.height) {
            puck.y = puck.y.coerceAtLeast(0f).coerceAtMost(window.decorView.height - puck.height.toFloat())
            puck.velocityY = -puck.velocityY
        }

        if (puck.intersect(player1Paddle) || puck.intersect(player2Paddle)) {
            puck.velocityX = -puck.velocityX
            puck.velocityY = -puck.velocityY
        }

        if (puck.x < player1Paddle.width && puck.y > player1Paddle.y && puck.y < player1Paddle.y + player1Paddle.height) {
            player2ScoreValue++
            player2Score.text = player2ScoreValue.toString()

            resetGame()
        }

        if (puck.x + puck.width > player2Paddle.x && puck.y > player2Paddle.y && puck.y < player2Paddle.y + player2Paddle.height) {
            player1ScoreValue++
            player1Score.text = player1ScoreValue.toString()
            resetGame()
        }

        return super.onTouchEvent(event)
    }

    private fun resetGame() {
        puck.x = (window.decorView.width / 2 - puck.width / 2).toFloat()
        puck.y = (window.decorView.height / 2 - puck.height / 2).toFloat()
        puck.velocityX = 0f
        puck.velocityY = 0f
        player1Paddle.x = 0f
        player1Paddle.y = (window.decorView.height / 2 - player1Paddle.height / 2).toFloat()
        player2Paddle.x = (window.decorView.width - player2Paddle.width).toFloat()
        player2Paddle.y = (window.decorView.height / 2 - player2Paddle.height / 2).toFloat()
    }

    private fun View.intersect(view: View): Boolean {
        val rect1 = IntArray(2)
        val rect2 = IntArray(2)
        this.getLocationOnScreen(rect1)
        view.getLocationOnScreen(rect2)
        return rect1[0] < rect2[0] + view.width && rect1[0] + this.width > rect2[0] && rect1[1] < rect2[1] + view.height && rect1[1] + this.height > rect2[1]
    }

    class Puck(context: Context) : AppCompatImageView(context) {

        var velocityX: Float = 0f
            set(value) {
                field = value
                animate().xBy(value).start()
            }

        var velocityY: Float = 0f
            set(value) {
                field = value
                animate().yBy(value).start()
            }
    }
}
