package com.example.lospolloshermanos

/**
 * Un plato del menú. Se guarda en: productos/{id}
 *
 * "imagen" NO es una foto subida: es el NOMBRE de un archivo que tú pones
 * en res/drawable (sin extensión). Ejemplo: "img_producto_1".
 * Mira ImagenUtil.kt para ver cómo se carga.
 */
data class Producto(
    var id: String = "",
    var nombre: String = "",
    var precio: Double = 0.0,
    var imagen: String = ""
)

/** Un producto dentro del carrito. Se guarda en: carrito/{uid}/{productoId} */
data class ItemCarrito(
    var productoId: String = "",
    var nombre: String = "",
    var precio: Double = 0.0,
    var imagen: String = "",
    var cantidad: Int = 0
)
