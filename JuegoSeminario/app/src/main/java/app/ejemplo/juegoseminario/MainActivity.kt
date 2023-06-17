package app.ejemplo.juegoseminario

import AdministradorMejoresJugadas
import android.content.Context
import android.app.Dialog
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
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {
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

        val mediaPlayer = MediaPlayer.create(this, R.raw.sonidocarta)
        val mediaPlayerFinal = MediaPlayer.create(this, R.raw.ganador)
        supportActionBar?.hide() // borra la barra de arriba
        gridLayout = findViewById(R.id.gridLayout) // botones cuadricula
        gridLayout.columnCount = 4

        explanationButton = findViewById(R.id.botonAyuda2) // declaro boton de ayuda
        // Manejar el clic en el botón "Explicación del juego"
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



                            // Modulo guardar jugadas
                            val nuevaJugada = movimientos
                            administradorMejoresJugadas.guardarMejorJugada(nuevaJugada)
                            val mejoresJugadas = administradorMejoresJugadas.obtenerMejoresJugadas()
                            for (jugada in mejoresJugadas) {
                                Log.d("MejoresJugadas", "Jugada: $jugada")
                            }




                            // calculo la eficiencia del jugador
                            // pq puse el toDouble? pq necesito una divicion de reales sino el resultado es 0 siempre
                            var eficiencia = (8.toDouble() /movimientos)*100;
                            // falta converitr el tamaño del decimal
                            val df = DecimalFormat("#") // Define el formato para dos decimales
                            val eficienciaFormateada = df.format(eficiencia) // Aplica el formato a eficiencia
                            // musica ganador
                            mediaPlayerFinal.start()


                            // Crear una alerta personalizada
                            val builder = MaterialAlertDialogBuilder(this)
                                .setTitle(getString(R.string.texto_felicidades))

                            val message = getString(R.string.texto_ganaste_con1) + " $movimientos " + getString (R.string.texto_ganaste_con2) +
                                    " "+getString (R.string.texto_ganaste_con3) + " $eficienciaFormateada%."
                            // Crear una instancia de SpannableString para aplicar el estilo al mensaje
                            val spannableMessage = SpannableString(message)
                            spannableMessage.setSpan(
                                ForegroundColorSpan(Color.WHITE), 0, message.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            builder.setMessage(spannableMessage)
                                .setPositiveButton("OK") { dialog, _ ->
                                    // Acciones al hacer clic en el botón "OK"
                                    dialog.dismiss()
                                }
                                .setBackground(resources.getDrawable(R.drawable.shape))
                            // Obtener el diálogo
                            val dialog = builder.create()
                            // Mostrar el diálogo
                            dialog.show()
                            // Establecer el color del título en blanco
                            dialog.findViewById<TextView>(androidx.appcompat.R.id.alertTitle)?.setTextColor(Color.WHITE)
                            // Establecer el color del botón "OK" en blanco
                            dialog.getButton(Dialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
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