package app.ejemplo.juegoseminario

import AdministradorMejoresJugadas
import android.content.Context
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {
    private lateinit var RankingsButton: ImageButton
    private lateinit var explanationButton: ImageButton
    private lateinit var gridLayout: GridLayout
    private var movimientos: Int = 0
    private var numCartasCompletadas = 0
    private var ultimosBotonesSeleccionados: MutableList<Button> = mutableListOf()
    private var cartas: MutableList<Int> = mutableListOf(
        1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8
    ).shuffled() as MutableList<Int>

    // Dentro de la clase MainActivity
    private lateinit var administradorMejoresJugadas: AdministradorMejoresJugadas // guardar jugadas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        administradorMejoresJugadas = AdministradorMejoresJugadas(this) // guardar jugadas
        actualizarRecord()

        val mediaPlayer = MediaPlayer.create(this, R.raw.sonidocarta)
        val mediaPlayerFinal = MediaPlayer.create(this, R.raw.ganador)
        supportActionBar?.hide() // borra la barra de arriba
        gridLayout = findViewById(R.id.gridLayout) // botones cuadricula
        gridLayout.columnCount = 4

        RankingsButton = findViewById(R.id.botonRanking2) // declaro boton rankings
        // Manejar el click del boton rankings
        RankingsButton.setOnClickListener{
            val intent = Intent(this, Ranking::class.java)
            startActivity(intent)
            mediaPlayer.start()
        }

        explanationButton = findViewById(R.id.botonAyuda2) // declaro boton de ayuda
        // Manejar el click del botón ayuda
        explanationButton.setOnClickListener {
            val intent = Intent(this, Ayuda::class.java)
            startActivity(intent)
            mediaPlayer.start()
        }

        // Manejar boton menu
        val BotonDeMenu = findViewById<Button>(R.id.botonMenu)
        BotonDeMenu.setOnClickListener {
            val opcion = Intent(this, MenuPrincipal::class.java)
            startActivity(opcion)
            mediaPlayer.start()
            mediaPlayerFinal.stop()}

        // Manejar boton reset
        val botonReset = findViewById<Button>(R.id.reiniciarButton)
        botonReset.setOnClickListener { reiniciarJuego()
            mediaPlayerFinal.stop()}

        for (i in 0 until cartas.size) {
            val boton = Button(this)
            boton.text = ""
            boton.setBackgroundResource(R.drawable.shape)
            boton.isSoundEffectsEnabled = false // desactiva sonido por defecto para que no se solapen
            boton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40f) // fija el tamaño del boton para que no varie
            boton.setOnClickListener { view ->
                boton.textSize = 40f // le estoy definiendo tamaño a las letras de los botones
                boton.setTextColor(Color.parseColor("#ffffff")) // le defino el color a las letras de los botones
                boton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40f) // fija el tamaño del boton para que no varie
                if (ultimosBotonesSeleccionados.size < 2 && boton.text == "") {
                    mediaPlayer.start()
                    // Si hay menos de dos botones seleccionados y este botón no ha sido dado vuelta,
                    // se muestra su valor y se agrega a la lista de botones seleccionados
                    boton.text = cartas[i].toString()
                    ultimosBotonesSeleccionados.add(boton)
                }
                if (ultimosBotonesSeleccionados.size == 2) {
                    // Si hay dos botones seleccionados, se verifica si sus valores son iguales
                    movimientos++
                    actualizarMovimientos()
                    val ultimoValor = cartas[cartas.indexOf(ultimosBotonesSeleccionados[1].text.toString().toInt())]
                    val valorActual = cartas[cartas.indexOf(ultimosBotonesSeleccionados[0].text.toString().toInt())]
                    if (ultimoValor == valorActual) {
                        numCartasCompletadas+=2 // suma las cartas para saber cuantas se completaron
                        // si completaste el juego...
                        if (numCartasCompletadas==16){

                            // logica para generar la lista de records
                            val nuevasJugadas = administradorMejoresJugadas.obtenerMejoresJugadas()
                            // Agregar el nuevo récord a la lista
                            nuevasJugadas.add(movimientos)
                            // Ordenar la lista en orden ascendente
                            nuevasJugadas.sort()
                            // Limitar la lista a 5 elementos
                            if (nuevasJugadas.size > 5) {
                                nuevasJugadas.removeAt(nuevasJugadas.size - 1)
                            }
                            // Guardar los datos en SharedPreferences
                            val sharedPreferences = getSharedPreferences("Rankings", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("MEJORES_JUGADAS", Gson().toJson(nuevasJugadas))
                            editor.apply()

                            // calculo la eficiencia del jugador
                            // pq puse el toDouble? pq necesito una divicion de reales sino el resultado es 0 siempre
                            var eficiencia = (8.toDouble() /movimientos)*100;
                            // falta converitr el tamaño del decimal
                            val df = DecimalFormat("#") // Define el formato para dos decimales
                            val eficienciaFormateada = df.format(eficiencia) // Aplica el formato a eficiencia

                            // musica ganador
                            mediaPlayerFinal.start()

                            // alerta fin del juego
                            val builder = MaterialAlertDialogBuilder(this)
                                .setTitle(getString(R.string.texto_felicidades))

                            val message = getString(R.string.texto_ganaste_con1) + " $movimientos " + getString(R.string.texto_ganaste_con2) +
                                    " " + getString(R.string.texto_ganaste_con3) + " $eficienciaFormateada%."
                            val spannableMessage = SpannableString(message)
                            spannableMessage.setSpan(
                                ForegroundColorSpan(Color.WHITE),
                                0,
                                message.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            builder.setMessage(spannableMessage)
                                .setBackground(resources.getDrawable(R.drawable.shape))
                                .setPositiveButton(getString(R.string.texto_ranking)) { dialog, _ ->
                                    abrirRankingsActivity()
                                    dialog.dismiss()
                                }
                                .setNegativeButton(R.string.texto_reiniciar) { dialog, _ ->
                                    dialog.dismiss()
                                    reiniciarJuego()
                                    mediaPlayerFinal.stop()
                                }
                                .setNeutralButton(R.string.texto_compartir) { dialog, _ ->
                                    val textoCompartir = getString(R.string.texto_TituloCompartir)+"\n"+
                                                         getString(R.string.texto_ganaste_con1) + " $movimientos " + getString(R.string.texto_ganaste_con2) + " " + getString(R.string.texto_ganaste_con3) + " $eficienciaFormateada%. "+"\n"+
                                                         getString(R.string.texto_spam) + "  https://play.google.com/store/apps/details?id=app.ejemplo.juegoseminario&hl=es_AR&gl=US";
                                    val intent = Intent(Intent.ACTION_SEND)
                                    intent.type = "text/plain"
                                    intent.putExtra(Intent.EXTRA_TEXT, textoCompartir)
                                    startActivity(Intent.createChooser(intent, "Compartir puntaje"))
                                    dialog.dismiss()
                                }

                            val dialog = builder.create()
                            // invocacion al dialogo
                            dialog.show()

                            dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)?.setTextColor(Color.WHITE)
                            dialog.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
                            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE)
                            dialog.getButton(DialogInterface.BUTTON_NEUTRAL)?.setTextColor(Color.WHITE)
                        }
                        // Si son iguales, los botones quedan visibles y se quitan de la lista de botones seleccionados

                        if (ultimosBotonesSeleccionados.size >= 2) {
                            ultimosBotonesSeleccionados[1].setBackgroundResource(R.drawable.botonseleccionadocorrecto)
                            ultimosBotonesSeleccionados[0].setBackgroundResource(R.drawable.botonseleccionadocorrecto)
                            // Espera de 500 milisegundos antes de volver al color original

                            val copia1boton = ultimosBotonesSeleccionados[1];
                            val copia2boton = ultimosBotonesSeleccionados[0]

                            val handler = Handler()
                            handler.postDelayed({ // Do something after 5s = 5000ms
                                copia1boton.setBackgroundResource(R.drawable.botonseleccionado)
                                copia2boton.setBackgroundResource(R.drawable.botonseleccionado)
                            }, 500)

                            ultimosBotonesSeleccionados.clear()
                        }
                    } else {
                        // Si no son iguales, los botones vuelven a estar ocultos después de un breve
                        // retraso para dar tiempo a visualizarlos
                        val botonUno = ultimosBotonesSeleccionados[0]
                        val botonDos = ultimosBotonesSeleccionados[1]

                        ultimosBotonesSeleccionados[1].setBackgroundResource(R.drawable.botonerrado)
                        ultimosBotonesSeleccionados[0].setBackgroundResource(R.drawable.botonerrado)

                        // bloqueamos los botones para que el usuario no pueda ingresar nada durante los 500ms de muestra
                        for (i in 0 until gridLayout.childCount) {
                            val boton = gridLayout.getChildAt(i) as Button
                            if (boton.text == "" || boton.background.constantState != resources.getDrawable(
                                    R.drawable.botonseleccionado
                                ).constantState) {
                                boton.isClickable = false
                            }
                        }
                        botonUno.postDelayed({
                            ultimosBotonesSeleccionados[1].setBackgroundResource(R.drawable.shape)
                            ultimosBotonesSeleccionados[0].setBackgroundResource(R.drawable.shape)
                            botonUno.text = ""
                            botonDos.text = ""
                            ultimosBotonesSeleccionados.clear()
                            for (i in 0 until gridLayout.childCount) {
                                val boton = gridLayout.getChildAt(i) as Button
                                boton.isClickable = true
                            }
                        }, 500)
                    }
                }
            }
            gridLayout.addView(boton)
        }
    }

    private fun actualizarRecord(){
        // obtener el record para mostrarlo en la actividad
        val sharedPreferences = getSharedPreferences("Rankings", Context.MODE_PRIVATE)
        val mejoresJugadasJson = sharedPreferences.getString("MEJORES_JUGADAS", null)
        val mejoresJugadas: List<Int> = if (mejoresJugadasJson != null) {
            Gson().fromJson(mejoresJugadasJson, object : TypeToken<List<Int>>() {}.type)
        } else {
            emptyList()
        }
        val mejorRecord = if (mejoresJugadas.isNotEmpty()) {
            mejoresJugadas[0] // Obtener el primer elemento de la lista (mejor récord)
        } else {
            0
        }

        val textView = findViewById<TextView>(R.id.MejorRecord)
        textView.text = "Record: $mejorRecord"
    }

    private fun abrirRankingsActivity() {
        val intent = Intent(this, Ranking::class.java)
        startActivity(intent)
    }

    private fun  actualizarMovimientos() {
        val textView = findViewById<TextView>(R.id.movimientosTextView)
        val mov = getString(R.string.texto_movimiento_archivo)
        textView.text = "$mov $movimientos"
    }

    private fun reiniciarJuego() {
        val mediaPlayerReset = MediaPlayer.create(this, R.raw.reset)
        mediaPlayerReset.start()
        numCartasCompletadas = 0
        movimientos = 0
        actualizarMovimientos()
        ultimosBotonesSeleccionados.clear()
        cartas.shuffle()
        for (i in 0 until gridLayout.childCount) {
            val boton = gridLayout.getChildAt(i) as Button
            boton.setBackgroundResource(R.drawable.shape)// reinicia el color de los botones
            boton.text = ""
            boton.isClickable = true
        }
    }
}