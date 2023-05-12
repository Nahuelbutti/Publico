package com.example.airhockey

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var player1Paddle: RectF
    private lateinit var player2Paddle: RectF
    private lateinit var puck: RectF
    private lateinit var player1Score: TextView
    private lateinit var player2Score: TextView
    private var player1ScoreValue = 0
    private var player2ScoreValue = 0
    private var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activar pantalla completa y ocultar la barra de título
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(AirHockeyView(this))

        // Inicializar las paletas, la ficha y los marcadores
        player1Paddle = RectF(0f, 0f, 0f, 0f)
        player2Paddle = RectF(0f, 0f, 0f, 0f)
        puck = RectF(0f, 0f, 0f, 0f)
        player1Score = findViewById(R.id.player1_score)
        player2Score = findViewById(R.id.player2_score)

        resetGame()
    }

    private fun resetGame() {
        // Centrar las paletas y la ficha en la pantalla
        val screenX = window.decorView.width.toFloat()
        val screenY = window.decorView.height.toFloat()

        player1Paddle.left = 50f
        player1Paddle.top = screenY / 2 - 200f
        player1Paddle.right = 100f
        player1Paddle.bottom = screenY / 2 + 200f

        player2Paddle.left = screenX - 100f
        player2Paddle.top = screenY / 2 - 200f
        player2Paddle.right = screenX - 50f
        player2Paddle.bottom = screenY / 2 + 200f

        puck.left = screenX / 2 - 50f
        puck.top = screenY / 2 - 50f
        puck.right = screenX / 2 + 50f
        puck.bottom = screenY / 2 + 50f

        // Inicializar la velocidad de la ficha
        puck.velocityX = 10f
        puck.velocityY = 10f
    }

    inner class AirHockeyView(context: Context) : View(context) {
        private val paint = Paint()

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)

            // Dibujar el fondo de la pantalla
            canvas?.drawColor(Color.BLACK)

            // Dibujar las paletas y la ficha
            paint.color = Color.WHITE
            canvas?.drawRect(player1Paddle, paint)
            canvas?.drawRect(player2Paddle, paint)
            canvas?.drawOval(puck, paint)

            // Dibujar los marcadores
            paint.textSize = 60f
            if (puck.intersect(player1Paddle) || puck.intersect(player2Paddle)) {
                puck.velocityX = -puck.velocityX
                puck.velocityY = -puck.velocityY
            }

            player1Paddle.offset(0f, player1Paddle.velocityY)
            player2Paddle.offset(0f, player2Paddle.velocityY)

            gameView.invalidate()

            return@setOnTouchListener true
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post(
        object : Runnable {
            override fun run() {
                updateGameState()
                handler.postDelayed(this, 10)
            }
        })
    }

    private fun updateGameState() {
        puck.offset(puck.velocityX, puck.velocityY)

        if (puck.x < 0) {
            player2ScoreValue++
            player2Score.text = player2ScoreValue.toString()
            resetPuck()
        }

        if (puck.x + puck.width > window.decorView.width) {
            player1ScoreValue++
            player1Score.text = player1ScoreValue.toString()
            resetPuck()
        }

        if (puck.y < 0 || puck.y + puck.height > window.decorView.height) {
            puck.velocityY = -puck.velocityY
        }

        player1Paddle.offset(0f, player1Paddle.velocityY)
        player2Paddle.offset(0f, player2Paddle.velocityY)

        if (player1Paddle.top < 0 || player1Paddle.bottom > window.decorView.height) {
            player1Paddle.offset(0f, -player1Paddle.velocityY)
        }

        if (player2Paddle.top < 0 || player2Paddle.bottom > window.decorView.height) {
            player2Paddle.offset(0f, -player2Paddle.velocityY)
        }

        if (puck.intersect(player1Paddle) || puck.intersect(player2Paddle)) {
            puck.velocityX = -puck.velocityX
            puck.velocityY = -puck.velocityY
        }
    }

    private fun updateGameState() {
        puck.offset(puck.velocityX, puck.velocityY)

        if (puck.x < 0) {
            player2ScoreValue++
            player2Score.text = player2ScoreValue.toString()
            resetPuck()
        }

        if (puck.x + puck.width > window.decorView.width) {
            player1ScoreValue++
            player1Score.text = player1ScoreValue.toString()
            resetPuck()
        }

        if (puck.y < 0 || puck.y + puck.height > window.decorView.height) {
            puck.velocityY = -puck.velocityY
        }

        player1Paddle.offset(0f, player1Paddle.velocityY)
        player2Paddle.offset(0f, player2Paddle.velocityY)

        if (player1Paddle.top < 0 || player1Paddle.bottom > window.decorView.height) {
            player1Paddle.offset(0f, -player1Paddle.velocityY)
        }

        if (player2Paddle.top < 0 || player2Paddle.bottom > window.decorView.height) {
            player2Paddle.offset(0f, -player2Paddle.velocityY)
        }

        if (puck.intersect(player1Paddle) || puck.intersect(player2Paddle)) {
            puck.velocityX = -puck.velocityX
            puck.velocityY = -puck.velocityY
        }

        // add code to handle collisions with walls here
        if (puck.x < 0 || puck.x + puck.width > window.decorView.width) {
            puck.velocityX = -puck.velocityX
        }

        // add code to handle game end here
        if (player1ScoreValue == maxScore || player2ScoreValue == maxScore) {
            endGame()
        }

        gameView.invalidate()
    }

    private fun endGame() {
        // add code to display winner and reset game here
        if (player1ScoreValue > player2ScoreValue) {
            Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show()
        }
        player1ScoreValue = 0
        player2ScoreValue = 0
        player1Score.text = "0"
        player2Score.text = "0"
        resetPuck()
    }
}
