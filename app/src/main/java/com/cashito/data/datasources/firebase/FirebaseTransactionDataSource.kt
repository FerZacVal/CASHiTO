package com.cashito.data.datasources.firebase

import android.util.Log
import com.cashito.data.dto.TransactionDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Se encarga de la comunicación directa con Firestore para escribir y leer datos de transacciones.
 */
class FirebaseTransactionDataSource(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    init {
        Log.d("FlowDebug", "SUCCESS: FirebaseTransactionDataSource instance created.")
    }

    /**
     * Añade un nuevo documento de transacción a la subcolección del usuario actual.
     * @param transactionDto El DTO que representa el documento a guardar.
     */
    suspend fun addTransaction(transactionDto: TransactionDto) {
        Log.d("FlowDebug", "DataSource: addTransaction called. Getting current user.")
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: FATAL - User is not authenticated. Cannot add transaction.")
            throw IllegalStateException("Usuario no autenticado")
        }

        // Asignar el userId al DTO antes de guardarlo
        transactionDto.userId = userId

        Log.d("FlowDebug", "DataSource: Adding document to Firestore for user $userId.")
        firestore.collection("Usuarios").document(userId).collection("Transacciones").add(transactionDto).await()
        Log.d("FlowDebug", "DataSource: Document successfully added to Firestore.")
    }
}
