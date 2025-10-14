package com.cashito.data.datasources.firebase

import android.util.Log
import com.cashito.data.dto.TransactionDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
    fun observeTransactions(): Flow<List<TransactionDto>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: run {
            Log.e("FlowDebug", "DataSource: User is not authenticated. Cannot observe transactions.")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val collectionRef = firestore.collection("Usuarios")
            .document(userId)
            .collection("Transacciones")
            .orderBy("date", Query.Direction.DESCENDING)

        val listenerRegistration: ListenerRegistration = collectionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FlowDebug", "DataSource: Error observing transactions.", e)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val transactions = snapshot.documents.mapNotNull { document ->
                    val transaction = document.toObject(TransactionDto::class.java)
                    transaction?.id = document.id
                    transaction
                }
                trySend(transactions)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose { listenerRegistration.remove() }
    }
}
