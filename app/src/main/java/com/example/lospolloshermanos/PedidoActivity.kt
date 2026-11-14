package com.example.lospolloshermanos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lospolloshermanos.databinding.ActivityPedidoBinding

/**
 * PANTALLA 6 — "Gracias por su compra".
 *
 * ★ La imagen del pedido está en res/layout/activity_pedido.xml (busca "IMAGEN AQUÍ #3").
 */
class PedidoActivity : AppCompatActivity() {

    private lateinit var b: ActivityPedidoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPedidoBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Datos recibidos de CarritoActivity con putExtra
        val total = intent.getDoubleExtra(Constantes.EXTRA_TOTAL, 0.0)
        val pedidoId = intent.getStringExtra(Constantes.EXTRA_PEDIDO_ID).orEmpty()

        b.tvTotal.text = getString(R.string.pedido_total, total.aMoneda())
        b.tvCodigo.text = getString(R.string.pedido_codigo, pedidoId.takeLast(6).uppercase())

        // Vuelve al menú del cliente (que quedó abajo en la pila de pantallas)
        b.btnVolverMenu.setOnClickListener { finish() }
    }
}
