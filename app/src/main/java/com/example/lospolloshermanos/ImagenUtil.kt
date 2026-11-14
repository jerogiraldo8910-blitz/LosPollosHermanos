package com.example.lospolloshermanos

import android.widget.ImageView

/*
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  ★★★  IMAGEN AQUÍ #4 — FOTOS DE LOS PRODUCTOS (PLATOS)  ★★★          ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  Cada producto tiene un campo "imagen" con el nombre de un archivo   ║
 * ║  de la carpeta  app/src/main/res/drawable/                           ║
 * ║                                                                      ║
 * ║  Archivos que debes poner (png o jpg, nombre en minúsculas):         ║
 * ║     img_producto_1.png   → "El favorito de Gus"                      ║
 * ║     img_producto_2.png   → "Don Claudio"                             ║
 * ║     img_producto_3.png   → "La abuela"                               ║
 * ║     img_producto_4.png   → "Don Inés"                                ║
 * ║     img_producto_default.png → se usa si un producto no tiene foto   ║
 * ║                                                                      ║
 * ║  IMPORTANTE: en esa carpeta ya hay archivos de marcador con el       ║
 * ║  mismo nombre pero terminados en .xml (cuadro gris con una X).       ║
 * ║  BÓRRALOS cuando agregues tu imagen, o Android Studio marcará        ║
 * ║  error de "recurso duplicado".                                       ║
 * ║                                                                      ║
 * ║  Para productos nuevos: copia tu imagen a res/drawable (ej:          ║
 * ║  img_pollo_frito.png) y en el panel de admin escribe en el campo     ║
 * ║  "Imagen" el nombre SIN extensión:  img_pollo_frito                  ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */
fun ImageView.cargarImagenProducto(nombreDrawable: String?) {
    val id = if (nombreDrawable.isNullOrBlank()) {
        0
    } else {
        resources.getIdentifier(nombreDrawable, "drawable", context.packageName)
    }

    if (id != 0) {
        setImageResource(id)
    } else {
        // ★ IMAGEN AQUÍ: se usa cuando el producto no tiene foto o el nombre no existe
        setImageResource(R.drawable.img_producto_default)
    }
}
