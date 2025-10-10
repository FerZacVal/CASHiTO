package com.cashito.data.mappers

import com.cashito.domain.entities.auth.User
import com.google.firebase.auth.FirebaseUser

/**
 * Convierte un objeto de datos de Firebase (FirebaseUser) a un objeto de dominio (User).
 * Esta es una parte crucial para mantener la capa de dominio independiente de la capa de datos.
 */
fun FirebaseUser.toDomain(): User {
    return User(
        uid = this.uid,
        email = this.email,
        displayName = this.displayName
    )
}
