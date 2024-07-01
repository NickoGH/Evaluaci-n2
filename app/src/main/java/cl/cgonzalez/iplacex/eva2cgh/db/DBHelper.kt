package cl.cgonzalez.iplacex.eva2cgh.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.cgonzalez.iplacex.eva2cgh.db.DBCarrito.TablaCompras

/**
 * DBHelper es una clase que extiende SQLiteOpenHelper y maneja la creación y actualización de la base de datos.
 */
class DBHelper(contexto: Context) : SQLiteOpenHelper(contexto, DB_NOMBRE, null, DB_VERSION) {

    companion object {
        // Nombre del archivo de la base de datos.
        const val DB_NOMBRE = "compras.db"
        // Versión de la base de datos.
        const val DB_VERSION = 1
        // Sentencia SQL para la creación de la tabla de compras.
        const val SQL_CREACION_TABLAS = """
            CREATE TABLE ${TablaCompras.TABLA_LISTA_COMPRAS}(
            ${TablaCompras.COLUMNA_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${TablaCompras.COLUMNA_PRODUCTO} TEXT,
            ${TablaCompras.COLUMNA_COMPRADO} BOOLEAN
            )
        """
    }

    /**
     * Método llamado cuando la base de datos es creada por primera vez.
     * Este método ejecuta la sentencia SQL para crear la tabla de compras.
     */
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREACION_TABLAS)
    }

    /**
     * Método llamado cuando la base de datos necesita ser actualizada.
     * Este método se utiliza para manejar actualizaciones de la estructura de la base de datos.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}
