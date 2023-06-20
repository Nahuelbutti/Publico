package app.ejemplo.juegoseminario

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Ranking : AppCompatActivity() {

    private lateinit var mejoresJugadas: MutableList<Int>
    private val MAX_MEJORES_JUGADAS = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rankmejoresjugadas)

        val sharedPreferences = getSharedPreferences("Rankings", Context.MODE_PRIVATE)
        val rankingJson = sharedPreferences.getString("MEJORES_JUGADAS", null)

        mejoresJugadas = if (rankingJson != null) {
            val listType = object : TypeToken<MutableList<Int>>() {}.type
            Gson().fromJson(rankingJson, listType)
        } else {
            mutableListOf()
        }

        supportActionBar?.hide()

        val rankingTextView = findViewById<TextView>(R.id.TxtRanking)
        val rankingText = mejoresJugadas.joinToString("\n")
        rankingTextView.text = rankingText

        val botonAtras = findViewById<Button>(R.id.botonAtras)
        botonAtras.setOnClickListener {
            onBackPressed()
        }
    }

    private fun actualizarMejoresJugadas(nuevoRecord: Int) {
        if (mejoresJugadas.size < MAX_MEJORES_JUGADAS) {
            mejoresJugadas.add(nuevoRecord)
        } else {
            val peorRecord = mejoresJugadas.last()
            if (nuevoRecord > peorRecord) {
                mejoresJugadas.remove(peorRecord)
                mejoresJugadas.add(nuevoRecord)
            }
        }

        mejoresJugadas.sort()
        guardarMejoresJugadas()
    }

    private fun guardarMejoresJugadas() {
        val sharedPreferences = getSharedPreferences("Rankings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("MEJORES_JUGADAS", Gson().toJson(mejoresJugadas))
        editor.apply()
    }
}