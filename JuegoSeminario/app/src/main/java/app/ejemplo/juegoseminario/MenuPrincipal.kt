package app.ejemplo.juegoseminario

import AdministradorMejoresJugadas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.preference.PreferenceManager
import android.provider.MediaStore.Audio.Media
import android.service.autofill.OnClickAction
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class MenuPrincipal : AppCompatActivity() {
    private lateinit var gameNameTextView: TextView
    private lateinit var rankingButton: Button
    private lateinit var explanationButton: Button
    private lateinit var playButton: Button
    private lateinit var pauseMusic: ImageButton
    private lateinit var mediaPlayerFondo: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private var isMusicPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        supportActionBar?.hide() // Ocultar la barra de acción

        // Obtener referencias a los elementos de la interfaz
        rankingButton = findViewById(R.id.botonRankin)
        gameNameTextView = findViewById(R.id.MenuTexto)
        explanationButton = findViewById(R.id.botonAyuda)
        playButton = findViewById(R.id.botonJugar)
        pauseMusic = findViewById(R.id.botonPausaMusica)

        // Establecer el nombre del juego
        gameNameTextView.text = getString(R.string.titulo_menu)

        // Inicializar el reproductor de música
        mediaPlayerFondo = MediaPlayer.create(this, R.raw.musicabackground)
        mediaPlayerFondo.isLooping = true

        // Inicializar SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Restaurar el estado de reproducción de la música
        isMusicPlaying = sharedPreferences.getBoolean("isMusicPlaying", true)

        // Establecer el estado del botón de pausa/reproducir música
        updatePauseButtonState()

        // Manejar el clic en el botón de pausa/reproducir música
        pauseMusic.setOnClickListener {
            isMusicPlaying = !isMusicPlaying

            if (isMusicPlaying) {
                mediaPlayerFondo.start()
            } else {
                mediaPlayerFondo.pause()
            }

            // Guardar el estado de reproducción de la música en SharedPreferences
            sharedPreferences.edit()
                .putBoolean("isMusicPlaying", isMusicPlaying)
                .apply()

            // Actualizar el estado del botón de pausa/reproducir música
            updatePauseButtonState()
        }

        // Manejar el clic en el botón "boton ayuda"
        explanationButton.setOnClickListener {
            val intent = Intent(this, Ayuda::class.java)
            startActivity(intent)
        }

        // Manejar el clic en el botón "Jugar"
        playButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        rankingButton.setOnClickListener{
            val intent = Intent(this, Ranking::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isMusicPlaying && !mediaPlayerFondo.isPlaying) {
            mediaPlayerFondo.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayerFondo.isPlaying) {
            mediaPlayerFondo.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerFondo.release()
    }

    private fun updatePauseButtonState() {
        if (isMusicPlaying) {
            pauseMusic.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
        } else {
            pauseMusic.setImageResource(android.R.drawable.ic_lock_silent_mode)
        }
    }
}
