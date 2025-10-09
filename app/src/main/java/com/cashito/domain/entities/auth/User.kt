package com.cashito.domain.entities.auth

/**
 * Representa a un usuario dentro del dominio de la aplicación.
 *
 * @property uid Identificador único del usuario (proveniente del sistema de autenticación).
 * @property email Correo electrónico del usuario, puede ser nulo.
 * @property displayName Nombre para mostrar del usuario, puede ser nulo.
 */
data class User(
    val uid: String,
    val email: String?,
    val displayName: String?
)
