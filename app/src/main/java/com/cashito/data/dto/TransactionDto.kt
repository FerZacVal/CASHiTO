package com.cashito.data.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Data Transfer Object (DTO) que representa la estructura de un documento
 * en la colección \'Transacciones\' de Firestore.
 * Este objeto es el intermediario directo entre la base de datos y la capa de datos de la aplicación.
 * Cada propiedad corresponde a un campo en el documento de Firestore.
 */
data class TransactionDto(

    /**
     * El ID único del documento en Firestore. Se marca con @Exclude para que no se intente
     * escribir este campo al crear o actualizar un documento, ya que Firestore lo genera automáticamente.
     * Se popula manualmente después de leer el documento.
     */
    @get:Exclude var id: String = "",

    /**
     * El ID del usuario al que pertenece esta transacción. Es fundamental para asegurar que cada usuario
     * solo vea sus propias transacciones (reglas de seguridad de Firestore).
     */
    var userId: String = "",

    /**
     * Una descripción textual de la transacción, proporcionada por el usuario.
     * Por ejemplo: "Café en la mañana", "Pago de alquiler", etc.
     */
    val description: String = "",

    /**
     * El monto monetario de la transacción. Puede ser positivo (ingreso) o negativo (gasto),
     * aunque el tipo se define en el campo \'type\'.
     */
    val amount: Double = 0.0,

    /**
     * La fecha y hora en que se registró la transacción. Con @ServerTimestamp, si se envía un \'null\'
     * a Firestore, el servidor de Firebase estampa la hora actual automáticamente. Esto asegura
     * consistencia en las marcas de tiempo, independientemente de la hora del dispositivo del usuario.
     */
    @ServerTimestamp
    val date: Date? = null,

    /**
     * Define el tipo de transacción. Usualmente se usan cadenas como "ingreso" o "gasto".
     * Este campo es crucial para la lógica de negocio, como calcular balances.
     */
    val type: String = "",

    /**
     * El ID del documento de la categoría a la que está asociada esta transacción (si la tiene).
     */
    val categoryId: String = "",

    /**
     * El nombre de la categoría. Se guarda aquí (desnormalización) para evitar tener que hacer una
     * consulta adicional a la colección de categorías solo para mostrar el nombre en una lista de transacciones.
     */
    val categoryName: String = "",

    /**
     * El ícono de la categoría (por ejemplo, un emoji o un identificador de recurso).
     * También es un dato desnormalizado para optimizar las lecturas.
     */
    val categoryIcon: String = "",

    /**
     * El ID de la meta de ahorro a la que está asociada esta transacción. Es anulable (nullable) porque
     * no todas las transacciones son aportes a una meta (pueden ser gastos generales, etc.).
     * Este fue el campo clave que añadimos para nuestra funcionalidad de detalle de metas.
     */
    val goalId: String? = null
)
