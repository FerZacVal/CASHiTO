package com.cashito.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Clase principal de la base de datos de Room para la aplicación.
 *
 * Define las entidades que contiene la base de datos y proporciona acceso a los DAOs.
 */
@Database(
    entities = [UserCredentialEntity::class],
    version = 1, // La versión inicial de la base de datos.
    exportSchema = true // Se recomienda exportar el esquema para control de versiones.
)
abstract class CashitoDatabase : RoomDatabase() {

    /**
     * Proporciona acceso al DAO de credenciales de usuario.
     */
    abstract fun userCredentialsDao(): UserCredentialsDao

    companion object {
        /**
         * Nombre del archivo de la base de datos.
         */
        const val DATABASE_NAME = "cashito-db"
    }
}
