package com.cashito.data.datasources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa las credenciales del usuario (email y contraseña)
 * almacenadas localmente en la base de datos Room.
 *
 * Se utiliza una única fila con un ID fijo para guardar las credenciales
 * del usuario que ha seleccionado "Mantener sesión iniciada".
 */
@Entity(tableName = "user_credentials")
data class UserCredentialEntity(
    /**
     * Clave primaria fija. Solo habrá una entrada en esta tabla.
     */
    @PrimaryKey
    val id: Int = 1,

    /**
     * El email del usuario.
     */
    val email: String,

    /**
     * La contraseña del usuario.
     * NOTA: En un escenario real, esta contraseña debería estar
     * encriptada antes de ser almacenada.
     */
    val password: String
)
