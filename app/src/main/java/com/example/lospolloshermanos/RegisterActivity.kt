package com.example.lospolloshermanos

import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.lospolloshermanos.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * PANTALLA 2 — Registrarse.
 * Solo crea cuentas de CLIENTE (las de admin se definen en Constantes.ADMIN_CORREOS).
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var b: ActivityRegisterBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnCrearCuenta.setOnClickListener { crearCuenta() }
        b.btnVolver.setOnClickListener { finish() }
    }

    private fun crearCuenta() {
        val nombre = b.etNombre.text.toString().trim()
        val apellidos = b.etApellidos.text.toString().trim()
        val cedula = b.etCedula.text.toString().trim()
        val direccion = b.etDireccion.text.toString().trim()
        val correo = b.etCorreo.text.toString().trim()
        val clave = b.etClave.text.toString()

        // ---- Validaciones ----
        if (nombre.isEmpty() || apellidos.isEmpty() || cedula.isEmpty() ||
            direccion.isEmpty() || correo.isEmpty() || clave.isEmpty()
        ) {
            toast(R.string.err_campos_vacios)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            toast(R.string.err_correo_invalido)
            return
        }
        if (Constantes.esAdmin(correo)) {
            toast(R.string.err_correo_reservado)
            return
        }
        if (clave.length < 6) {
            toast(R.string.err_clave_corta)
            return
        }
        if (!b.chkTerminos.isChecked) {
            toast(R.string.err_terminos)
            return
        }

        // ---- Crear usuario en Authentication y guardar su perfil en la base de datos ----
        b.btnCrearCuenta.isEnabled = false
        auth.createUserWithEmailAndPassword(correo, clave)
            .addOnSuccessListener { resultado ->
                val uid = resultado.user?.uid ?: return@addOnSuccessListener
                val datos = mapOf(
                    "nombre" to nombre,
                    "apellidos" to apellidos,
                    "cedula" to cedula,
                    "direccion" to direccion,
                    "correo" to correo,
                    "rol" to "cliente"
                )
                FirebaseDatabase.getInstance().reference
                    .child(Constantes.NODO_USUARIOS).child(uid)
                    .setValue(datos)
                    .addOnSuccessListener {
                        toast(R.string.msg_cuenta_creada)
                        auth.signOut()   // el usuario debe iniciar sesión desde el login
                        finish()
                    }
                    .addOnFailureListener {
                        b.btnCrearCuenta.isEnabled = true
                        toast(R.string.err_generico, it.message.orEmpty())
                    }
            }
            .addOnFailureListener {
                b.btnCrearCuenta.isEnabled = true
                toast(R.string.err_registro, it.message.orEmpty())
            }
    }
}
