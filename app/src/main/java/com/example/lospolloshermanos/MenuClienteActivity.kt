package com.example.lospolloshermanos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lospolloshermanos.databinding.ActivityMenuClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

/**
 * PANTALLA 3A — Menú del CLIENTE.
 * Lista los productos, deja agregarlos al carrito, cambiar la dirección e ir al carrito.
 *
 * ★ Las fotos de cada plato se ponen con ImagenUtil.kt (busca "IMAGEN AQUÍ #4").
 */
class MenuClienteActivity : AppCompatActivity() {

    private lateinit var b: ActivityMenuClienteBinding
    private lateinit var adapter: ProductoClienteAdapter

    private val db by lazy { FirebaseDatabase.getInstance().reference }
    private val uid by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    private val gestor by lazy { GestorEscuchas(this) }
    private val productos = mutableListOf<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMenuClienteBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Dato recibido de LoginActivity con putExtra
        val nombre = intent.getStringExtra(Constantes.EXTRA_NOMBRE).orEmpty()
        b.tvSaludo.text = getString(R.string.menu_saludo, nombre)

        adapter = ProductoClienteAdapter(productos) { producto -> agregarAlCarrito(producto) }
        b.rvProductos.layoutManager = LinearLayoutManager(this)
        b.rvProductos.adapter = adapter

        b.btnCambiarDireccion.setOnClickListener {
            startActivity(Intent(this, DireccionActivity::class.java))
        }
        b.btnCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
        b.btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }

        escucharDatos()
    }

    private fun escucharDatos() {
        // 1) Productos del menú
        gestor.escuchar(db.child(Constantes.NODO_PRODUCTOS)) { snap ->
            productos.clear()
            for (hijo in snap.children) {
                val producto = hijo.getValue(Producto::class.java) ?: continue
                producto.id = hijo.key.orEmpty()
                productos.add(producto)
            }
            adapter.notifyDataSetChanged()
        }

        // 2) Dirección actual del usuario
        gestor.escuchar(db.child(Constantes.NODO_USUARIOS).child(uid).child("direccion")) { snap ->
            val direccion = snap.getValue(String::class.java) ?: "-"
            b.tvDireccion.text = getString(R.string.menu_direccion, direccion)
        }

        // 3) Cantidad de productos en el carrito (se muestra en el botón)
        gestor.escuchar(db.child(Constantes.NODO_CARRITO).child(uid)) { snap ->
            var total = 0L
            for (hijo in snap.children) {
                total += hijo.child("cantidad").getValue(Long::class.java) ?: 0L
            }
            b.btnCarrito.text = getString(R.string.btn_carrito, total)
        }
    }

    /** Suma 1 a la cantidad del producto en carrito/{uid}/{productoId}. */
    private fun agregarAlCarrito(producto: Producto) {
        val ref = db.child(Constantes.NODO_CARRITO).child(uid).child(producto.id)
        val datos = mapOf(
            "nombre" to producto.nombre,
            "precio" to producto.precio,
            "imagen" to producto.imagen,
            "cantidad" to ServerValue.increment(1)   // suma 1 de forma segura
        )
        ref.updateChildren(datos)
            .addOnSuccessListener { toast(R.string.msg_agregado, producto.nombre) }
            .addOnFailureListener { toast(R.string.err_generico, it.message.orEmpty()) }
    }

    override fun onDestroy() {
        gestor.detenerTodas()
        super.onDestroy()
    }
}
