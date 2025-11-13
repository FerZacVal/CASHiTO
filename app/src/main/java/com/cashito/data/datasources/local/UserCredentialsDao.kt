package com.cashito.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO (Data Access Object) para interactuar con la entidad de credenciales de usuario.
 */
@Dao
interface UserCredentialsDao {

    /**
     * Obtiene las credenciales guardadas. Como solo habrá una fila, esta consulta
     * devuelve la única entidad o null si la tabla está vacía.
     */
    @Query("SELECT * FROM user_credentials LIMIT 1")
    suspend fun getCredentials(): UserCredentialEntity?

    /**
     * Inserta o actualiza las credenciales. Si ya existe una entidad con el mismo ID (1),
     * la reemplaza. Esto nos sirve tanto para crear como para actualizar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(credential: UserCredentialEntity)

    /**
     * Elimina todas las credenciales de la tabla. Útil para el logout.
     */
    @Query("DELETE FROM user_credentials")
    suspend fun clearCredentials()
}
