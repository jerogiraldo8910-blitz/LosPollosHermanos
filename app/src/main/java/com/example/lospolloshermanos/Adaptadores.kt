package com.example.lospolloshermanos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lospolloshermanos.databinding.ItemCarritoBinding
import com.example.lospolloshermanos.databinding.ItemProductoAdminBinding
import com.example.lospolloshermanos.databinding.ItemProductoClienteBinding

/*
 * Los 3 adaptadores dibujan filas de lista. En todos, la foto del producto se
 * pone con  cargarImagenProducto(...)  → ★ IMAGEN AQUÍ #4 (ver ImagenUtil.kt)
 */

/** Fila del menú del CLIENTE: foto + nombre + precio + botón "+". */
class ProductoClienteAdapter(
    private val lista: List<Producto>,
    private val onAgregar: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoClienteAdapter.VH>() {

    class VH(val b: ItemProductoClienteBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemProductoClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = lista[position]
        holder.b.imgProducto.cargarImagenProducto(p.imagen)   // ★ IMAGEN AQUÍ #4
        holder.b.tvNombre.text = p.nombre
        holder.b.tvPrecio.text = p.precio.aMoneda()
        holder.b.btnAgregar.setOnClickListener { onAgregar(p) }
    }
}

/** Fila del panel ADMIN: foto + nombre + precio + botones Editar / Eliminar. */
class ProductoAdminAdapter(
    private val lista: List<Producto>,
    private val onEditar: (Producto) -> Unit,
    private val onEliminar: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdminAdapter.VH>() {

    class VH(val b: ItemProductoAdminBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemProductoAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = lista[position]
        holder.b.imgProducto.cargarImagenProducto(p.imagen)   // ★ IMAGEN AQUÍ #4
        holder.b.tvNombre.text = p.nombre
        holder.b.tvPrecio.text = p.precio.aMoneda()
        holder.b.btnEditar.setOnClickListener { onEditar(p) }
        holder.b.btnEliminar.setOnClickListener { onEliminar(p) }
    }
}

/** Fila del CARRITO: foto + nombre + "2 x $15.00" + subtotal de la línea + botón Quitar. */
class CarritoAdapter(
    private val lista: List<ItemCarrito>,
    private val onQuitar: (ItemCarrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.VH>() {

    class VH(val b: ItemCarritoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCarritoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]
        val context = holder.b.root.context
        holder.b.imgProducto.cargarImagenProducto(item.imagen)   // ★ IMAGEN AQUÍ #4
        holder.b.tvNombre.text = item.nombre
        holder.b.tvCantidad.text = context.getString(
            R.string.carrito_linea, item.cantidad, item.precio.aMoneda()
        )
        holder.b.tvSubtotalLinea.text = (item.precio * item.cantidad).aMoneda()
        holder.b.btnQuitar.setOnClickListener { onQuitar(item) }
    }
}
