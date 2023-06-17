import android.content.Context
import android.util.Log


class AdministradorMejoresJugadas(private val context: Context) {

    private val PREFS_NAME = "MejoresJugadas"
    private val PREFS_KEY_JUGADA = "jugada"

    fun guardarMejorJugada(jugada: Int) {

        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        // Obtener las mejores jugadas existentes
        val mejoresJugadas = obtenerMejoresJugadas()

        // Agregar la nueva jugada a la lista
        mejoresJugadas.add(jugada)

        // Ordenar las jugadas de mayor a menor
        mejoresJugadas.sortDescending()

        // Mantener solo las primeras 5 jugadas
        val mejoresJugadasLimitadas = mejoresJugadas.take(5)

        // Guardar las mejores jugadas en las preferencias
        for (i in 0 until mejoresJugadasLimitadas.size) {
            editor.putInt(PREFS_KEY_JUGADA + i, mejoresJugadasLimitadas[i])
        }

        // Guardar el número total de jugadas almacenadas
        editor.putInt("jugadas_total", mejoresJugadasLimitadas.size)

        // Aplicar los cambios
        editor.apply()
    }

    fun obtenerMejoresJugadas(): MutableList<Int> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val mejoresJugadas: MutableList<Int> = mutableListOf()

        // Obtener el número total de jugadas almacenadas
        val jugadasTotal = sharedPreferences.getInt("jugadas_total", 0)

        // Obtener las mejores jugadas desde las preferencias
        for (i in 0 until jugadasTotal) {
            val jugada = sharedPreferences.getInt(PREFS_KEY_JUGADA + i, 0)
            mejoresJugadas.add(jugada)
        }

        return mejoresJugadas
    }
    fun imprimirMejoresJugadas() {
        val mejoresJugadas = obtenerMejoresJugadas()
        for (jugada in mejoresJugadas) {
            Log.d("MejoresJugadas", "Jugada: $jugada")
            //R.id.TxtRanking(obtenerMejoresJugadas())
        }
    }
}

