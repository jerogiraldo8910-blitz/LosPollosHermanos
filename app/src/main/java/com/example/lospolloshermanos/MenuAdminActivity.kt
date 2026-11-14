package com.example.lospolloshermanos

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lospolloshermanos.databinding.ActivityMenuAdminBinding
import com.example.lospolloshermanos.databinding.DialogProductoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

/**
 * PANTALLA 3B — Panel del ADMINISTRADOR.
 * Ve, añade, edita y elimina productos (Crear / Leer / Actualizar / Eliminar).
 *
 * ★ Las fotos de cada plato se ponen con ImagenUtil.kt (busca "IMAGEN AQUÍ #4").
 */
class MenuAdminActivity : AppCompatActivity() {

    private lateinit var b: ActivityMenuAdminBinding
    private lateinit var adapter: ProductoAdminAdapter

    private val db by lazy { FirebaseDatabase.getInstance().reference }
    private val gestor by lazy { GestorEscuchas(this) }
    private val productos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMenuAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        val nombre = intent.getStringExtra(Constantes.EXTRA_NOMBRE).orEmpty()
        b.tvSaludo.text = getString(R.string.menu_saludo, nombre)

        adapter = ProductoAdminAdapter(
            productos,
            onEditar = { mostrarDialogo(it) },
            onEliminar = { confirmarEliminar(it) }
        )
        b.rvProductos.layoutManager = LinearLayoutManager(this)
        b.rvProductos.adapter = adapter

        b.btnAnadir.setOnClickListener { mostrarDialogo(null) }
        b.btnEjemplos.setOnClickListener { cargarEjemplos() }
        b.btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }

        // LEER: cada vez que cambia productos/ en la base, se refresca la lista
        gestor.escuchar(db.child(Constantes.NODO_PRODUCTOS)) { snap ->
            productos.clear()
            for (hijo in snap.children) {
                val producto = hijo.getValue(Producto::class.java) ?: continue
                producto.id = hijo.key.orEmpty()
                productos.add(producto)
            }
            adapter.notifyDataSetChanged()
        }
    }

    /** CREAR (existente == null) o ACTUALIZAR (existente != null). */
    private fun mostrarDialogo(existente: Producto?) {
        val d = DialogProductoBinding.inflate(layoutInflater)
        if (existente != null) {
            d.etNombre.setText(existente.nombre)
            d.etPrecio.setText(existente.precio.toString())
            d.etImagen.setText(existente.imagen)
        }

        val dialogo = AlertDialog.Builder(this)
            .setTitle(if (existente == null) R.string.dlg_titulo_nuevo else R.string.dlg_titulo_editar)
            .setView(d.root)
            .setPositiveButton(R.string.btn_guardar, null)   // se define abajo para validar sin cerrar
            .setNegativeButton(R.string.btn_cancelar, null)
            .create()

        dialogo.setOnShowListener {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nombre = d.etNombre.text.toString().trim()
                val precio = d.etPrecio.text.toString().trim().replace(",", ".").toDoubleOrNull()
                val imagen = d.etImagen.text.toString().trim()

                if (nombre.isEmpty()) {
                    toast(R.string.err_campos_vacios)
                    return@setOnClickListener
                }
                if (precio == null || precio <= 0.0) {
                    toast(R.string.err_precio_invalido)
                    return@setOnClickListener
                }

                val datos = mapOf(
                    "nombre" to nombre,
                    "precio" to precio,
                    // ★ IMAGEN AQUÍ: si el admin deja el campo vacío se usa img_producto_default
                    "imagen" to imagen.ifEmpty { "img_producto_default" }
                )
                val lista = db.child(Constantes.NODO_PRODUCTOS)
                val destino = if (existente == null) lista.push() else lista.child(existente.id)

                destino.setValue(datos)
                    .addOnSuccessListener {
                        toast(R.string.msg_producto_guardado)
                        dialogo.dismiss()
                    }
                    .addOnFailureListener { toast(R.string.err_generico, it.message.orEmpty()) }
            }
        }
        dialogo.show()
    }

    /** ELIMINAR */
    private fun confirmarEliminar(producto: Producto) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.dlg_confirmar_eliminar, producto.nombre))
            .setPositiveButton(R.string.btn_eliminar) { _, _ ->
                db.child(Constantes.NODO_PRODUCTOS).child(producto.id).removeValue()
                    .addOnSuccessListener { toast(R.string.msg_producto_eliminado) }
                    .addOnFailureListener { toast(R.string.err_generico, it.message.orEmpty()) }
            }
            .setNegativeButton(R.string.btn_cancelar, null)
            .show()
    }

    /**
     * Carga 4 platos de ejemplo (los del boceto) para no empezar con el menú vacío.
     *
     * ╔════════════════════════════════════════════════════════════════╗
     * ║ ★★★ IMAGEN AQUÍ #4 — los nombres "img_producto_1..4" son los   ║
     * ║ archivos de res/drawable que debes poner (ver ImagenUtil.kt).  ║
     * ╚════════════════════════════════════════════════════════════════╝
     */
    private fun cargarEjemplos() {
        val ejemplos = listOf(
            Triple("El favorito de Gus", 15.0, "img_producto_1"),   // ★ IMAGEN AQUÍ
            Triple("Don Claudio", 15.0, "img_producto_2"),          // ★ IMAGEN AQUÍ
            Triple("La abuela", 20.0, "img_producto_3"),            // ★ IMAGEN AQUÍ
            Triple("Don Inés", 20.0, "img_producto_4")              // ★ IMAGEN AQUÍ
        )

        val lista = db.child(Constantes.NODO_PRODUCTOS)
        val lote = HashMap<String, Any>()
        for ((nombre, precio, imagen) in ejemplos) {
            val clave = lista.push().key ?: continue
            lote[clave] = mapOf("nombre" to nombre, "precio" to precio, "imagen" to imagen)
        }

        lista.updateChildren(lote)
            .addOnSuccessListener { toast(R.string.msg_ejemplos_cargados) }
            .addOnFailureListener { toast(R.string.err_generico, it.message.orEmpty()) }
    }

    override fun onDestroy() {
        gestor.detenerTodas()
        super.onDestroy()
    }
}
