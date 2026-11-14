package com.example.lospolloshermanos

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.lospolloshermanos.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * PANTALLA 1 — Iniciar sesión.
 *
 * ★ La imagen del logo está en res/layout/activity_login.xml (busca "IMAGEN AQUÍ #1").
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var b: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnIniciarSesion.setOnClickListener { iniciarSesion() }
        b.btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun iniciarSesion() {
        val correo = b.etCorreo.text.toString().trim()
        val clave = b.etClave.text.toString()

        if (correo.isEmpty() || clave.isEmpty()) {
            toast(R.string.err_campos_vacios)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            toast(R.string.err_correo_invalido)
            return
        }

        b.btnIniciarSesion.isEnabled = false
        auth.signInWithEmailAndPassword(correo, clave)
            .addOnSuccessListener { irAlMenu() }
            .addOnFailureListener {
                b.btnIniciarSesion.isEnabled = true
                toast(R.string.err_credenciales)
            }
    }

    /** Decide si va al menú de admin o de cliente y le pasa el nombre con putExtra. */
    private fun irAlMenu() {
        val usuario = auth.currentUser ?: return
        val esAdmin = Constantes.esAdmin(usuario.email)

        FirebaseDatabase.getInstance().reference
            .child(Constantes.NODO_USUARIOS).child(usuario.uid).child("nombre")
            .get()
            .addOnCompleteListener { tarea ->
                val nombreGuardado = if (tarea.isSuccessful) tarea.result.getValue(String::class.java) else null
                val nombre = nombreGuardado ?: if (esAdmin) "Administrador" else usuario.email.orEmpty()

                val destino = if (esAdmin) MenuAdminActivity::class.java else MenuClienteActivity::class.java
                val intent = Intent(this, destino)
                intent.putExtra(Constantes.EXTRA_NOMBRE, nombre)
                startActivity(intent)

                b.btnIniciarSesion.isEnabled = true
                b.etClave.text?.clear()
            }
    }
}
