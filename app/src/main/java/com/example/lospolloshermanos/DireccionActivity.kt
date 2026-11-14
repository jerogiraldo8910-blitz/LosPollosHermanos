package com.example.lospolloshermanos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lospolloshermanos.databinding.ActivityDireccionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * PANTALLA 4 — "Veamos dónde vives".
 *  - "Nuevo": limpia el campo para escribir una dirección nueva.
 *  - "Listo": guarda la dirección del usuario y vuelve al menú.
 *
 * ★ La imagen del mapa está en res/layout/activity_direccion.xml (busca "IMAGEN AQUÍ #2").
 */
class DireccionActivity : AppCompatActivity() {

    private lateinit var b: ActivityDireccionBinding
    private val uid by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    private val refDireccion by lazy {
        FirebaseDatabase.getInstance().reference
            .child(Constantes.NODO_USUARIOS).child(uid).child("direccion")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDireccionBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Mostrar la dirección que ya tiene guardada
        refDireccion.get().addOnSuccessListener { snap ->
            val actual = snap.getValue(String::class.java).orEmpty()
            b.tvActual.text = getString(R.string.dir_actual, actual.ifEmpty { "-" })
            b.etDireccion.setText(actual)
        }

        b.btnNuevo.setOnClickListener {
            b.etDireccion.text?.clear()
            b.etDireccion.requestFocus()
        }

        b.btnListo.setOnClickListener {
            val nueva = b.etDireccion.text.toString().trim()
            if (nueva.isEmpty()) {
                toast(R.string.err_campos_vacios)
                return@setOnClickListener
            }
            refDireccion.setValue(nueva)
                .addOnSuccessListener {
                    toast(R.string.msg_direccion_guardada)
                    finish()   // vuelve al menú
                }
                .addOnFailureListener { toast(R.string.err_generico, it.message.orEmpty()) }
        }
    }
}
