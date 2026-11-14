package com.example.lospolloshermanos

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

/**
 * Guarda los "oyentes" de Firebase para poder apagarlos al cerrar la pantalla
 * (así no se gasta batería ni se producen errores).
 *
 * Uso:  gestor.escuchar(referencia) { snapshot -> ... }
 *       override fun onDestroy() { gestor.detenerTodas() }
 */
class GestorEscuchas(private val contexto: Context) {

    private val registro = mutableListOf<Pair<DatabaseReference, ValueEventListener>>()

    fun escuchar(ref: DatabaseReference, alCambiar: (DataSnapshot) -> Unit) {
        val oyente = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = alCambiar(snapshot)

            override fun onCancelled(error: DatabaseError) {
                contexto.toast(R.string.err_generico, error.message)
            }
        }
        ref.addValueEventListener(oyente)
        registro.add(ref to oyente)
    }

    fun detenerTodas() {
        registro.forEach { (ref, oyente) -> ref.removeEventListener(oyente) }
        registro.clear()
    }
}
