package com.example.lospolloshermanos

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import java.util.Locale

/**
 * Valores fijos de la app.
 */
object Constantes {

    // ╔══════════════════════════════════════════════════════════════╗
    // ║  CUENTAS DE ADMINISTRADOR (según el boceto: "las admin se    ║
    // ║  crean en el código").                                       ║
    // ║  1) Escribe aquí los correos que serán admin.                ║
    // ║  2) Crea esas mismas cuentas a mano en la consola de         ║
    // ║     Firebase → Authentication → Users → Add user.            ║
    // ║  3) Usa el mismo correo en las reglas de la base de datos    ║
    // ║     (archivo firebase_rules.json).                           ║
    // ╚══════════════════════════════════════════════════════════════╝
    val ADMIN_CORREOS = listOf(
        "admin@lospollos.com"
    )

    const val IVA = 0.19

    // Nodos de Realtime Database
    const val NODO_USUARIOS = "usuarios"
    const val NODO_PRODUCTOS = "productos"
    const val NODO_CARRITO = "carrito"
    const val NODO_PEDIDOS = "pedidos"

    // Claves para pasar datos entre pantallas con putExtra
    const val EXTRA_NOMBRE = "extra_nombre"
    const val EXTRA_TOTAL = "extra_total"
    const val EXTRA_PEDIDO_ID = "extra_pedido_id"

    fun esAdmin(correo: String?): Boolean =
        correo != null && ADMIN_CORREOS.any { it.equals(correo.trim(), ignoreCase = true) }
}

/** Muestra un mensaje corto usando un texto de strings.xml. */
fun Context.toast(@StringRes id: Int, vararg args: Any) {
    Toast.makeText(this, getString(id, *args), Toast.LENGTH_SHORT).show()
}

/** Da formato de dinero con 2 decimales: 15.0 -> "$15.00" */
fun Double.aMoneda(): String = "\$" + String.format(Locale.US, "%.2f", this)
