package com.cashito.domain.entities.settings

/**
 * Representa las configuraciones o preferencias de un usuario.
 * En la base de datos (Firestore), esto corresponde a un mapa (objeto) anidado llamado `configuracion`
 * dentro del documento principal del usuario.
 */
data class UserSettings(
    /**
     * Indica si el usuario desea recibir notificaciones.
     * Mapea desde el campo `notificaciones` en el mapa de Firestore.
     */
    val enableNotifications: Boolean,

    /**
     * Indica si el usuario prefiere el tema oscuro.
     * Mapea desde el campo `temaOscuro` en el mapa de Firestore.
     */
    val useDarkMode: Boolean,

    /**
     * Límite de gasto mensual que el usuario puede configurar (opcional).
     * Mapea desde el campo `limiteGasto` en el mapa de Firestore.
     */
    val spendingLimit: Double? = null,

    /**
     * Indica si el usuario ha habilitado la autenticación biométrica.
     * Mapea desde el campo `autenticacionBiometrica` en el mapa de Firestore.
     */
    val biometricAuthEnabled: Boolean
)
