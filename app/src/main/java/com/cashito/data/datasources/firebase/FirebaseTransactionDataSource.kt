package com.cashito.data.datasources.firebase

import android.util.Log
import com.cashito.data.dto.TransactionDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    /**
     * Obtiene todos los documentos de transacción, incluyendo su ID, de la subcolección del usuario actual.
     * @return Una lista de TransactionDto, cada uno con su ID de documento asignado.
     */
    suspend fun getTransactions(): List<TransactionDto> {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: User is not authenticated. Cannot get transactions.")
            return emptyList()
        }

        return try {
            val snapshot = firestore.collection("Usuarios")
                .document(userId)
                .collection("Transacciones")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            // Mapear manualmente para incluir el ID del documento
            snapshot.documents.mapNotNull { document ->
                val transaction = document.toObject(TransactionDto::class.java)
                transaction?.id = document.id // Asignar el ID del documento al DTO
                transaction
            }
        } catch (e: Exception) {
            Log.e("FlowDebug", "DataSource: Error getting transactions from Firestore.", e)
            emptyList()
        }
    }
}
