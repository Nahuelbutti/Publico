package app.ejemplo.juegoseminario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Ranking : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rankmejoresjugadas)

        supportActionBar?.hide() // borra la barra de arriba

        val botonAtras = findViewById<Button>(R.id.botonAtras)
        botonAtras.setOnClickListener {
            onBackPressed()}

    }
}