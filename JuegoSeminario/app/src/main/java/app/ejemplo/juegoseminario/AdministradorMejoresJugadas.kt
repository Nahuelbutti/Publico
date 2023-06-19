import android.content.Context
import android.util.Log
import com.google.gson.Gson

class AdministradorMejoresJugadas(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("Rankings", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun obtenerMejoresJugadas(): MutableList<Int> {
        val listaJugadasJson = sharedPreferences.getString("MEJORES_JUGADAS", null)
        return if (listaJugadasJson != null) {
            gson.fromJson(listaJugadasJson, Array<Int>::class.java).toMutableList()
        } else {
            mutableListOf()
        }
    }

    fun actualizarMejoresJugadas(nuevaJugada: Int) {
        val mejoresJugadas = obtenerMejoresJugadas()

        if (mejoresJugadas.size < 5 || nuevaJugada < obtenerPeorRegistro(mejoresJugadas)) {
            if (mejoresJugadas.size == 5) {
                mejoresJugadas.removeAt(obtenerPeorRegistroIndex(mejoresJugadas))
            }
            mejoresJugadas.add(nuevaJugada)
            guardarMejoresJugadas(mejoresJugadas)
        }
    }

    private fun obtenerPeorRegistro(mejoresJugadas: List<Int>): Int {
        return mejoresJugadas.minOrNull() ?: 0
    }

    private fun obtenerPeorRegistroIndex(mejoresJugadas: List<Int>): Int {
        val peorRegistro = obtenerPeorRegistro(mejoresJugadas)
        return mejoresJugadas.indexOf(peorRegistro)
    }

    private fun guardarMejoresJugadas(mejoresJugadas: List<Int>) {
        val listaJugadasJson = gson.toJson(mejoresJugadas)
        sharedPreferences.edit().putString("MEJORES_JUGADAS", listaJugadasJson).apply()
    }
}
