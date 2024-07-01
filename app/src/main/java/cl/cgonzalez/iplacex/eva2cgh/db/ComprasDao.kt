package cl.cgonzalez.iplacex.eva2cgh.db

import android.content.ContentValues
import cl.cgonzalez.iplacex.eva2cgh.db.DBCarrito.TablaCompras

/**
 * Esta clase representa un Data Access Object (DAO) para interactuar con la tabla de compras en la base de datos.
 * Maneja operaciones como inserción, actualización, eliminación y consulta de compras.
 */
class ComprasDao(val db: DBHelper) {

    /**
     * Retorna todas las compras almacenadas en la base de datos.
     */
    fun finAll(): List<Compra> {
        // Realiza una consulta a la base de datos.
        val cursor = db.readableDatabase.query(
            TablaCompras.TABLA_LISTA_COMPRAS,
            null,
            "",
            null,
            null,
            null,
            "${TablaCompras.COLUMNA_COMPRADO} ASC"
        )
        // Lista mutable para almacenar las compras.
        val lista = mutableListOf<Compra>()
        // Itera sobre los resultados de la consulta.
        while (cursor.moveToNext()) {
            // Obtiene los valores de cada columna.
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(TablaCompras.COLUMNA_ID))
            val producto = cursor.getString(cursor.getColumnIndexOrThrow(TablaCompras.COLUMNA_PRODUCTO))
            val comprado = cursor.getInt(cursor.getColumnIndexOrThrow(TablaCompras.COLUMNA_COMPRADO))
            // Crea un objeto Compra con los valores obtenidos.
            val compra = Compra(id, producto, comprado > 0)
            // Agrega la compra a la lista.
            lista.add(compra)
        }
        // Cierra el cursor.
        cursor.close()
        // Devuelve la lista de compras.
        return lista
    }

    /**
     * Inserta una nueva compra en la base de datos.
     */
    fun insertar(compra: Compra): Long {
        // ContentValues con los valores de la compra.
        val valores = ContentValues().apply {
            put(TablaCompras.COLUMNA_PRODUCTO, compra.producto)
            put(TablaCompras.COLUMNA_COMPRADO, compra.comprado)
        }
        // Inserta los valores en la base de datos y devuelve el ID de la fila insertada.
        return db.writableDatabase.insert(TablaCompras.TABLA_LISTA_COMPRAS, null, valores)
    }

    /**
     * Elimina una compra de la base de datos por su ID.
     */
    fun eliminar(id: Int) {
        db.writableDatabase.delete(TablaCompras.TABLA_LISTA_COMPRAS, "${TablaCompras.COLUMNA_ID} = ?", arrayOf(id.toString()))
    }

    /**
     * Elimina todas las compras de la base de datos.
     */
    fun eliminarTodos() {
        db.writableDatabase.delete(TablaCompras.TABLA_LISTA_COMPRAS, null, null)
    }

    /**
     * Actualiza una compra existente en la base de datos.
     */
    fun actualizar(compra: Compra) {
        val contentValues = ContentValues()
        contentValues.put(TablaCompras.COLUMNA_PRODUCTO, compra.producto)
        contentValues.put(TablaCompras.COLUMNA_COMPRADO, if (compra.comprado) 1 else 0)

        db.writableDatabase.update(TablaCompras.TABLA_LISTA_COMPRAS, contentValues, "${TablaCompras.COLUMNA_ID} = ?", arrayOf(compra.id.toString()))
    }
}
