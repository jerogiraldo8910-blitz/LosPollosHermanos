package com.example.lospolloshermanos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lospolloshermanos.databinding.ActivityCarritoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

/**
 * PANTALLA 5 — Carrito.
 * Muestra los productos, permite quitarlos, calcula subtotal / IVA / total y paga.
 *
 * ★ Las fotos de cada plato se ponen con ImagenUtil.kt (busca "IMAGEN AQUÍ #4").
 */
class CarritoActivity : AppCompatActivity() {

    private lateinit var b: ActivityCarritoBinding
    private lateinit var adapter: CarritoAdapter

    private val db by lazy { FirebaseDatabase.getInstance().reference }
    private val uid by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    private val gestor by lazy { GestorEscuchas(this) }
    private val items = mutableListOf<ItemCarrito>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = CarritoAdapter(items) { item -> quitar(item) }
        b.rvCarrito.layoutManager = LinearLayoutManager(this)
        b.rvCarrito.adapter = adapter

        b.btnVolver.setOnClickListener { finish() }
        b.btnPagar.setOnClickListener { pagar() }

        gestor.escuchar(db.child(Constantes.NODO_CARRITO).child(uid)) { snap ->
            items.clear()
            for (hijo in snap.children) {
                val item = hijo.getValue(ItemCarrito::class.java) ?: continue
                item.productoId = hijo.key.orEmpty()
                items.add(item)
            }
            adapter.notifyDataSetChanged()
            mostrarTotales()
        }
    }

    /**
     * CÁLCULOS MATEMÁTICOS:
     *   subtotal = suma de (precio × cantidad)   -> multiplicación y suma
     *   iva      = subtotal × 0.19               -> porcentaje
     *   total    = subtotal + iva                -> suma
     */
    private fun calcularTotales(): Triple<Double, Double, Double> {
        val subtotal = items.sumOf { it.precio * it.cantidad }
        val iva = subtotal * Constantes.IVA
        val total = subtotal + iva
        return Triple(subtotal, iva, total)
    }

    private fun mostrarTotales() {
        val (subtotal, iva, total) = calcularTotales()
        b.tvVacio.visibility = if (items.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        b.tvSubtotal.text = getString(R.string.carrito_subtotal, subtotal.aMoneda())
        b.tvIva.text = getString(R.string.carrito_iva, iva.aMoneda())
        b.tvTotal.text = getString(R.string.carrito_total, total.aMoneda())
    }

    /** Elimina el producto del carrito. */
    private fun quitar(item: ItemCarrito) {
        db.child(Constantes.NODO_CARRITO).child(uid).child(item.productoId).removeValue()
            .addOnFailureListener { toast(R.string.err_generico, it.message.orEmpty()) }
    }

    /** Guarda el pedido, vacía el carrito y abre la pantalla de "Gracias por su compra". */
    private fun pagar() {
        if (items.isEmpty()) {
            toast(R.string.carrito_vacio)
            return
        }
        b.btnPagar.isEnabled = false
        val (subtotal, iva, total) = calcularTotales()

        db.child(Constantes.NODO_USUARIOS).child(uid).child("direccion").get()
            .addOnCompleteListener { tarea ->
                val direccion = if (tarea.isSuccessful) tarea.result.getValue(String::class.java).orEmpty() else ""
                val pedidoId = db.child(Constantes.NODO_PEDIDOS).child(uid).push().key
                if (pedidoId == null) {
                    b.btnPagar.isEnabled = true
                    return@addOnCompleteListener
                }

                val lineas = items.associate { item ->
                    item.productoId to mapOf(
                        "nombre" to item.nombre,
                        "precio" to item.precio,
                        "cantidad" to item.cantidad
                    )
                }
                val pedido = mapOf(
                    "items" to lineas,
                    "subtotal" to subtotal,
                    "iva" to iva,
                    "total" to total,
                    "direccion" to direccion,
                    "fecha" to ServerValue.TIMESTAMP
                )

                // Guarda el pedido y borra el carrito en una sola operación
                val cambios = hashMapOf<String, Any?>(
                    "${Constantes.NODO_PEDIDOS}/$uid/$pedidoId" to pedido,
                    "${Constantes.NODO_CARRITO}/$uid" to null
                )
                db.updateChildren(cambios)
                    .addOnSuccessListener {
                        val intent = Intent(this, PedidoActivity::class.java)
                        intent.putExtra(Constantes.EXTRA_TOTAL, total)
                        intent.putExtra(Constantes.EXTRA_PEDIDO_ID, pedidoId)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        b.btnPagar.isEnabled = true
                        toast(R.string.err_generico, it.message.orEmpty())
                    }
            }
    }

    override fun onDestroy() {
        gestor.detenerTodas()
        super.onDestroy()
    }
}
